# 06 — Couche données

L'application persiste ses données **localement**, selon deux mécanismes complémentaires :
- **SQLite** pour les données structurées et volumineuses (alertes, contacts).
- **SharedPreferences** pour les petites valeurs clé/valeur (profil, préférences).

## 6.1 Base de données SQLite (`DatabaseHelper`)

`DatabaseHelper` étend `SQLiteOpenHelper`.

```
DB_NAME    = "emergency.db"
DB_VERSION = 2
```

### Table `alertes`
| Colonne | Type | Description |
|---------|------|-------------|
| `id` | INTEGER PRIMARY KEY AUTOINCREMENT | Identifiant |
| `type` | TEXT | Panne / Accident / Médical / Incendie / Autre |
| `description` | TEXT | Détail de la situation |
| `latitude` | REAL | Latitude GPS |
| `longitude` | REAL | Longitude GPS |
| `photo_path` | TEXT | Chemin de la photo (optionnel) |
| `statut` | TEXT | `EN_COURS` ou `RÉSOLU` |
| `timestamp` | INTEGER | Date/heure (millisecondes) |

### Table `contacts_urgence`
| Colonne | Type | Description |
|---------|------|-------------|
| `id` | INTEGER PRIMARY KEY AUTOINCREMENT | Identifiant |
| `nom` | TEXT | Nom du contact |
| `telephone` | TEXT | Numéro |
| `relation` | TEXT | Famille, Ami(e), Conjoint(e)… |
| `primary_responder` | INTEGER (0/1) | Répondant principal (cible SMS prioritaire) |

> La version est passée de 1 à **2** lors de l'ajout des colonnes `relation` et `primary_responder` ; `onUpgrade()` recrée les tables.

### Méthodes publiques
| Méthode | Rôle |
|---------|------|
| `insererAlerte(Alerte)` | Insère une alerte |
| `getAllAlertes()` | Retourne toutes les alertes (tri timestamp décroissant) |
| `supprimerAlerte(int)` | Supprime une alerte |
| `changerStatut(int, String)` | Met à jour le statut |
| `supprimerToutesAlertes()` | Vide l'historique |
| `insererContact(Contact)` | Insère un contact |
| `getAllContacts()` | Retourne tous les contacts (tri par nom) |
| `supprimerContact(int)` | Supprime un contact |

### Règles SQLite respectées
- `getWritableDatabase()` / `getReadableDatabase()` **jamais** appelés dans `onCreate()` du helper.
- Lectures via `rawQuery` + `Cursor`, avec `cursor.close()` puis `db.close()` systématiques.
- Écritures via `ContentValues` + `insert/update/delete`.

## 6.2 Accès asynchrone (AsyncTask)

Les opérations SQLite « lourdes » sont exécutées **hors du thread principal** via `AsyncTask` pour ne pas bloquer l'UI (et éviter les ANR) :

```java
new AsyncTask<Void, Void, ArrayList<Contact>>() {
    @Override protected ArrayList<Contact> doInBackground(Void... v) {
        return db.getAllContacts();          // thread d'arrière-plan
    }
    @Override protected void onPostExecute(ArrayList<Contact> result) {
        // mise à jour de l'UI sur le thread principal
    }
}.execute();
```

Utilisé dans : `AddContactActivity` (insertion), `NouvelleAlerteActivity` (insertion), `HomeFragment` (lecture contacts pour SMS).

## 6.3 SharedPreferences (`SharedPrefsHelper`)

Fichier : **`EmergencyPrefs`**. Méthodes **statiques** (pas d'instanciation) :

| Clé | Type | Getter / Setter |
|-----|------|-----------------|
| `nom` | String | `getNom` / `saveNom` |
| `telephone` | String | `getTelephone` / `saveTelephone` |
| `se_rappeler` | boolean | `isSeRappeler` / `saveSeRappeler` |
| `notifications` | boolean | `isNotificationsActive` / `saveNotifications` |
| `localisation` | boolean | `isLocalisationActive` / `saveLocalisation` |
| `blood_type` | String | `getBloodType` / `saveBloodType` |
| `conditions` | String | `getConditions` / `saveConditions` |

**Rôle clé** : la présence d'un `nom` + `telephone` en SharedPreferences sert d'indicateur « profil configuré » → c'est ce qui permet à `SplashActivity` de sauter l'onboarding et de **rester connecté** entre deux lancements.

## 6.4 Modèles (`models/`)

- **`Alerte`** : reflète une ligne de la table `alertes` (id, type, description, latitude, longitude, photoPath, statut, timestamp) avec getters/setters.
- **`Contact`** : id, nom, telephone, relation, primaryResponder.
- **`AlerteApi`** : POJO pour Retrofit (`id`, `title` = type, `body` = description, `userId` = 1). Sérialisé/désérialisé par Gson.
- **`NotifItem`** : titre, corps, temps, type (INFO / ALERTE / PRISE_EN_CHARGE) pour le `RecyclerView` des notifications.

## 6.5 Pourquoi pas MySQL ?

Question fréquente : **une app Android ne se connecte jamais directement à MySQL**. Ce serait une faille de sécurité (identifiants de la base embarqués dans l'APK). Le stockage embarqué standard est **SQLite**. Pour un vrai backend MySQL, il faudrait un **serveur intermédiaire** (PHP/Laravel, Node.js, Spring Boot…) exposant une API REST, que l'app consommerait via Retrofit (déjà en place ici, branché sur une API de test).
