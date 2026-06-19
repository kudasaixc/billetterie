# 🎟️ Billetterie Tic'n Go — Application JavaFX & MySQL  
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

## ▶️ Lancer le projet

### 1. Importer le projet
mvn clean install

### 2. Lancer MySQL avec la base :
mysql -u root -p < sql/billetterie_template.sql

### 3. Configurer la connexion :
Les paramètres de connexion sont externalisés dans
`src/main/resources/config.properties` (`db.url`, `db.user`, `db.password`).
Adaptez-les à votre environnement avant le lancement.

### 4. Lancer l’application :
mvn clean javafx:run

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
- 🧾 Factures PDF
- 🧪 Tests unitaires JUnit + coverage
- 🎧 Mode scanner QR code pour contrôler les billets

---

## 🧑‍💻 Auteur

Projet conçu et développé par **Azdine**
