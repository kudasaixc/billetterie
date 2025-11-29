package fr.maa.models;

import java.time.LocalDateTime;

public class Representation {

    private int id;
    private int idSpectacle;
    private LocalDateTime dateHeure;
    private String salle;
    private int placesDisponibles;
    private String spectacleTitle;

    public Representation() {}

    public Representation(int id, int idSpectacle, LocalDateTime dateHeure, String salle, int placesDisponibles) {
        this.id = id;
        this.idSpectacle = idSpectacle;
        this.dateHeure = dateHeure;
        this.salle = salle;
        this.placesDisponibles = placesDisponibles;
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

    public String getSpectacleTitle() {
        return spectacleTitle;
    }

    public void setSpectacleTitle(String spectacleTitle) {
        this.spectacleTitle = spectacleTitle;
    }
}
