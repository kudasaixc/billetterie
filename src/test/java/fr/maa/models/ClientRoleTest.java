package fr.maa.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests de la cohérence entre le rôle et les indicateurs is_admin / is_vendeur.
 */
class ClientRoleTest {

    private Client client() {
        return new Client(1, "p", "Nom", "Prenom", "0000", "a@b.c", "pwd", "adr", false);
    }

    @Test
    @DisplayName("Un client créé non-admin a le rôle CLIENT")
    void clientParDefaut() {
        Client c = client();
        assertEquals(Role.CLIENT, c.getRole());
        assertFalse(c.isAdmin());
        assertFalse(c.isVendeur());
    }

    @Test
    @DisplayName("Le constructeur isAdmin=true donne le rôle ADMIN")
    void constructeurAdmin() {
        Client c = new Client(1, "p", "n", "pr", "0", "a@b.c", "pwd", "adr", true);
        assertEquals(Role.ADMIN, c.getRole());
        assertTrue(c.isAdmin());
    }

    @Test
    @DisplayName("Promouvoir VENDEUR active isVendeur mais pas isAdmin")
    void roleVendeur() {
        Client c = client();
        c.setRole(Role.VENDEUR);
        assertTrue(c.isVendeur());
        assertFalse(c.isAdmin());
    }

    @Test
    @DisplayName("Désactiver admin ne rétrograde pas un vendeur")
    void desactiverAdminPreserveVendeur() {
        Client c = client();
        c.setRole(Role.VENDEUR);
        c.setAdmin(false);
        assertEquals(Role.VENDEUR, c.getRole());
    }

    @Test
    @DisplayName("Un rôle null retombe sur CLIENT")
    void roleNull() {
        Client c = client();
        c.setRole(null);
        assertEquals(Role.CLIENT, c.getRole());
    }
}
