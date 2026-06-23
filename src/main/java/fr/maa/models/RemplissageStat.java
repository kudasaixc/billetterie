package fr.maa.models;

/**
 * Statistique de remplissage agrégée pour un spectacle.
 * <p>
 * La capacité totale initiale n'est pas stockée en base : les places sont
 * décrémentées à chaque vente. On la reconstitue donc comme
 * {@code placesRestantes + billetsVendus}, ce qui correspond à la capacité de
 * départ de l'ensemble des représentations du spectacle.
 */
public class RemplissageStat {

    private final String titre;
    private final int billetsVendus;
    private final int placesRestantes;

    public RemplissageStat(String titre, int billetsVendus, int placesRestantes) {
        this.titre = titre;
        this.billetsVendus = billetsVendus;
        this.placesRestantes = placesRestantes;
    }

    public String getTitre() {
        return titre;
    }

    public int getBilletsVendus() {
        return billetsVendus;
    }

    public int getPlacesRestantes() {
        return placesRestantes;
    }

    /** Capacité totale initiale = places restantes + billets déjà vendus. */
    public int getPlacesTotales() {
        return placesRestantes + billetsVendus;
    }

    /** Taux de remplissage entre 0 et 1 (0 si aucune place, pour éviter la division par zéro). */
    public double getTauxRemplissage() {
        int total = getPlacesTotales();
        return total == 0 ? 0d : (double) billetsVendus / total;
    }

    /** Taux de remplissage formaté en pourcentage, ex. {@code "87,5 %"}. */
    public String getTauxRemplissagePct() {
        return String.format("%.1f %%", getTauxRemplissage() * 100);
    }
}
