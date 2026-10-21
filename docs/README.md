# Documentation — AidGuard (EmergencyApp)

> Application Android d'assistance d'urgence (panne, accident, secours).



## Sommaire

| # | Document | Contenu |
|---|----------|---------|
| 01 | [Présentation](01-presentation.md) | Vision, contexte, fonctionnalités, public cible |
| 02 | [Architecture technique](02-architecture-technique.md) | Stack, patterns, schéma d'architecture |
| 03 | [Structure du projet](03-structure-projet.md) | Chaque fichier expliqué |
| 04 | [Design system](04-design-system.md) | Couleurs, dimensions, thèmes, icônes |
| 05 | [Écrans & navigation](05-ecrans-navigation.md) | Tous les écrans + diagramme de flux |
| 06 | [Couche données](06-donnees.md) | SQLite, SharedPreferences, modèles |
| 07 | [Couche réseau](07-reseau-retrofit.md) | Retrofit, API, mode offline |
| 08 | [Concepts Android](08-concepts-android.md) | Les 11 concepts mappés au code |
| 09 | [Permissions & sécurité](09-permissions-securite.md) | Permissions runtime, auth Firebase |
| 10 | [Capacités système](10-capacites-systeme.md) | Notifications, Maps, caméra, appels, SMS, widget |
| 11 | [Build & exécution](11-build-execution.md) | Compiler, lancer, configurer Firebase |
| 12 | [FAQ de soutenance](12-faq-soutenance.md) | Questions/réponses d'expert |

## Fiche d'identité rapide

| Élément | Valeur |
|---------|--------|
| Nom de l'app | **AidGuard** |
| Package | `com.example.emergencyapp` |
| Langage | **Java** (100 %) |
| SDK minimum | API 26 (Android 8.0) |
| SDK cible / compilation | API 34 (Android 14) |
| Build system | Gradle (Kotlin DSL `.kts` + version catalog) |
| Architecture | Activities + Fragments + `BottomNavigationView` |
| Persistance locale | SQLite + SharedPreferences |
| Réseau | Retrofit 2 (+ Gson) |
| Cartographie | Google Maps SDK |
| Authentification | Firebase Phone Auth (avec repli simulé) |
| UI | Material Design 3 |
