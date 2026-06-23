package fr.maa.models;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests du calcul du taux de remplissage par spectacle.
 */
class RemplissageStatTest {

    @Test
    @DisplayName("La capacité totale est la somme des places restantes et des billets vendus")
    void capaciteTotale() {
        RemplissageStat stat = new RemplissageStat("Spectacle", 30, 70);
        assertEquals(100, stat.getPlacesTotales());
    }

    @Test
    @DisplayName("Le taux de remplissage est vendus / capacité totale")
    void tauxRemplissage() {
        RemplissageStat stat = new RemplissageStat("Spectacle", 30, 70);
        assertEquals(0.30, stat.getTauxRemplissage(), 1e-9);
    }

    @Test
    @DisplayName("Salle complète : taux à 100%")
    void salleComplete() {
        RemplissageStat stat = new RemplissageStat("Spectacle", 100, 0);
        assertEquals(1.0, stat.getTauxRemplissage(), 1e-9);
    }

    @Test
    @DisplayName("Aucune place : taux à 0 sans division par zéro")
    void aucunePlace() {
        RemplissageStat stat = new RemplissageStat("Spectacle", 0, 0);
        assertEquals(0.0, stat.getTauxRemplissage(), 1e-9);
        assertEquals(0, stat.getPlacesTotales());
    }
}
