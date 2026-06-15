# 02 — Architecture technique

## 2.1 Stack technologique

| Couche | Technologie | Rôle |
|--------|-------------|------|
| Langage | **Java** | Toute la logique métier |
| UI | **Material Design 3** (`com.google.android.material`) | Composants : MaterialButton, MaterialCardView, TextInputLayout, BottomNavigationView, FloatingActionButton |
| Navigation | **Activities + Fragments + BottomNavigationView** | 1 activité hôte, 4 fragments |
| Listes | **ListView/BaseAdapter** et **RecyclerView** | Affichage de collections |
| Asynchrone | **AsyncTask** | Opérations SQLite hors thread principal |
| Persistance locale | **SQLite** (`SQLiteOpenHelper`) + **SharedPreferences** | Alertes, contacts, profil, préférences |
| Réseau | **Retrofit 2** + **Gson** | Appels HTTP REST |
| Cartographie | **Google Maps SDK** (`play-services-maps`) + `play-services-location` | Carte + marqueurs + position |
| Authentification | **Firebase Auth** (Phone) | OTP par SMS |
| Notifications | **NotificationCompat** + canal Android 8+ | Alertes locales |
| Build | **Gradle** (Kotlin DSL, version catalog `libs.versions.toml`) | Compilation, dépendances |

## 2.2 Contraintes d'architecture (choix assumés)

Le projet suit volontairement une architecture **classique et explicite**, sans frameworks récents, pour démontrer la maîtrise des fondamentaux Android :

- ❌ **Pas de ViewModel / LiveData** → l'état est géré directement dans les Activities/Fragments.
- ❌ **Pas de DataBinding / ViewBinding** → accès aux vues via `findViewById`.
- ❌ **Pas de Coroutines / RxJava** → asynchronisme via `AsyncTask` et callbacks Retrofit (`enqueue`).
- ✅ **Pattern ViewHolder** obligatoire dans les adapters.
- ✅ **Méthodes privées** pour chaque action (pas de logique lourde dans les lambdas).

## 2.3 Schéma d'architecture en couches

```
┌─────────────────────────────────────────────────────────┐
│                    COUCHE PRÉSENTATION                    │
│  Activities (Splash, VerifyIdentity, Otp, ProfileSetup,  │
│   Main, AddContact, NouvelleAlerte, Settings)            │
│  Fragments (Home, Contacts, Map, Alerts)                 │
│  Adapters (NotificationAdapter)                          │
└───────────────┬─────────────────────────┬───────────────┘
                │                         │
        ┌───────▼────────┐       ┌────────▼─────────┐
        │  COUCHE MÉTIER │       │  COUCHE UTILS    │
        │  /helpers      │       │  SharedPrefsHelper│
        │  NotificationH.│       │  AuthHelper       │
        └───────┬────────┘       └──────────────────┘
                │
   ┌────────────┼─────────────────────┐
   │            │                     │
┌──▼─────┐  ┌───▼──────┐      ┌────────▼────────┐
│ DONNÉES│  │ RÉSEAU   │      │  MODÈLES        │
│ SQLite │  │ Retrofit │      │  Alerte, Contact│
│ Prefs  │  │ ApiService│     │  AlerteApi,     │
│        │  │          │      │  NotifItem      │
└────────┘  └──────────┘      └─────────────────┘
```

## 2.4 Principe « offline-first »

Une décision d'architecture clé : **l'application ne dépend jamais du réseau pour fonctionner**. Lors de l'envoi d'une alerte :

1. On tente l'envoi au backend via Retrofit (`enqueue`, asynchrone).
2. **Quel que soit le résultat** (succès `onResponse` ou échec réseau `onFailure`), l'alerte est **toujours sauvegardée en SQLite**.
3. L'utilisateur reçoit un retour (Toast + notification) dans tous les cas.

Ce choix est essentiel pour une app d'urgence : une panne réseau ne doit jamais empêcher l'enregistrement d'une alerte.

## 2.5 Cycle de vie & navigation

- **1 seule activité hôte** (`MainActivity`) contient un `BottomNavigationView` et un conteneur `FrameLayout`. Les 4 onglets sont des **Fragments** échangés via `FragmentTransaction.replace()`.
- Les écrans **hors onglets** (onboarding, ajout de contact, nouvelle alerte, réglages) sont des **Activities** dédiées lancées par `Intent` explicite.
- L'**onboarding** est un enchaînement linéaire d'Activities qui se terminent (`finish()` / `finishAffinity()`) pour ne pas rester dans la pile.

Voir le diagramme complet dans [05 — Écrans & navigation](05-ecrans-navigation.md).

## 2.6 Gestion de la mémoire et des fuites

- Chaque `DatabaseHelper` ouvert dans une Activity/Fragment est fermé dans `onDestroy()`/`onDestroyView()`.
- Les `Cursor` SQLite sont systématiquement fermés (`cursor.close()` + `db.close()`).
- L'`ApiService` Retrofit est créé **une seule fois** (singleton `RetrofitClient`), jamais à chaque clic.
- Le `Handler` du SOS (appui maintenu) retire ses callbacks dans `onDestroyView()` pour éviter les fuites.
