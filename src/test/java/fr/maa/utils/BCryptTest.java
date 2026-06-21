package fr.maa.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests du hachage des mots de passe (BCrypt).
 */
class BCryptTest {

    @Test
    @DisplayName("Le hash est différent du mot de passe en clair")
    void hashDifferentDuClair() {
        String clair = "monMotDePasse123";
        String hash = BCrypt.hashPassword(clair);
        assertNotEquals(clair, hash);
    }

    @Test
    @DisplayName("Un mot de passe correct est validé par son hash")
    void motDePasseCorrectValide() {
        String clair = "admin123";
        String hash = BCrypt.hashPassword(clair);
        assertTrue(BCrypt.checkpw(clair, hash));
    }

    @Test
    @DisplayName("Un mot de passe incorrect est rejeté")
    void motDePasseIncorrectRejete() {
        String hash = BCrypt.hashPassword("admin123");
        assertFalse(BCrypt.checkpw("mauvais", hash));
    }

    @Test
    @DisplayName("Deux hash du même mot de passe diffèrent (sel aléatoire)")
    void selAleatoire() {
        String clair = "memeMotDePasse";
        String hash1 = BCrypt.hashPassword(clair);
        String hash2 = BCrypt.hashPassword(clair);
        assertNotEquals(hash1, hash2);
    }

    @Test
    @DisplayName("Deux hash distincts du même mot de passe restent valides")
    void hashAvecSelsDifferentsRestentValides() {
        String clair = "memeMotDePasse";
        String hash1 = BCrypt.hashPassword(clair);
        String hash2 = BCrypt.hashPassword(clair);
        assertTrue(BCrypt.checkpw(clair, hash1));
        assertTrue(BCrypt.checkpw(clair, hash2));
    }
}
