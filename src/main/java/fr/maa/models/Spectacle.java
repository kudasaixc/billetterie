package fr.maa.models;

public class Spectacle {

    private int id;
    private String titre;
    private String lieu;
    private String affiche;
    private String tags;
    private int duree;
    private String descriptionCourte;
    private String descriptionLongue;
    private String langue;
    private int ageMinimum;
    private String photos;

    public Spectacle() {}

    public Spectacle(int id, String titre, String lieu, String affiche, String tags, int duree,
                     String descriptionCourte, String descriptionLongue, String langue,
                     int ageMinimum, String photos) {

        this.id = id;
        this.titre = titre;
        this.lieu = lieu;
        this.affiche = affiche;
       	this.tags = tags;
        this.duree = duree;
        this.descriptionCourte = descriptionCourte;
        this.descriptionLongue = descriptionLongue; // <-- correction ici
        this.langue = langue;
        this.ageMinimum = ageMinimum;
        this.photos = photos;
    }

    // GETTERS & SETTERS

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getLieu() { return lieu; }
    public void setLieu(String lieu) { this.lieu = lieu; }

    public String getAffiche() { return affiche; }
    public void setAffiche(String affiche) { this.affiche = affiche; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public int getDuree() { return duree; }
    public void setDuree(int duree) { this.duree = duree; }

    public String getDescriptionCourte() { return descriptionCourte; }
    public void setDescriptionCourte(String descriptionCourte) { this.descriptionCourte = descriptionCourte; }

    public String getDescriptionLongue() { return descriptionLongue; }
    public void setDescriptionLongue(String descriptionLongue) { this.descriptionLongue = descriptionLongue; }

    public String getLangue() { return langue; }
    public void setLangue(String langue) { this.langue = langue; }

    public int getAgeMinimum() { return ageMinimum; }
    public void setAgeMinimum(int ageMinimum) { this.ageMinimum = ageMinimum; }

    public String getPhotos() { return photos; }
    public void setPhotos(String photos) { this.photos = photos; }
}
