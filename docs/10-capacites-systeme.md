# 10 — Capacités système

## 10.1 Appels téléphoniques

Deux mécanismes selon le contexte :

| Cas | Intent | Permission |
|-----|--------|------------|
| Bouton SOS (appel automatique du 15) | `ACTION_CALL` | `CALL_PHONE` |
| Cartes « Appeler » (services, contacts) | `ACTION_DIAL` | aucune |

```java
// Composeur (l'utilisateur valide) — sans permission
startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:15")));
```

Numéros utilisés : **15** (SAMU), **17** (Police), + numéros mockés des services.

## 10.2 SMS d'urgence

Lors du SOS, un SMS pré-rempli est préparé pour **chaque contact** enregistré :

```java
Intent sms = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + contact.getTelephone()));
sms.putExtra("sms_body", "ALERTE URGENCE - " + nomUtilisateur + " a besoin d'aide");
startActivity(sms);
```

Le contenu provient de `R.string.message_sos`. Les contacts sont lus depuis SQLite **via AsyncTask** (hors thread principal) dans `HomeFragment`.

## 10.3 Caméra (photo de la situation)

Dans `NouvelleAlerteActivity` :

```java
private void ouvrirCamera() {
    if (permission CAMERA accordée) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CODE_CAMERA);
    } else { requestPermissions(...); }
}

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == CODE_CAMERA && resultCode == RESULT_OK && data != null) {
        Bitmap photo = (Bitmap) data.getExtras().get("data");
        ivPhoto.setImageBitmap(photo);
        ivPhoto.setVisibility(View.VISIBLE);
    }
}
```

La zone photo bascule entre « Prendre une photo » et l'aperçu (avec bouton de suppression).

## 10.4 Géolocalisation

Via `LocationManager` (sans dépendance à Google Play pour la lecture de base) :

```java
LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
Location loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
if (loc == null) loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER); // repli
// → latitude/longitude stockées dans l'objet Alerte avant insertion
```

- **`NouvelleAlerteActivity`** : si le switch GPS est activé, la position est jointe à l'alerte.
- **`HomeFragment`** : la position est convertie en **adresse lisible** par `Geocoder` (géocodage inverse) et affichée dans la carte « Votre position ».

## 10.5 Google Maps

`MapFragment implements OnMapReadyCallback`.

```java
// Le fragment Maps est ajouté PAR CODE et EN DIFFÉRÉ (post) pour éviter l'ANR au lancement
findViewById(R.id.mapContainer).post(this::initialiserCarte);

@Override public void onMapReady(GoogleMap map) {
    LatLng centre = new LatLng(33.9716, -6.8498);      // Rabat
    map.moveCamera(CameraUpdateFactory.newLatLngZoom(centre, 14f));
    map.getUiSettings().setZoomControlsEnabled(true);
    map.addMarker(... "Moi" ...);                       // utilisateur (rouge)
    // + marqueurs hôpitaux (bleus) et police (azur/orange)
}
```

### Données mockées (services proches)
| Service | Coordonnées | Distance |
|---------|-------------|----------|
| CHU Urgences Rabat | 33.9890, -6.8544 | 0.8 km |
| Clinique Agdal | 33.9751, -6.8650 | 1.1 km |
| Commissariat Central | 33.9926, -6.8543 | 1.2 km |
| SAMU Rabat | 33.9751, -6.8495 | 1.5 km |

### Anti-ANR (point technique important)
Le `<fragment>` Maps inséré directement en XML provoquait un **ANR au lancement** (initialisation bloquante de Maps sur le thread principal pendant `setContentView`). Solution : conteneur `FrameLayout` + ajout du `SupportMapFragment` **par code dans un `post()`**, donc après que l'écran soit interactif. De plus, `MyApplication` **précharge** le SDK Maps en arrière-plan.

### Sans clé API
La clé est dans le manifest (`com.google.android.geo.API_KEY`). L'initialisation est entourée d'un `try/catch` : sans clé valide, la carte reste vide mais les cartes de services restent utilisables.

## 10.6 Notifications

`NotificationHelper` :

```java
// Canal (obligatoire Android 8+), créé au démarrage par MyApplication
NotificationChannel channel = new NotificationChannel(
    "emergency_channel", "Alertes d'urgence", NotificationManager.IMPORTANCE_HIGH);

// Envoi
NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, CHANNEL_ID)
    .setSmallIcon(R.drawable.ic_notification)   // silhouette bouclier
    .setColor(0xC0392B)                         // teinte rouge AidGuard
    .setContentTitle(titre)
    .setContentText(message)
    .setPriority(NotificationCompat.PRIORITY_HIGH)
    .setAutoCancel(true);
```

### Notifications de prise en charge
Après l'envoi d'une alerte (`NouvelleAlerteActivity`) :
1. Immédiatement : **« Alerte envoyée »** (l'alerte a été transmise aux secours).
2. Après **7 secondes** (`Handler.postDelayed`) : **« Prise en charge ✓ »** (simulation que les secours ont accepté).

Le SOS de l'accueil envoie aussi une notification « Alerte SOS déclenchée ».

> Sur **Android 13+**, l'affichage nécessite la permission runtime `POST_NOTIFICATIONS`, demandée dans `MainActivity`.
