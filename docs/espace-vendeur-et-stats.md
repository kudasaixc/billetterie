# Espace Vendeur & Statistiques de remplissage

Documentation d'implémentation des deux fonctionnalités ajoutées : un **espace
vendeur** distinct de l'espace administrateur, et une **page de statistiques de
remplissage** par spectacle.

---

## 1. Gestion des rôles

### Problème de départ

Le rôle utilisateur reposait sur un booléen `client.is_admin` : impossible de
modéliser trois profils distincts avec deux états.

### Solution

Introduction d'une véritable notion de rôle à trois valeurs via l'enum
`fr.maa.models.Role` :

| Rôle | Droits |
|------|--------|
| `ADMIN` | Accès complet (administration, CRUD, statistiques globales) |
| `VENDEUR` | Consultation des spectacles, vente, statistiques et billetterie de **ses** spectacles |
| `CLIENT` | Accès limité à ses propres billets |

Une colonne `client.role` est ajoutée en base. Le booléen `is_admin` est conservé
pour compatibilité : `isAdmin()` dérive désormais du rôle (`role == ADMIN`).

### Capacités centralisées

Les droits sont exposés sous forme de **capacités métier** dans `Session`, plutôt
que par des tests de rôle dispersés dans le code :

```java
Session.peutVendre();                    // ADMIN ou VENDEUR
Session.peutGererSpectacles();           // ADMIN
Session.peutGererUsers();                // ADMIN
Session.peutVoirStatistiques();          // ADMIN ou VENDEUR
Session.peutVoirStatistiquesGlobales();  // ADMIN (sinon : ses spectacles)
```

> Ajouter un nouveau rôle ne touche que `Session`, pas les contrôleurs.

---

## 2. Cloisonnement des droits (défense en profondeur)

Le contrôle d'accès s'applique à **deux niveaux** :

1. **Menu** (`MainController`) — les boutons non autorisés sont masqués selon la
   capacité de l'utilisateur.
2. **Contrôleurs** — chaque écran sensible re-vérifie le droit dans son
   `initialize()` et renvoie au menu si l'accès n'est pas permis.

> Masquer un bouton n'est pas une sécurité : le contrôle réel se fait côté logique.

| Action | Admin | Vendeur | Client |
|--------|:-----:|:-------:|:------:|
| Consulter les spectacles | ✅ | ✅ | ✅ |
| Vendre des billets | ✅ | ✅ | ❌ |
| CRUD spectacles / représentations | ✅ | ❌ | ❌ |
| Gérer les utilisateurs | ✅ | ❌ | ❌ |
| Billetterie | tous | **ses** spectacles | ses billets |
| Statistiques | tous | **ses** spectacles | ❌ |

La vente passe de `Session.isAdmin()` à `Session.peutVendre()`
(`BilletFormController`). Le formulaire client remplace la case « Admin ? » par un
menu déroulant de rôle, ce qui permet à l'administrateur de créer des vendeurs.

---

## 3. Page de statistiques de remplissage

Pour chaque spectacle : **places totales**, **billets vendus**, **places
restantes** et **taux de remplissage (%)**.

### Calcul de la capacité

La capacité initiale n'est pas stockée — les places sont décrémentées à chaque
vente. Elle est donc reconstituée :

```
places_totales = places_restantes + billets_vendus
taux           = billets_vendus / places_totales   (0 si capacité nulle)
```

Le cas « capacité nulle » est géré explicitement pour éviter une division par zéro
(`RemplissageStat.getTauxRemplissage`).

### Requête SQL (anti fan-out)

Joindre `representation` **et** `billet` en même temps multiplierait les lignes et
fausserait la somme des places. On utilise donc des **sous-requêtes corrélées** :

```sql
SELECT s.titre,
  COALESCE((SELECT SUM(r.places_disponibles)
            FROM representation r WHERE r.id_spectacle = s.id), 0) AS places_restantes,
  COALESCE((SELECT COUNT(b.id)
            FROM billet b JOIN representation r ON b.id_representation = r.id
            WHERE r.id_spectacle = s.id), 0) AS billets_vendus
FROM spectacle s
[WHERE s.id_vendeur = ?]   -- filtre vendeur
ORDER BY s.titre;
```

Les requêtes sont des constantes `static final` (`SQL_TOUS` / `SQL_PAR_VENDEUR`),
sans concaténation à l'exécution — ce qui lève aussi les alertes SonarCloud.

### Périmètre selon le rôle

- **Admin** : tous les spectacles.
- **Vendeur** : uniquement les siens (`WHERE s.id_vendeur = ?`).

Si la colonne `id_vendeur` est absente (migration non appliquée), les statistiques
vendeur renvoient une liste vide plutôt que d'exposer toutes les données.

---

## 4. Affectation d'un spectacle à un vendeur

L'appartenance d'un spectacle à un vendeur est portée par la colonne
`spectacle.id_vendeur` (nullable, FK vers `client`).

Elle est gérable **depuis l'interface admin** : le formulaire spectacle propose un
menu déroulant « Vendeur » (avec une option « — Aucun — »). À l'enregistrement,
`id_vendeur` est renseigné, et le vendeur voit immédiatement ses statistiques et sa
billetterie.

Côté code :

- `Spectacle.idVendeur` (`Integer`, nullable)
- `SpectacleDAO` lit/écrit `id_vendeur` (en défensif, via `checkColumnExists`)
- `ClientDAO.getVendeurs()` liste les comptes `role = VENDEUR`

---

## 5. Base de données

| Fichier | Rôle |
|---------|------|
| `sql/billetterie_template.sql` | Schéma de référence : colonnes `role` et `id_vendeur`, FK, index, comptes et affectations de démo |
| `sql/migration_vendeur_statistiques.sql` | Migration **non destructive** pour une base existante |

La migration est **idempotente** et **compatible MySQL 8 et MariaDB**. La syntaxe
`ALTER ... ADD COLUMN IF NOT EXISTS` étant propre à MariaDB, l'existence des
colonnes/contraintes/index est testée via `information_schema` puis le DDL est
exécuté conditionnellement (`PREPARE` / `EXECUTE`).

```bash
mysql -u <user> -p billetterie < sql/migration_vendeur_statistiques.sql
```

---

## 6. Tests

Tests JUnit ajoutés (logique pure, sans base) :

- `RemplissageStatTest` — capacité, taux, salle complète, cas zéro place (pas de
  division par zéro).
- `ClientRoleTest` — cohérence `role` / `isAdmin` / `isVendeur`, désactivation
  admin ne rétrograde pas un vendeur, rôle `null` → `CLIENT`.

---

## 7. Comptes de démonstration

| Rôle | Email | Mot de passe |
|------|-------|--------------|
| Admin | `admin@billetterie.local` | `admin123` |
| Vendeur | `vendeur@billetterie.local` | `admin123` |
