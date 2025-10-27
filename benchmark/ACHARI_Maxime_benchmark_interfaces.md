# Benchmark des frameworks d’interfaces Java  
**Auteur :** Maxime-Azdine ACHARI  
**Projet :** Gestion de Billetterie  
**Date :** 27 octobre 2025  

---

## Objectif
Comparer plusieurs frameworks Java permettant la création d’interfaces graphiques dans le cadre du projet de billetterie, afin d’en recommander deux :  
- un framework accessible et rapide à prendre en main,  
- un framework plus professionnel, utilisé en entreprise.

---

## Les critères

| Critère | Description | Poids (1–10) |
|----------|-------------|---------------|
| Installation | Simplicité de mise en place et de configuration | 8 |
| Documentation | Qualité et clarté de la documentation officielle | 9 |
| Facilité d’apprentissage | Rapidité de prise en main, communauté active | 8 |
| Qualité du rendu visuel | Aspect moderne, responsive, ergonomie | 7 |
| Intégration MVC | Support ou encouragement du modèle MVC | 7 |
| Performance / légèreté | Rapidité d’exécution et faible consommation de ressources | 6 |
| Popularité / mise à jour | Fréquence des releases et activité communautaire | 6 |
| Utilisation en entreprise | Adoption dans des environnements professionnels | 5 |
| Outils de conception visuelle | Outils graphiques disponibles (WYSIWYG, SceneBuilder, etc.) | 5 |

---

## Frameworks étudiés

1. **JavaFX** – Framework officiel moderne soutenu par Oracle, successeur de Swing.  
2. **Swing** – Ancien framework intégré à Java SE, simple mais visuellement dépassé.  
3. **SWT (Standard Widget Toolkit)** – Développé par la fondation Eclipse, utilisé dans l’IDE Eclipse.  
4. **Vaadin** – Framework orienté web basé sur Java et HTML5.  
5. **JSF (Jakarta Faces)** – Framework Java EE pour interfaces web, souvent couplé à Spring.

---

## Matrice de comparaison

| Critères (poids) | JavaFX | Swing | SWT | Vaadin | JSF |
|------------------:|:------:|:------:|:------:|:------:|:------:|
| Installation (8) | 9 | 10 | 7 | 6 | 5 |
| Documentation (9) | 9 | 8 | 6 | 8 | 7 |
| Facilité (8) | 9 | 9 | 7 | 6 | 5 |
| Rendu visuel (7) | 10 | 4 | 6 | 9 | 7 |
| MVC (7) | 8 | 4 | 6 | 9 | 8 |
| Performance (6) | 8 | 9 | 7 | 6 | 6 |
| Popularité / MAJ (6) | 9 | 7 | 6 | 8 | 6 |
| Usage en prod (5) | 8 | 6 | 7 | 9 | 9 |
| Outils visuels (5) | 8 | 7 | 5 | 7 | 6 |
| **Total pondéré** | **674** | **583** | **551** | **611** | **554** | -- calculé par ChatGPT

---

## Choix final -- déterminé par ChatGPT

### Framework principal : JavaFX
- Officiel, maintenu par Oracle et OpenJFX  
- Moderne, esthétique, facile à apprendre  
- Bonne compatibilité avec le modèle MVC  
- Intégration possible avec FXML et SceneBuilder  
→ Convient parfaitement pour une application locale telle que la billetterie.

### Framework secondaire : Vaadin
- Framework Java pour interfaces web  
- Utilisé dans de nombreux projets professionnels  
- Permet de créer une interface web entièrement en Java  
- Supporte le modèle MVC et une architecture client/serveur claire  
→ Intéressant pour une évolution future du projet vers une version web.

---

## Conclusion

Pour la version locale du projet, **JavaFX** est le choix le plus adapté grâce à sa simplicité, son rendu moderne et sa bonne documentation ainsi que sa soutenance par Oracle.
Pour une version en ligne ou dans un contexte de production  pro, **Vaadin** représente une alternative solide, tout en restant dans l’écosystème Java.


