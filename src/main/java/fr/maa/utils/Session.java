package fr.maa.utils;

import fr.maa.models.Client;

public class Session {
    private static Client currentUser;

    private Session() {
    }

    public static void setUser(Client user) {
        currentUser = user;
    }

    public static Client getUser() {
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }

    public static boolean isVendeur() {
        return currentUser != null && currentUser.isVendeur();
    }

    // --- Capacités métier (matrice de droits centralisée) ------------------
    // Les contrôleurs interrogent une capacité (ce que l'utilisateur a le droit
    // de faire) plutôt qu'un rôle brut : ajouter un rôle ne touche qu'ici.

    /** Vendre des billets (admin et vendeur). */
    public static boolean peutVendre() {
        return isAdmin() || isVendeur();
    }

    /** Gérer le CRUD des spectacles et représentations (admin uniquement). */
    public static boolean peutGererSpectacles() {
        return isAdmin();
    }

    /** Gérer les utilisateurs (admin uniquement). */
    public static boolean peutGererUsers() {
        return isAdmin();
    }

    /** Accéder à la page de statistiques (admin et vendeur). */
    public static boolean peutVoirStatistiques() {
        return isAdmin() || isVendeur();
    }

    /** Voir les statistiques de tous les spectacles (admin) vs uniquement les siens (vendeur). */
    public static boolean peutVoirStatistiquesGlobales() {
        return isAdmin();
    }

    public static void logout() {
        currentUser = null;
    }
}
