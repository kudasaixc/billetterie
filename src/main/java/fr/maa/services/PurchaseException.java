package fr.maa.services;

/**
 * Exception métier levée lorsqu'un achat de billets ne peut pas être mené à
 * son terme (places insuffisantes, erreur d'accès aux données, etc.).
 * <p>
 * Elle permet de remonter un message exploitable vers la couche présentation
 * sans exposer les détails techniques (SQL) à l'utilisateur.
 */
public class PurchaseException extends Exception {

    public PurchaseException(String message) {
        super(message);
    }

    public PurchaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
