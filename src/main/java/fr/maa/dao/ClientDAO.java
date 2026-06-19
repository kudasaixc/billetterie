package fr.maa.dao;

import fr.maa.models.Client;
import fr.maa.utils.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientDAO {

    private static final Logger LOGGER = Logger.getLogger(ClientDAO.class.getName());

    private Connection conn = Database.getConnection();

    public List<Client> getAll() {
        List<Client> list = new ArrayList<>();
        String sql = "SELECT * FROM client";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Client c = new Client(
                        rs.getInt("id"),
                        rs.getString("pseudo"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("numero"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("adresse"),
                        rs.getBoolean("is_admin")
                );
                list.add(c);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur d'accès aux données", e);
        }
        return list;
    }

    public Client getById(int id) {
        String sql = "SELECT * FROM client WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Client(
                        rs.getInt("id"),
                        rs.getString("pseudo"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("numero"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("adresse"),
                        rs.getBoolean("is_admin")
                );
            }
        } catch (SQLException e) { LOGGER.log(Level.SEVERE, "Erreur d'accès aux données", e); }
        return null;
    }

    public boolean insert(Client c) {
        String sql = "INSERT INTO client (pseudo, nom, prenom, numero, email, password, adresse, is_admin) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, c.getPseudo());
            stmt.setString(2, c.getNom());
            stmt.setString(3, c.getPrenom());
            stmt.setString(4, c.getNumero());
            stmt.setString(5, c.getEmail());
            stmt.setString(6, hashPassword(c.getPassword()));
            stmt.setString(7, c.getAdresse());
            stmt.setBoolean(8, c.isAdmin());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { LOGGER.log(Level.SEVERE, "Erreur d'accès aux données", e); }
        return false;
    }

    public boolean update(Client c) {
        String sql = "UPDATE client SET pseudo=?, nom=?, prenom=?, numero=?, email=?, password=?, adresse=?, is_admin=? WHERE id=?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, c.getPseudo());
            stmt.setString(2, c.getNom());
            stmt.setString(3, c.getPrenom());
            stmt.setString(4, c.getNumero());
            stmt.setString(5, c.getEmail());
            stmt.setString(6, hashPassword(c.getPassword()));
            stmt.setString(7, c.getAdresse());
            stmt.setBoolean(8, c.isAdmin());
            stmt.setInt(9, c.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { LOGGER.log(Level.SEVERE, "Erreur d'accès aux données", e); }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM client WHERE id=?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { LOGGER.log(Level.SEVERE, "Erreur d'accès aux données", e); }
        return false;
    }

    public Client login(String email, String password) {
        String sql = "SELECT * FROM client WHERE email = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String hashed = rs.getString("password");
                if (hashed != null && BCrypt.checkpw(password, hashed)) {
                    return new Client(
                            rs.getInt("id"),
                            rs.getString("pseudo"),
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("numero"),
                            rs.getString("email"),
                            hashed,
                            rs.getString("adresse"),
                            rs.getBoolean("is_admin")
                    );
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur d'accès aux données", e);
        }
        return null;
    }

    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM client WHERE email = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur d'accès aux données", e);
        }
        return false;
    }

    public boolean register(Client c) {
        if (c == null || c.getPassword() == null) {
            return false;
        }
        return insert(c);
    }

    private String hashPassword(String password) {
        if (password == null || password.isBlank()) {
            return password;
        }
        if (password.startsWith("$2a$") || password.startsWith("$2b$")) {
            return password;
        }
        return BCrypt.hashPassword(password);
    }
}
