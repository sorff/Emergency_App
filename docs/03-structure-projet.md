# 03 — Structure du projet

## 3.1 Arborescence des packages Java

```
com.example.emergencyapp/
├── MyApplication.java              # Application : crée le canal de notif + précharge Maps
│
├── activities/
│   ├── SplashActivity.java         # Écran de démarrage (2 s) + routage onboarding/app
│   ├── VerifyIdentityActivity.java # Saisie du numéro → envoi du code OTP
│   ├── OtpActivity.java            # Saisie du code à 6 chiffres
│   ├── ProfileSetupActivity.java   # Profil médical (nom, tél, groupe sanguin, conditions)
│   ├── MainActivity.java           # Hôte BottomNavigationView + 4 fragments
│   ├── AddContactActivity.java     # Ajout d'un contact d'urgence
│   ├── NouvelleAlerteActivity.java # Création d'alerte (type, desc, GPS, photo, envoi)
│   └── SettingsActivity.java       # Réglages : profil, préférences, zone de danger
│
├── fragments/
│   ├── HomeFragment.java           # Tableau de bord SOS (appui 3 s, appels, position)
│   ├── ContactsFragment.java       # Liste des contacts + recherche
│   ├── MapFragment.java            # Carte Google Maps + services proches
│   └── AlertsFragment.java         # Notifications + historique d'alertes (RecyclerView)
│
├── adapters/
│   └── NotificationAdapter.java    # RecyclerView.Adapter des notifications/alertes
│
├── database/
│   └── DatabaseHelper.java         # SQLiteOpenHelper : tables alertes + contacts
│
├── models/
│   ├── Alerte.java                 # Modèle d'une alerte
│   ├── Contact.java                # Modèle d'un contact d'urgence
│   ├── AlerteApi.java              # POJO envoyé/reçu via Retrofit
│   └── NotifItem.java              # Élément de notification (RecyclerView)
│
├── network/
│   ├── ApiService.java             # Interface Retrofit (@POST, @GET)
│   └── RetrofitClient.java         # Singleton Retrofit (baseUrl, Gson)
│
├── notifications/
│   └── NotificationHelper.java     # Création canal + envoi de notifications
│
├── utils/
│   ├── SharedPrefsHelper.java      # Lecture/écriture des préférences
│   └── AuthHelper.java             # OTP Firebase + repli simulé
│
└── widget/
    └── SosWidgetProvider.java      # Widget d'écran d'accueil (tap → ouvre l'app)
```

## 3.2 Rôle détaillé de chaque classe

### Application
- **`MyApplication`** : étend `Application`. Dans `onCreate()` : crée le canal de notification (obligatoire Android 8+) et **précharge le module Google Maps** sur un thread d'arrière-plan pour accélérer l'ouverture de la carte. Déclarée via `android:name=".MyApplication"` dans le manifest.

### Activities
- **`SplashActivity`** : affiche le logo 2 s, puis route vers `MainActivity` (si un profil existe en SharedPreferences) ou `VerifyIdentityActivity` (sinon).
- **`VerifyIdentityActivity`** : valide le numéro, le normalise (`+212…`), appelle `AuthHelper.envoyerCode()`, puis ouvre `OtpActivity`.
- **`OtpActivity`** : 6 cases auto-avance, vérifie le code via `AuthHelper.verifierCode()`, puis ouvre `ProfileSetupActivity`.
- **`ProfileSetupActivity`** : nom, téléphone, **grille de 8 groupes sanguins** (sélection unique), conditions médicales → SharedPreferences → `MainActivity`.
- **`MainActivity`** : hôte de navigation. Gère le `BottomNavigationView`, échange les 4 fragments, demande la permission `POST_NOTIFICATIONS` (Android 13+).
- **`AddContactActivity`** : formulaire (nom, mobile, relation via `Spinner`, répondant principal via `Switch`), insertion en SQLite via `AsyncTask`.
- **`NouvelleAlerteActivity`** : sélection du type (chips), description, switch GPS, photo (caméra), envoi Retrofit + sauvegarde SQLite + notification de prise en charge différée.
- **`SettingsActivity`** : édition du profil (dont groupe sanguin), préférences (notifications, localisation), zone de danger (vider l'historique, se déconnecter).

### Fragments
- **`HomeFragment`** : header AidGuard, carte de statut, **bouton SOS à appui maintenu 3 s** (`OnTouchListener` + `Handler`), cartes Appeler Police/Secours, position géocodée (`Geocoder`).
- **`ContactsFragment`** : header, cartes de statut (niveau de sécurité), barre de recherche filtrante, liste de contacts (cartes inflatées), suppression par appui long.
- **`MapFragment`** : `SupportMapFragment` ajouté **par code et en différé** (anti-ANR), marqueurs utilisateur/hôpitaux/police, liste des services proches.
- **`AlertsFragment`** : carte de statut système, compteur de contacts, **RecyclerView** des notifications (mock + alertes réelles), FAB → `NouvelleAlerteActivity`.

### Données / Réseau / Utils
- **`DatabaseHelper`** : voir [06](06-donnees.md).
- **`ApiService` / `RetrofitClient`** : voir [07](07-reseau-retrofit.md).
- **`SharedPrefsHelper`** : méthodes statiques pour nom, téléphone, se-rappeler, notifications, localisation, groupe sanguin, conditions.
- **`AuthHelper`** : abstraction OTP (Firebase réel ou simulé).
- **`NotificationHelper`** : canal `emergency_channel`, `sendNotification(titre, message)`.
- **`SosWidgetProvider`** : `AppWidgetProvider`, `PendingIntent` vers `SplashActivity`.

## 3.3 Ressources (`res/`)

| Dossier | Contenu |
|---------|---------|
| `layout/` | 18 fichiers XML (écrans + items de liste + dialog + widget) |
| `drawable/` | 47 fichiers (formes, dégradés, icônes vectorielles, fonds) |
| `values/` | `colors.xml`, `dimens.xml`, `strings.xml`, `styles.xml`, `themes.xml` |
| `menu/` | `bottom_nav_menu.xml` (4 onglets) |
| `anim/` | `pulse.xml` (animation du halo SOS) |
| `xml/` | `sos_widget_info.xml`, règles de sauvegarde |
| `mipmap-*` | Icônes du launcher (PNG générés + adaptive icon v26) |
| `color/` | `nav_item_color.xml` (sélecteur d'état de la nav) |

## 3.4 Fichiers de configuration

- **`AndroidManifest.xml`** : permissions, déclaration des activités, clé API Maps, widget, application.
- **`app/build.gradle.kts`** : SDK, dépendances.
- **`gradle/libs.versions.toml`** : catalogue centralisé des versions.
