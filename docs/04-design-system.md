# 04 — Design system

## 4.1 Palette de couleurs (`res/values/colors.xml`)

| Nom | Hex | Usage |
|-----|-----|-------|
| `primary` | `#C0392B` | Rouge principal (boutons, accents, SOS) |
| `primary_dark` | `#922B21` | Variante foncée (dégradé splash) |
| `primary_light` | `#FFEBEE` | Rouge très clair (fonds d'icônes) |
| `accent` | `#E74C3C` | Accent secondaire |
| `background` | `#F8F9FA` | Fond d'écran général |
| `card_bg` | `#FFFFFF` | Fond des cartes |
| `text_primary` | `#212121` | Texte principal |
| `text_secondary` | `#757575` | Texte secondaire / légendes |
| `success` | `#27AE60` | Vert (statut « résolu/sécurisé ») |
| `warning` | `#F39C12` | Orange (statut « en cours ») |
| `header_bg` | `#B71C1C` | Fond de bandeau d'en-tête |
| `status_bar_bg` | `#7B1C1C` | Barre de statut |

**Logique** : le rouge évoque l'urgence/le secours ; le vert et l'orange servent à coder l'état des alertes (résolu / en cours).

## 4.2 Dimensions (`res/values/dimens.xml`)

Centralisées pour la cohérence visuelle :

| Nom | Valeur | Usage |
|-----|--------|-------|
| `marge_standard` | 16dp | Marge par défaut |
| `marge_large` | 24dp | Marge large |
| `card_radius` | 16dp | Arrondi des cartes |
| `card_elevation` | 4dp | Ombre des cartes |
| `btn_height` | 56dp | Hauteur des boutons |
| `btn_radius` | 100dp | Boutons pilule |
| `icon_bg_size` | 52dp | Carré de fond d'icône |
| `sos_button_size` | 160dp | Bouton SOS |
| `sos_ring_size` | 220dp | Halo SOS |
| `avatar_size` | 80dp | Avatar profil |

## 4.3 Typographie

- **Titres** : 22–28 sp, bold.
- **Sous-titres** : 16 sp.
- **Corps** : 14 sp.
- **Labels** : 11–12 sp, souvent en majuscules avec `letterSpacing` (style « libellé de section »).

## 4.4 Thèmes (`res/values/themes.xml`)

- `Theme.EmergencyApp` : parent `Theme.Material3.DayNight.NoActionBar`, sans ActionBar (les écrans gèrent leur propre barre).
- `Theme.EmergencyApp.Splash` : fond rouge foncé pour le démarrage.
- Couleurs de la status bar définies par thème.

## 4.5 Drawables clés (`res/drawable/`)

Les drawables sont des **XML vectoriels** (icônes) ou des **shapes** (formes) — aucun PNG décoratif, ce qui rend l'app légère et nette à toute résolution.

| Fichier | Type | Rôle |
|---------|------|------|
| `bg_sos_button.xml` | oval | Cercle rouge du bouton SOS |
| `bg_sos_ring.xml` | oval | Halo semi-transparent (animé) |
| `bg_avatar_circle.xml` | oval | Avatar rond rouge |
| `bg_icon_red.xml` / `bg_icon_blue.xml` | rectangle | Carrés de fond d'icône |
| `bg_chip_selected/unselected.xml` | rectangle | Puces de type d'urgence |
| `bg_badge_orange/green.xml` | rectangle | Badges de statut |
| `bg_gradient_splash.xml` | gradient | Dégradé rouge du splash |
| `bg_photo_dashed.xml` | rectangle pointillé | Zone photo |
| `bg_otp_box.xml` | rectangle | Cases du code OTP |
| `ic_shield, ic_home, ic_contacts, ic_map, ic_alerts…` | vector | Icônes de l'app |
| `ic_notification.xml` | vector | Silhouette pour les notifications |

## 4.6 Animation

- **`anim/pulse.xml`** : animation `scale` 1.0 → 1.08 → 1.0, répétée à l'infini, appliquée au halo du bouton SOS pour signaler que le dispositif est « vivant »/prêt.

## 4.7 Icône de l'application (logo)

- **Icône adaptive** (`mipmap-anydpi-v26/ic_launcher.xml`) : `background` rouge + `foreground` bouclier blanc avec croix médicale rouge + `monochrome` (icônes thématiques Android 13+).
- **Bitmaps PNG** générés à toutes les densités (`mdpi`→`xxxhdpi`) pour les surfaces qui n'utilisent pas l'adaptive (notamment l'icône affichée dans les **notifications** et les **récents**).
- **Cohérence** : le même bouclier rouge apparaît dans le launcher, les notifications, le splash et les écrans d'auth.
