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

    public static void logout() {
        currentUser = null;
    }
}
