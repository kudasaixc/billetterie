package fr.maa.models;

import java.time.LocalDateTime;

public class Representation {

    private int id;
    private int idSpectacle;
    private LocalDateTime dateHeure;
    private String salle;
    private int placesDisponibles;
    private double prix;
    private String spectacleTitle;

    public Representation() {}

    public Representation(int id, int idSpectacle, LocalDateTime dateHeure, String salle, int placesDisponibles, double prix) {
        this.id = id;
        this.idSpectacle = idSpectacle;
        this.dateHeure = dateHeure;
        this.salle = salle;
        this.placesDisponibles = placesDisponibles;
        this.prix = prix;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdSpectacle() {
        return idSpectacle;
    }

    public void setIdSpectacle(int idSpectacle) {
        this.idSpectacle = idSpectacle;
    }

    public LocalDateTime getDateHeure() {
        return dateHeure;
    }

    public void setDateHeure(LocalDateTime dateHeure) {
        this.dateHeure = dateHeure;
    }

    public String getSalle() {
        return salle;
    }

    public void setSalle(String salle) {
        this.salle = salle;
    }

    public int getPlacesDisponibles() {
        return placesDisponibles;
    }

    public void setPlacesDisponibles(int placesDisponibles) {
        this.placesDisponibles = placesDisponibles;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public String getSpectacleTitle() {
        return spectacleTitle;
    }

    public void setSpectacleTitle(String spectacleTitle) {
        this.spectacleTitle = spectacleTitle;
    }
}
