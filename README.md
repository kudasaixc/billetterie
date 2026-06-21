# 🎟️ Billetterie Tic'n Go — Application JavaFX & MySQL  

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=kudasai_billetterie&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=kudasai_billetterie)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=kudasai_billetterie&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=kudasai_billetterie)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=kudasai_billetterie&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=kudasai_billetterie)

Système complet de gestion de billetterie, développé en JavaFX (MVC), avec base de données MySQL/MariaDB, authentification sécurisée, dashboard statistique, génération de billets PDF et outils d'administration.

Ce projet est conçu comme une véritable application professionnelle comparable à Ticketmaster Back-Office, SeeTickets ou les outils internes utilisés par les salles de spectacles.

---

## 🚀 Fonctionnalités principales

### 🔐 Authentification & Rôles
- Login sécurisé avec **bcrypt**
- Inscription utilisateur avec contrôle d’unicité email
- Système de session persistante (`Session.java`)
- **Rôle admin** : accès complet à l’administration
- **Rôle client** : accès seulement à ses propres billets

---

## 🎭 Gestion complète des entités
### ✔ Spectacles
- Ajout / modification / suppression
- Tags, langues, description, durée
- Lieux réels, affiches et informations détaillées

### ✔ Représentations
- Date & heure
- Salle
- Prix différent par représentation
- Stock / places disponibles
- Filtre par spectacle, date, prix

### ✔ Billets
- Achat multi-billets
- Numéro unique : `TCK-YYYY-XXX`
- Décrémentation automatique du stock
- Liste filtrée par client connecté

### ✔ Clients (admin only)
- CRUD complet
- Recherche par nom/email
- Pagination

---

## 📊 Dashboard Administrateur

Interface moderne regroupant toutes les statistiques clés :

- Nombre de spectacles
- Nombre de représentations
- Total billets vendus
- **Chiffre d’affaires** généré
- Top 5 spectacles les plus vendus
- Graphique des ventes par jour
- Répartition des ventes (PieChart)
- CA par spectacle (BarChart)
- Évolution chronologique (LineChart)

Le dashboard utilise JavaFX Charts et des requêtes SQL optimisées.

---

## 🧾 Génération de billets PDF

À chaque achat de billet :
- Génération d’un **PDF professionnel**
- Informations :
  - nom client
  - spectacle
  - date & salle
  - numéro du billet
  - prix payé
- **QR code intégré** (ZXing)
- Aperçu réaliste façon Ticketmaster / Fnac

Système basé sur **OpenPDF + ZXing**.

---

## 🔍 Recherche & Filtres avancés

Disponible dans toutes les listes :
- recherche textuelle (LIKE %term%)
- filtres sur :
  - date
  - prix
  - tags
  - lieux
  - spectacles
  - client (admin)
- pagination automatique (50 par page)
- UX très fluide et professionnelle

---

## 🛢️ Base de données (MySQL/MariaDB)

Tables utilisées :

- `client`  
- `spectacle`  
- `representation`  
- `billet`

### ⭐ Particularités :
- `representation.prix` → prix dynamique par représentation  
- `billet.prix` → copie au moment de l’achat  
- `representation.places_disponibles` → décrémenté par achat  
- `client.is_admin` → rôles / permissions  

Le script SQL complet se trouve dans `sql/billetterie_template.sql`.

---

## 🧰 Technologies & Librairies

### Back-end
- Java 17+
- JavaFX 20
- Maven

### Front-end (UI)
- JavaFX (FXML)
- Charts (PieChart, BarChart, LineChart, AreaChart)
- Feuilles de style CSS personnalisées

### Database
- MySQL / MariaDB
- JDBC

### PDF & QR Codes
- OpenPDF
- ZXing (QRCodeWriter)

---

## ✅ Prérequis

Le projet suppose que les outils suivants sont **installés au préalable** :

| Outil | Version | Vérifier |
|-------|---------|----------|
| **JDK** (OpenJDK) | 21+ | `java -version` |
| **Maven** | 3.9+ | `mvn -v` |
| **MySQL** *ou* **MariaDB** | MySQL 8.x / MariaDB 10+ | `mysql --version` |

> JavaFX n'est **pas** à installer séparément : les modules sont récupérés automatiquement par Maven.

### Installation rapide

**Windows** (via [winget](https://learn.microsoft.com/windows/package-manager/winget/)) :
```powershell
winget install Microsoft.OpenJDK.21
winget install MariaDB.Server          # ou : winget install Oracle.MySQL
# Maven n'est pas dans winget : télécharger https://maven.apache.org/download.cgi,
# décompresser, puis ajouter le dossier bin\ au PATH.
```

**macOS** (Homebrew) :
```bash
brew install openjdk@21 maven mariadb
```

**Linux** (Debian/Ubuntu) :
```bash
sudo apt install openjdk-21-jdk maven mariadb-server
```

Vérifiez que `JAVA_HOME` pointe vers le JDK 21 et que `java`, `mvn` et `mysql` sont accessibles dans le `PATH`.

---

## ▶️ Lancer le projet

### 1. Cloner le dépôt
```bash
git clone https://github.com/kudasaixc/billetterie.git
cd billetterie
```

### 2. Créer la base de données
```bash
mysql -u root -p < sql/billetterie_template.sql
```

### 3. Configurer la connexion
Les paramètres de connexion sont externalisés dans
`src/main/resources/config.properties` (`db.url`, `db.user`, `db.password`).
Adaptez-les à votre environnement avant le lancement.

### 4. Compiler et lancer les tests
```bash
mvn clean test
```

### 5. Lancer l’application
```bash
mvn clean javafx:run
```

---

## 🔐 Identifiants de test

### Admin :
email : admin@billetterie.local
password : admin123

---

## 📝 Avenir & Extensions (Roadmap)

- 📲 Application mobile (API REST)
- 📸 Upload d’images pour spectacles
- ⌛ Réservation temporaire (15 min) avec colonne `reserved_until`
- 🧪 Couverture de tests (JaCoCo) en complément des tests JUnit existants
- 🎧 Mode scanner QR code pour contrôler les billets

> ✅ **Déjà livré** : génération PDF des billets (OpenPDF + QR code ZXing) et premiers tests unitaires JUnit.

---

## 🧑‍💻 Auteur

Projet conçu et développé par **Azdine**
