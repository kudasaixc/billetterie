package fr.maa.utils;

/**
 * Logique de génération des numéros de billet, isolée de l'accès aux données
 * afin d'être testable unitairement (sans base de données).
 * <p>
 * Format produit : {@code B{annee}-{sequence sur 6 chiffres}}, par exemple
 * {@code B2025-000001}. Le remplissage à six chiffres garantit un tri
 * lexicographique cohérent avec le tri numérique.
 */
public final class BilletNumeroGenerator {

    private BilletNumeroGenerator() {
    }

    /** Préfixe d'une année, ex. {@code "B2025-"}. */
    public static String prefix(int year) {
        return "B" + year + "-";
    }

    /** Formate un numéro complet, ex. {@code format(2025, 1) -> "B2025-000001"}. */
    public static String format(int year, int sequence) {
        return String.format("%s%06d", prefix(year), sequence);
    }

    /**
     * Calcule la séquence suivante à partir du dernier numéro connu.
     *
     * @param lastNumero dernier numéro enregistré, ou {@code null} s'il n'en
     *                   existe aucun.
     * @return la prochaine séquence ({@code 1} si {@code lastNumero} est nul ou
     *         non exploitable).
     */
    public static int nextSequence(String lastNumero) {
        if (lastNumero == null) {
            return 1;
        }
        String[] parts = lastNumero.split("-");
        if (parts.length == 2) {
            try {
                return Integer.parseInt(parts[1]) + 1;
            } catch (NumberFormatException ignored) {
                return 1;
            }
        }
        return 1;
    }
}
