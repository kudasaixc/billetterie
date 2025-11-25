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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public int getIdRepresentation() {
        return idRepresentation;
    }

    public void setIdRepresentation(int idRepresentation) {
        this.idRepresentation = idRepresentation;
    }

    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }
}
