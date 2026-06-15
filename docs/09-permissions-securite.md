# 09 — Permissions & sécurité

## 9.1 Permissions déclarées (`AndroidManifest.xml`)

| Permission | Type | Usage |
|------------|------|-------|
| `INTERNET` | Normale | Appels Retrofit, Firebase, tuiles Maps |
| `CALL_PHONE` | Dangereuse | Appel direct du 15 lors du SOS |
| `SEND_SMS` | Dangereuse | (déclarée) envoi de SMS d'urgence |
| `CAMERA` | Dangereuse | Photo de la situation |
| `ACCESS_FINE_LOCATION` | Dangereuse | Position GPS précise |
| `ACCESS_COARSE_LOCATION` | Dangereuse | Position approximative (repli) |
| `POST_NOTIFICATIONS` | Dangereuse (API 33+) | Affichage des notifications |

## 9.2 Permissions à l'exécution (runtime)

Depuis Android 6 (API 23), les permissions **dangereuses** doivent être demandées à l'exécution. Le pattern utilisé :

```java
if (ContextCompat.checkSelfPermission(this, PERMISSION) == PackageManager.PERMISSION_GRANTED) {
    action();                                      // déjà accordée → on agit
} else {
    ActivityCompat.requestPermissions(this, new String[]{PERMISSION}, CODE);
}
// puis dans onRequestPermissionsResult :
//   si accordée → relancer action()
//   sinon → Toast d'explication
```

| Permission | Demandée dans | Déclencheur |
|------------|---------------|-------------|
| `POST_NOTIFICATIONS` | `MainActivity.onCreate()` | À l'entrée dans l'app (Android 13+) |
| `ACCESS_FINE_LOCATION` | `HomeFragment`, `NouvelleAlerteActivity` | Affichage position / alerte avec GPS |
| `CAMERA` | `NouvelleAlerteActivity` | Avant d'ouvrir la caméra |
| `CALL_PHONE` | (SOS) | Avant un appel direct |

> Pour les appels « simples » (cartes Appeler, contacts), on utilise volontairement `ACTION_DIAL` qui **n'exige aucune permission** (l'utilisateur confirme dans le composeur) — plus sûr et sans friction.

## 9.3 Authentification par téléphone (OTP)

### Principe
L'app vérifie l'identité par **One-Time Password (OTP)** envoyé par SMS, via **Firebase Phone Authentication**. La logique est encapsulée dans `AuthHelper` pour découpler l'UI du fournisseur.

### Repli simulé (« mock »)
Pour que l'app **compile et tourne sans configuration Firebase**, `AuthHelper` détecte l'absence de Firebase et bascule automatiquement en mode simulé :

```java
public static boolean firebaseDisponible(Context ctx) {
    try { FirebaseApp.getInstance(); return true; }
    catch (IllegalStateException e) { return false; }   // pas de google-services.json → mock
}
```

| Mode | Comportement |
|------|--------------|
| **Firebase configuré** | Vrai SMS envoyé, `PhoneAuthProvider.verifyPhoneNumber`, connexion par `PhoneAuthCredential` |
| **Mock (par défaut)** | « Code envoyé » immédiat ; **tout code à 6 chiffres** est accepté |

### Activer le vrai SMS OTP
1. Créer un projet sur [console.firebase.google.com](https://console.firebase.google.com).
2. Ajouter une app Android, package `com.example.emergencyapp`.
3. Télécharger `google-services.json` → le placer dans `app/`.
4. Activer **Authentication → Phone**.
5. Ajouter l'empreinte **SHA-1** (debug) au projet Firebase.
6. Appliquer le plugin Gradle `com.google.gms.google-services`.

## 9.4 Bonnes pratiques de sécurité appliquées

- **Pas de base distante en dur** : aucune connexion directe à une BDD (pas d'identifiants dans l'APK).
- **Permissions minimales et contextuelles** : demandées au moment de l'usage, avec explication en cas de refus.
- **`ACTION_DIAL` plutôt que `ACTION_CALL`** quand l'appel n'a pas besoin d'être automatique → l'utilisateur garde le contrôle.
- **Données sensibles locales** : le profil médical reste sur l'appareil (SharedPreferences / SQLite).
- **Clé API Maps** : restreinte par empreinte SHA-1 + nom de package côté Google Cloud (recommandé).

## 9.5 Limites connues / pistes d'amélioration

- La clé Maps est en clair dans le manifest (pratique courante, mais à restreindre côté console Google).
- `SEND_SMS` est déclarée mais l'envoi passe par `ACTION_SENDTO` (l'utilisateur valide l'envoi) — choix volontaire pour éviter l'envoi silencieux.
- Le chiffrement des données locales (SQLCipher) pourrait être ajouté pour un usage en production.
