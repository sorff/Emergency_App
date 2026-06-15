# 05 — Écrans & navigation

## 5.1 Diagramme de navigation

```
                      ┌──────────────────┐
                      │  SplashActivity  │  (2 s, logo)
                      └────────┬─────────┘
              profil existant ?│
                  ┌────────────┴────────────┐
                 NON                        OUI
                  │                          │
        ┌─────────▼──────────┐               │
        │ VerifyIdentity     │ (numéro tél.) │
        └─────────┬──────────┘               │
                  │ envoi code OTP           │
        ┌─────────▼──────────┐               │
        │   OtpActivity      │ (6 chiffres)  │
        └─────────┬──────────┘               │
                  │ code validé              │
        ┌─────────▼──────────┐               │
        │ ProfileSetup       │ (profil méd.) │
        └─────────┬──────────┘               │
                  └────────────┬─────────────┘
                               ▼
                    ┌────────────────────┐
                    │    MainActivity    │  ← BottomNavigationView
                    │  ┌──────────────┐  │
                    │  │ HomeFragment │  │ (onglet Accueil — SOS)
                    │  │ ContactsFrag │  │ (onglet Contacts)
                    │  │ MapFragment  │  │ (onglet Carte)
                    │  │ AlertsFrag   │  │ (onglet Alertes)
                    │  └──────────────┘  │
                    └─────┬────────┬─────┘
        avatar │   FAB Alertes │   bouton + Contacts
               ▼              ▼                ▼
     ┌──────────────┐ ┌──────────────────┐ ┌────────────────┐
     │SettingsActiv.│ │NouvelleAlerteAct.│ │AddContactActiv.│
     └──────────────┘ └──────────────────┘ └────────────────┘
```

## 5.2 Écrans d'onboarding

### SplashActivity (`activity_splash.xml`)
- Fond dégradé rouge, bouclier blanc, titre « AidGuard », sous-titre, points de chargement.
- Après 2 s (`Handler.postDelayed`), route selon la présence d'un profil.

### VerifyIdentityActivity (`activity_verify_identity.xml`)
- Bouclier rouge, titre « Vérifier l'identité », champ téléphone (préfixe `+212`), note de chiffrement, bouton « Envoyer le code de vérification ».
- Valide → `AuthHelper.envoyerCode()` → `OtpActivity`.

### OtpActivity (`activity_otp.xml`)
- **6 cases** (`EditText`) avec auto-avance et gestion du retour arrière.
- « Renvoyer le code », note de sécurité, bouton « Vérifier et continuer ».
- Code validé → `ProfileSetupActivity` (`finishAffinity()`).

### ProfileSetupActivity (`activity_profile_setup.xml`)
- Avatar + badge caméra, champs Nom et Téléphone, **grille de 8 groupes sanguins** (A+, A-, B+, B-, O+, O-, AB+, AB-) en sélection unique, zone « Conditions médicales critiques ».
- « Enregistrer et continuer » → SharedPreferences → `MainActivity`.

## 5.3 Application principale — les 4 onglets

### Onglet Accueil — HomeFragment (`fragment_home.xml`)
Le **tableau de bord SOS** :
- Header : avatar (→ Réglages), « AidGuard », icône d'alerte.
- Carte « Statut actuel : Surveillance du système… » + badge vert « Sécurisé ».
- **Bouton SOS** (halo animé) : **appui maintenu 3 s** → notification + SMS aux contacts + appel du 15.
- Cartes « Appeler la police » (17) / « Appeler les secours » (15).
- « Prévenir les contacts de confiance » (→ onglet Contacts) avec compteur.
- « Votre position » : adresse obtenue par `Geocoder` à partir du GPS.

### Onglet Contacts — ContactsFragment (`fragment_contacts.xml`)
- Titre « Contacts d'urgence », bouton d'ajout (→ `AddContactActivity`).
- Cartes de statut : « Niveau de sécurité : Optimal », « Alerte active : Aucune ».
- Barre de recherche filtrante.
- Liste de contacts (cartes avatar + nom + téléphone + interrupteur d'appel).
- Suppression par **appui long** (dialogue de confirmation).
- État vide + note d'information.

### Onglet Carte — MapFragment (`fragment_map.xml`)
- Header AidGuard, **carte Google Maps** plein écran.
- Marqueur rouge « Moi » + marqueurs hôpitaux (bleus) et police (noir/bleu).
- Carte basse « Services proches » : liste (CHU, Commissariat, Clinique, SAMU) avec distance et bouton « Appeler ».
- La carte est chargée **par code et différée** pour éviter un ANR au lancement.

### Onglet Alertes — AlertsFragment (`fragment_alerts.xml`)
- Carte rouge « Prêt pour l'assistance / Système actif / Suivi en temps réel activé ».
- Carte « Contacts de confiance : N actifs ».
- « Alertes récentes » + « Tout marquer comme lu ».
- **RecyclerView** affichant : les **alertes réelles** (SQLite, avec statut « envoyée » / « prise en charge ») + des notifications de démonstration.
- **FAB +** → `NouvelleAlerteActivity`.

## 5.4 Écrans secondaires

### NouvelleAlerteActivity (`activity_nouvelle_alerte.xml`)
- **Type d'urgence** : puces (Panne, Accident, Médical, Incendie, Autre) — sélection unique.
- **Description** (multiligne) + compteur de caractères.
- **Switch GPS** : si activé, récupère la position via `LocationManager`.
- **Photo** : zone pointillée → caméra (`ACTION_IMAGE_CAPTURE`), aperçu + suppression.
- « Envoyer l'alerte » : validation → Retrofit (`enqueue`) → sauvegarde SQLite → notification « Alerte envoyée » + **notification de prise en charge différée (7 s)**.

### AddContactActivity (`activity_add_contact.xml`)
- Nom, mobile, **relation** (`Spinner`), **« Répondant principal »** (`Switch`), Annuler / Enregistrer.
- Insertion en SQLite via `AsyncTask`.

### SettingsActivity (`activity_settings.xml`)
- **Profil** : avatar, nom, téléphone, groupe sanguin (`Spinner`), conditions, « Sauvegarder ».
- **Préférences** : « Notifications activées », « Localisation automatique » (`Switch` liés à SharedPreferences).
- **Zone de danger** : « Vider l'historique des alertes » (DELETE SQLite avec confirmation), « Se déconnecter » (efface le profil, retourne à l'onboarding).
