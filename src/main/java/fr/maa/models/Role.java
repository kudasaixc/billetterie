package fr.maa.models;

/**
 * Rôle d'un utilisateur, qui détermine ses droits dans l'application.
 * <ul>
 *     <li>{@link #ADMIN} : accès complet (administration, CRUD, statistiques globales).</li>
 *     <li>{@link #VENDEUR} : consultation des spectacles, vente de billets et
 *     statistiques de remplissage limitées à ses propres spectacles. Pas d'accès
 *     aux fonctions d'administration ni au CRUD des entités.</li>
 *     <li>{@link #CLIENT} : accès limité à ses propres billets.</li>
 * </ul>
 */
public enum Role {
    ADMIN,
    VENDEUR,
    CLIENT;

    /**
     * Convertit une valeur stockée en base (insensible à la casse) en {@link Role}.
     * Retourne {@code null} si la valeur est nulle ou inconnue, afin que l'appelant
     * puisse appliquer une valeur par défaut.
     */
    public static Role fromString(String value) {
        if (value == null) {
            return null;
        }
        try {
            return Role.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
