package fr.maa.models;

public class Billet {

    private int id;
    private String numero;
    private int idRepresentation;
    private int idClient;

    public Billet() {}

    public Billet(int id, String numero, int idRepresentation, int idClient) {
        this.id = id;
        this.numero = numero;
        this.idRepresentation = idRepresentation;
        this.idClient = idClient;
    }

    // getters/setters
}
