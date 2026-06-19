package fr.maa.models;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests de la règle métier de disponibilité des places.
 */
class RepresentationTest {

    private Representation representationAvecPlaces(int places) {
        return new Representation(1, 1, LocalDateTime.now(), "Salle A", places, 50.0);
    }

    @Test
    @DisplayName("Assez de places quand la quantité est inférieure au stock")
    void assezDePlacesQuantiteInferieure() {
        assertTrue(representationAvecPlaces(10).hasEnoughPlaces(5));
    }

    @Test
    @DisplayName("Assez de places quand la quantité égale exactement le stock")
    void assezDePlacesQuantiteEgale() {
        assertTrue(representationAvecPlaces(10).hasEnoughPlaces(10));
    }

    @Test
    @DisplayName("Refus quand la capacité est dépassée")
    void refusCapaciteDepassee() {
        assertFalse(representationAvecPlaces(10).hasEnoughPlaces(11));
    }

    @Test
    @DisplayName("Refus quand il ne reste aucune place")
    void refusAucunePlace() {
        assertFalse(representationAvecPlaces(0).hasEnoughPlaces(1));
    }

    @Test
    @DisplayName("Refus pour une quantité nulle ou négative")
    void refusQuantiteInvalide() {
        assertFalse(representationAvecPlaces(10).hasEnoughPlaces(0));
        assertFalse(representationAvecPlaces(10).hasEnoughPlaces(-3));
    }
}
