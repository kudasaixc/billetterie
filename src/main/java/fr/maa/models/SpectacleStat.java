package fr.maa.models;

public class SpectacleStat {

    private final String titre;
    private final int billetsVendus;
    private final double chiffreAffaires;

    public SpectacleStat(String titre, int billetsVendus, double chiffreAffaires) {
        this.titre = titre;
        this.billetsVendus = billetsVendus;
        this.chiffreAffaires = chiffreAffaires;
    }

    public String getTitre() {
        return titre;
    }

    public int getBilletsVendus() {
        return billetsVendus;
    }

    public double getChiffreAffaires() {
        return chiffreAffaires;
    }
}
