package fr.maa.utils;

import fr.maa.models.Spectacle;

public class SelectedSpectacle {
    private static Spectacle value;

    public static void set(Spectacle spectacle) { value = spectacle; }
    public static Spectacle get() { return value; }
    public static void clear() { value = null; }
}
