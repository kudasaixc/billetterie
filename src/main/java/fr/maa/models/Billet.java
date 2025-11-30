package fr.maa.models;

import java.time.LocalDateTime;

public class Billet {

    private int id;
    private String numero;
    private int idRepresentation;
    private int idClient;
    private double prix;
    private LocalDateTime dateAchat;
    private String representationLabel;
    private String spectacleTitle;
    private String clientName;

    public Billet() {}

    public Billet(int id, String numero, int idRepresentation, int idClient, double prix, LocalDateTime dateAchat) {
        this.id = id;
        this.numero = numero;
        this.idRepresentation = idRepresentation;
        this.idClient = idClient;
        this.prix = prix;
        this.dateAchat = dateAchat;
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

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public LocalDateTime getDateAchat() {
        return dateAchat;
    }

    public void setDateAchat(LocalDateTime dateAchat) {
        this.dateAchat = dateAchat;
    }

    public String getRepresentationLabel() {
        return representationLabel;
    }

    public void setRepresentationLabel(String representationLabel) {
        this.representationLabel = representationLabel;
    }

    public String getSpectacleTitle() {
        return spectacleTitle;
    }

    public void setSpectacleTitle(String spectacleTitle) {
        this.spectacleTitle = spectacleTitle;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
}
