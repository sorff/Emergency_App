# 01 — Présentation

## 1.1 Vision du produit

**AidGuard** est une application mobile Android d'**assistance d'urgence**. Son objectif est de permettre à un utilisateur en situation critique (panne mécanique, accident de la route, urgence médicale, incendie, etc.) de **déclencher une alerte en un geste**, qui :

1. **Appelle automatiquement** les services de secours concernés ;
2. **Envoie un SMS** avec un message d'urgence à ses contacts de confiance ;
3. **Transmet sa géolocalisation** (et éventuellement une photo de la situation) à un backend ;
4. **Affiche une carte** des hôpitaux et postes de police les plus proches ;
5. **Reçoit des notifications** de prise en charge.

Le nom « AidGuard » combine *Aid* (secours) et *Guard* (protection) ; la signature de l'app est « **Votre sécurité, notre priorité** ».

## 1.2 Problème résolu

En situation d'urgence, chaque seconde compte et l'utilisateur est souvent stressé, blessé ou paniqué. Les actions critiques (appeler le 15, prévenir un proche, partager sa position) demandent normalement plusieurs manipulations. AidGuard **centralise et automatise** ces actions derrière un **bouton SOS unique** (appui maintenu 3 secondes pour éviter les déclenchements accidentels), plus un **widget d'écran d'accueil** pour un accès immédiat sans même ouvrir l'app.

## 1.3 Fonctionnalités principales

| Fonctionnalité | Description |
|----------------|-------------|
| **Bouton SOS** | Appui maintenu 3 s → appel des secours + SMS aux contacts + notification |
| **Géolocalisation** | Position GPS récupérée et jointe à l'alerte, envoyée au backend |
| **Appel automatique** | Composition directe des numéros d'urgence (15 SAMU, 17 Police) |
| **SMS d'urgence** | Message pré-rempli envoyé à tous les contacts enregistrés |
| **Photo de la situation** | Capture caméra optionnelle jointe à une alerte |
| **Carte des secours** | Hôpitaux et postes de police proches, avec bouton « Appeler » |
| **Contacts d'urgence** | Cercle de confiance géré localement (ajout, suppression, relation, répondant principal) |
| **Profil médical** | Nom, téléphone, groupe sanguin, conditions médicales critiques |
| **Notifications de prise en charge** | Confirmation que les secours ont reçu / pris en charge l'alerte |
| **Authentification** | Vérification du numéro de téléphone par code SMS (OTP) |

## 1.4 Public cible

- Conducteurs (assistance panne / accident).
- Personnes à risque médical (groupe sanguin, allergies renseignés pour les secouristes).
- Toute personne souhaitant un dispositif d'alerte rapide vers ses proches.

## 1.5 Parcours utilisateur type

1. **Première ouverture** : l'utilisateur vérifie son identité par numéro de téléphone (code SMS), puis renseigne son profil médical.
2. **Configuration** : il ajoute ses contacts d'urgence.
3. **Usage quotidien** : l'app reste prête. En cas d'urgence, il maintient le bouton SOS (ou le widget) → secours appelés, proches prévenus, position partagée.
4. **Suivi** : il consulte l'onglet Alertes pour voir l'historique et les notifications de prise en charge, et la carte pour localiser les services proches.

## 1.6 Périmètre (ce qui est simulé)

Comme il s'agit d'un projet pédagogique sans infrastructure serveur réelle :
- Le **backend** est simulé via l'API publique de test `jsonplaceholder.typicode.com` (l'envoi réseau est réel, mais aucune donnée n'est réellement stockée côté serveur ; l'alerte est toujours sauvegardée localement en SQLite — mode *offline-first*).
- Les **services proches** (hôpitaux, police) sont des données mockées géolocalisées sur Rabat.
- La **prise en charge** par les secours est simulée par une notification différée.
- L'**OTP** fonctionne en mode simulé tant que Firebase n'est pas configuré (voir [09](09-permissions-securite.md)).
