package fr.maa.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests de la logique de génération des numéros de billet (sans base de données).
 */
class BilletNumeroGeneratorTest {

    @Test
    @DisplayName("Le préfixe contient l'année encadrée par B et -")
    void prefixContientAnnee() {
        assertEquals("B2025-", BilletNumeroGenerator.prefix(2025));
    }

    @Test
    @DisplayName("Le numéro est formaté sur 6 chiffres")
    void formatSur6Chiffres() {
        assertEquals("B2025-000001", BilletNumeroGenerator.format(2025, 1));
        assertEquals("B2025-000042", BilletNumeroGenerator.format(2025, 42));
        assertEquals("B2025-123456", BilletNumeroGenerator.format(2025, 123456));
    }

    @Test
    @DisplayName("La séquence suivante incrémente le dernier numéro")
    void sequenceSuivanteIncremente() {
        assertEquals(6, BilletNumeroGenerator.nextSequence("B2025-000005"));
        assertEquals(124, BilletNumeroGenerator.nextSequence("B2025-000123"));
    }

    @Test
    @DisplayName("La séquence repart à 1 quand il n'y a pas de numéro existant")
    void sequenceReprendA1SiNull() {
        assertEquals(1, BilletNumeroGenerator.nextSequence(null));
    }

    @Test
    @DisplayName("La séquence repart à 1 si le dernier numéro est mal formé")
    void sequenceReprendA1SiMalForme() {
        assertEquals(1, BilletNumeroGenerator.nextSequence("numero-invalide"));
        assertEquals(1, BilletNumeroGenerator.nextSequence("B2025-abc"));
        assertEquals(1, BilletNumeroGenerator.nextSequence("sansTiret"));
    }

    @Test
    @DisplayName("Chaînage : dernier numéro -> numéro suivant complet")
    void chainageNumeroSuivant() {
        String dernier = "B2025-000009";
        String suivant = BilletNumeroGenerator.format(2025, BilletNumeroGenerator.nextSequence(dernier));
        assertEquals("B2025-000010", suivant);
    }
}
