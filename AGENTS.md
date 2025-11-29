# GitHub Agents – Project Rules & Professor Requirements

This repository contains a JavaFX + Maven application following a strict MVC architecture required by the professor.  
Agents must comply with ALL rules defined below.

---

# 📚 1. Professor Requirements (MANDATORY)

### 🎨 GUI Requirements
- All UI must be done using **JavaFX**.
- All layout files must be **FXML** created with **SceneBuilder**.
- UI must NOT contain business logic.

### 🧱 MVC Structure
Agents MUST respect the architecture:

FXML (View) → Controller → DAO → Model → Database
 
### 🛠️ CRUD Required
The application must implement COMPLETE CRUD operations for:

- Client  
- Spectacle  
- Représentation  
- Billet  

### 🔎 Functional Requirements
- Pagination for list screens  
- Search (filter) for list screens  
- Secure image upload for Spectacles  
- Clean, maintainable, SOLID code  
- No duplicated logic  
- No dead files  

---

# 🔒 2. Files Agents MUST NOT MODIFY

These files are considered **stable, validated, and required**.  
Agents are FORBIDDEN from altering, renaming, moving, or restructuring them.

### Controllers
- `src/main/java/fr/maa/controllers/MainController.java`
- `src/main/java/fr/maa/controllers/ClientListController.java`
- `src/main/java/fr/maa/controllers/ClientFormController.java`
- `src/main/java/fr/maa/controllers/SpectacleListController.java`
- `src/main/java/fr/maa/controllers/SpectacleFormController.java`
- `src/main/java/fr/maa/controllers/RepresentationListController.java`
- `src/main/java/fr/maa/controllers/RepresentationFormController.java`
- `src/main/java/fr/maa/controllers/BilletFormController.java`

### DAO
- `src/main/java/fr/maa/dao/ClientDAO.java`
- `src/main/java/fr/maa/dao/SpectacleDAO.java`
- `src/main/java/fr/maa/dao/RepresentationDAO.java`
- `src/main/java/fr/maa/dao/BilletDAO.java`
- `src/main/java/fr/maa/dao/Database.java`

### Models
- `src/main/java/fr/maa/models/*.java`

### Utils
- `src/main/java/fr/maa/utils/SceneSwitcher.java`
- `src/main/java/fr/maa/utils/SelectedClient.java`

### FXML (View)
- `src/main/resources/views/*.fxml`

### POM
The file `pom.xml` must remain **exactly identical to the version defined by the user**:
- No new plugins  
- No new dependencies  
- No changes to Java version  
- No auto-formatting  

---

# 🗑️ 3. Cleanup Rules

Agents MAY delete files ONLY IF:

- Explicitly listed by the user  
- Or proven unused (0-byte, empty class, never imported)

Agents MUST NOT:
- Guess  
- Invent cleanup  
- Delete anything not listed  

---

# 🛠️ 4. What Agents ARE Allowed to Do

- Fix compilation errors ONLY inside permitted files  
- Apply patches modifying ONLY the files mentioned by the user  
- Improve readability (when user allows it)  
- Fix import issues  
- Remove duplicate class definitions  
- Rename internal variables ONLY if harmless  

---

# 🚫 5. Forbidden Actions

Agents MUST NOT:
- Refactor architecture  
- Move classes between packages  
- Change controllers-to-FXML bindings  
- Create or destroy model/DAO files without instruction  
- Change database logic  
- Modify pom.xml  
- Generate new files unless user explicitly asks  

---

# 📝 6. Precedence Rule
If the rules in this file conflict with **direct user instructions**,  
➡️ **User instructions ALWAYS win.**

---

# 🧩 7. Notes for Codex
- Do NOT ask for `AGENTS.md` again.  
- Follow this file as global policy.  
- For patches, use the minimal diff needed.  
