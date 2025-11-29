package fr.maa.utils;

import fr.maa.models.Billet;

public class SelectedBillet {
    private static Billet value;

    public static void set(Billet billet) { value = billet; }
    public static Billet get() { return value; }
    public static void clear() { value = null; }
}
