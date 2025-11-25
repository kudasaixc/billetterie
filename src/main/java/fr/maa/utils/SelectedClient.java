package fr.maa.utils;

import fr.maa.models.Client;

public class SelectedClient {
    private static Client value;

    public static void set(Client c) { value = c; }
    public static Client get() { return value; }
    public static void clear() { value = null; }
}
