package fr.maa.utils;

import fr.maa.models.Representation;

public class SelectedRepresentation {
    private static Representation value;

    public static void set(Representation representation) { value = representation; }
    public static Representation get() { return value; }
    public static void clear() { value = null; }
}
