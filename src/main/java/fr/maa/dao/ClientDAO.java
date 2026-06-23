package fr.maa.dao;

import fr.maa.models.Client;
import fr.maa.models.Role;
import fr.maa.utils.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientDAO {

    private static final Logger LOGGER = Logger.getLogger(ClientDAO.class.getName());

    private Connection conn = Database.getConnection();

    // Présence optionnelle de la colonne `role` : le code fonctionne que la
    // migration SQL ait été appliquée ou non (même approche que SpectacleDAO).
    private final boolean hasRoleColumn = checkColumnExists("client", "role");

    private boolean checkColumnExists(String tableName, String columnName) {
        try (ResultSet rs = conn.getMetaData().getColumns(null, null, tableName, columnName)) {
            return rs.next();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur d'accès aux données", e);
        }
        return false;
    }

    /**
     * Construit un {@link Client} depuis la ligne courante. Le rôle provient de
     * la colonne `role` si elle existe ; sinon il est déduit de `is_admin`.
     */
    private Client mapRow(ResultSet rs) throws SQLException {
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
        if (hasRoleColumn) {
            Role role = Role.fromString(rs.getString("role"));
            if (role != null) {
                c.setRole(role);
            }
        }
        return c;
    }

    public List<Client> getAll() {
        List<Client> list = new ArrayList<>();
        String sql = "SELECT * FROM client";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur d'accès aux données", e);
        }
        return list;
    }

    /**
     * Liste les utilisateurs ayant le rôle VENDEUR (pour l'affectation d'un
     * spectacle). Retourne une liste vide si la colonne `role` n'existe pas
     * encore (migration non appliquée).
     */
    public List<Client> getVendeurs() {
        List<Client> list = new ArrayList<>();
        if (!hasRoleColumn) {
            return list;
        }
        String sql = "SELECT * FROM client WHERE role = 'VENDEUR' ORDER BY nom, prenom";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
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
                return mapRow(rs);
            }
        } catch (SQLException e) { LOGGER.log(Level.SEVERE, "Erreur d'accès aux données", e); }
        return null;
    }

    public boolean insert(Client c) {
        String sql = hasRoleColumn
                ? "INSERT INTO client (pseudo, nom, prenom, numero, email, password, adresse, is_admin, role) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
                : "INSERT INTO client (pseudo, nom, prenom, numero, email, password, adresse, is_admin) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, c.getPseudo());
            stmt.setString(2, c.getNom());
            stmt.setString(3, c.getPrenom());
            stmt.setString(4, c.getNumero());
            stmt.setString(5, c.getEmail());
            stmt.setString(6, hashPassword(c.getPassword()));
            stmt.setString(7, c.getAdresse());
            stmt.setBoolean(8, c.isAdmin());
            if (hasRoleColumn) {
                stmt.setString(9, c.getRole().name());
            }

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { LOGGER.log(Level.SEVERE, "Erreur d'accès aux données", e); }
        return false;
    }

    public boolean update(Client c) {
        String sql = hasRoleColumn
                ? "UPDATE client SET pseudo=?, nom=?, prenom=?, numero=?, email=?, password=?, adresse=?, is_admin=?, role=? WHERE id=?"
                : "UPDATE client SET pseudo=?, nom=?, prenom=?, numero=?, email=?, password=?, adresse=?, is_admin=? WHERE id=?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, c.getPseudo());
            stmt.setString(2, c.getNom());
            stmt.setString(3, c.getPrenom());
            stmt.setString(4, c.getNumero());
            stmt.setString(5, c.getEmail());
            stmt.setString(6, hashPassword(c.getPassword()));
            stmt.setString(7, c.getAdresse());
            stmt.setBoolean(8, c.isAdmin());
            if (hasRoleColumn) {
                stmt.setString(9, c.getRole().name());
                stmt.setInt(10, c.getId());
            } else {
                stmt.setInt(9, c.getId());
            }

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

    /**
     * Authentifie un client.
     *
     * @return le {@link Client} si l'authentification réussit,
     *         {@code null} si l'email est inconnu ou le mot de passe incorrect.
     * @throws SQLException si la base de données est injoignable ou mal
     *         configurée. L'appelant peut ainsi distinguer un échec technique
     *         d'un simple échec d'identifiants et afficher un message adapté.
     */
    public Client login(String email, String password) throws SQLException {
        Connection c = Database.getConnection();
        if (c == null) {
            throw new SQLException("Connexion à la base de données indisponible.");
        }

        String sql = "SELECT * FROM client WHERE email = ?";
        try (PreparedStatement stmt = c.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String hashed = rs.getString("password");
                if (hashed != null && BCrypt.checkpw(password, hashed)) {
                    return mapRow(rs);
                }
            }
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
