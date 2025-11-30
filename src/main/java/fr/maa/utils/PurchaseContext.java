package fr.maa.utils;

import fr.maa.models.Representation;
import fr.maa.models.Spectacle;

public class PurchaseContext {
    private static Spectacle spectacle;
    private static Representation representation;
    private static int quantity;

    private PurchaseContext() {
    }

    public static void set(Spectacle s, Representation r, int qty) {
        spectacle = s;
        representation = r;
        quantity = qty;
    }

    public static Spectacle getSpectacle() {
        return spectacle;
    }

    public static Representation getRepresentation() {
        return representation;
    }

    public static int getQuantity() {
        return quantity;
    }

    public static void clear() {
        spectacle = null;
        representation = null;
        quantity = 0;
    }
}
