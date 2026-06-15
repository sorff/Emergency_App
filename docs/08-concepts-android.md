# 08 — Concepts Android implémentés

Ce document est **le plus important pour une soutenance** : il prouve, pour chaque concept Android attendu, **où** et **comment** il est implémenté dans le code.

## 8.1 Tableau de synthèse

| # | Concept | Implémenté | Fichier(s) clé(s) |
|---|---------|:---------:|-------------------|
| 1 | ListView / **RecyclerView** | ✅ | `NotificationAdapter`, `AlertsFragment` |
| 2 | Widget (App Widget) | ✅ | `SosWidgetProvider`, `widget_sos.xml`, `sos_widget_info.xml` |
| 3 | SharedPreferences | ✅ | `SharedPrefsHelper`, `SettingsActivity` |
| 4 | SQLite | ✅ | `DatabaseHelper` |
| 5 | Fragments | ✅ | `Home/Contacts/Map/AlertsFragment`, `MainActivity` |
| 6 | Intents explicites | ✅ | navigation (toutes les activités/fragments) |
| 6 | Intents implicites | ✅ | appels, SMS, caméra |
| 7 | AsyncTask | ✅ | `AddContactActivity`, `NouvelleAlerteActivity`, `HomeFragment` |
| 8 | Caméra | ✅ | `NouvelleAlerteActivity` |
| 8 | Appels téléphoniques | ✅ | `HomeFragment`, `MapFragment` |
| 8 | SMS | ✅ | `HomeFragment` |
| 9 | Google Maps | ✅ | `MapFragment` |
| 10 | Retrofit | ✅ | `RetrofitClient`, `ApiService`, `NouvelleAlerteActivity` |
| 11 | Notifications | ✅ | `NotificationHelper` |

## 8.2 Détail par concept

### 1 — RecyclerView (et ListView/BaseAdapter)
- **RecyclerView** : `AlertsFragment` affiche les notifications/alertes via `NotificationAdapter` (un `RecyclerView.Adapter` avec **pattern ViewHolder** : `onCreateViewHolder`, `onBindViewHolder`, `getItemCount`).
- Le `LinearLayoutManager` gère la disposition verticale.

### 2 — Widget d'écran d'accueil
- `SosWidgetProvider` étend `AppWidgetProvider`. Dans `onUpdate()`, il construit un `RemoteViews` (le widget ne peut pas manipuler des `View` classiques) et branche un `PendingIntent` qui ouvre l'app au tap.
- Déclaré dans le manifest via un `<receiver>` + `<meta-data android:resource="@xml/sos_widget_info"/>`.
- *Limite assumée* : un widget de launcher ne supporte pas le geste « appui maintenu » ; le tap ouvre l'écran SOS.

### 3 — SharedPreferences
- `SharedPrefsHelper` encapsule toutes les lectures/écritures (profil + préférences). Voir [06](06-donnees.md).

### 4 — SQLite
- `DatabaseHelper` (deux tables, CRUD complet). Voir [06](06-donnees.md).

### 5 — Fragments
- `MainActivity` héberge 4 fragments échangés par `FragmentTransaction.replace()`.
- `MapFragment` utilise un **fragment imbriqué** (`SupportMapFragment`) via `getChildFragmentManager()`.

### 6 — Intents
- **Explicites** : `new Intent(context, XxxActivity.class)` pour toute la navigation.
- **Implicites** : `ACTION_DIAL`/`ACTION_CALL` (téléphone), `ACTION_SENDTO` (SMS), `ACTION_IMAGE_CAPTURE` (caméra) — délèguent à d'autres apps du système.

### 7 — AsyncTask
- Pattern `doInBackground()` (opération SQLite) / `onPostExecute()` (mise à jour UI). Voir [06](06-donnees.md).

### 8 — Capacités système (caméra, appels, SMS)
- **Caméra** : `NouvelleAlerteActivity` lance `MediaStore.ACTION_IMAGE_CAPTURE` via `startActivityForResult`, récupère le `Bitmap` dans `onActivityResult`.
- **Appels** : `Intent(ACTION_DIAL, "tel:15")` (composeur, sans permission) et `ACTION_CALL` (appel direct, avec permission `CALL_PHONE`) pour le SOS.
- **SMS** : `Intent(ACTION_SENDTO, "smsto:<num>")` + `putExtra("sms_body", message)` pour chaque contact.
Voir [10](10-capacites-systeme.md).

### 9 — Google Maps
- `MapFragment implements OnMapReadyCallback`, `getMapAsync(this)`, marqueurs et caméra dans `onMapReady()`. Voir [10](10-capacites-systeme.md).

### 10 — Retrofit
- Singleton + interface + `enqueue` + offline-first. Voir [07](07-reseau-retrofit.md).

### 11 — Notifications
- `NotificationHelper` : canal `emergency_channel` (Android 8+), `NotificationCompat.Builder`, priorité haute, icône dédiée. Voir [10](10-capacites-systeme.md).

## 8.3 Bonus (non listés mais présents)

- **Material Design 3** : MaterialButton, MaterialCardView, TextInputLayout, BottomNavigationView, FloatingActionButton, Switch, Spinner.
- **Géocodage inverse** (`Geocoder`) : conversion coordonnées → adresse lisible sur l'accueil.
- **Authentification téléphone** (Firebase Phone Auth) avec repli simulé.
- **Animations** (halo SOS).
- **Vector Drawables** pour toutes les icônes.
