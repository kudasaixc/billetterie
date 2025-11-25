package fr.maa.models;

import java.time.LocalDateTime;

public class Representation {

    private int id;
    private int idSpectacle;
    private LocalDateTime dateHeure;
    private String salle;

    public Representation() {}

    public Representation(int id, int idSpectacle, LocalDateTime dateHeure, String salle) {
        this.id = id;
        this.idSpectacle = idSpectacle;
        this.dateHeure = dateHeure;
        this.salle = salle;
    }

    // getters/setters
}
