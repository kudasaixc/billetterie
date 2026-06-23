package fr.maa.dao;

import fr.maa.models.Spectacle;
import fr.maa.models.SpectacleStat;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SpectacleDAO {

    private static final Logger LOGGER = Logger.getLogger(SpectacleDAO.class.getName());

    private final Connection conn = Database.getConnection();
    private final boolean hasImagePathColumn = checkColumnExists("spectacle", "image_path");
    private final boolean hasVendeurColumn = checkColumnExists("spectacle", "id_vendeur");

    private boolean checkColumnExists(String tableName, String columnName) {
        try (ResultSet rs = conn.getMetaData().getColumns(null, null, tableName, columnName)) {
            return rs.next();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur d'accès aux données", e);
        }
        return false;
    }

    public List<Spectacle> getAll() {
        List<Spectacle> list = new ArrayList<>();
        // `SELECT *` couvre les colonnes optionnelles (image_path, id_vendeur)
        // si elles existent ; sinon on liste explicitement les colonnes de base.
        String sql = hasImagePathColumn
                ? "SELECT * FROM spectacle"
                : "SELECT id, titre, lieu, affiche, tags, duree, description_courte, description_longue, langue, age_minimum, photos"
                  + (hasVendeurColumn ? ", id_vendeur" : "")
                  + " FROM spectacle";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Spectacle s = new Spectacle(
                        rs.getInt("id"),
                        rs.getString("titre"),
                        rs.getString("lieu"),
                        rs.getString("affiche"),
                        rs.getString("tags"),
                        rs.getInt("duree"),
                        rs.getString("description_courte"),
                        rs.getString("description_longue"),
                        rs.getString("langue"),
                        rs.getInt("age_minimum"),
                        rs.getString("photos"),
                        hasImagePathColumn ? rs.getString("image_path") : null
                );
                if (hasVendeurColumn) {
                    int idVendeur = rs.getInt("id_vendeur");
                    s.setIdVendeur(rs.wasNull() ? null : idVendeur);
                }
                list.add(s);
            }

        } catch (SQLException e) { LOGGER.log(Level.SEVERE, "Erreur d'accès aux données", e); }
        return list;
    }

    /**
     * Spectacles appartenant à un vendeur donné. Retourne une liste vide si la
     * colonne {@code id_vendeur} n'existe pas encore (migration non appliquée).
     */
    public List<Spectacle> getByVendeur(int vendeurId) {
        List<Spectacle> list = new ArrayList<>();
        if (!hasVendeurColumn) {
            return list;
        }
        String baseColumns = hasImagePathColumn
                ? "*"
                : "id, titre, lieu, affiche, tags, duree, description_courte, description_longue, langue, age_minimum, photos, id_vendeur";
        String sql = "SELECT " + baseColumns + " FROM spectacle WHERE id_vendeur = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, vendeurId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Spectacle s = new Spectacle(
                        rs.getInt("id"),
                        rs.getString("titre"),
                        rs.getString("lieu"),
                        rs.getString("affiche"),
                        rs.getString("tags"),
                        rs.getInt("duree"),
                        rs.getString("description_courte"),
                        rs.getString("description_longue"),
                        rs.getString("langue"),
                        rs.getInt("age_minimum"),
                        rs.getString("photos"),
                        hasImagePathColumn ? rs.getString("image_path") : null
                );
                int idVendeur = rs.getInt("id_vendeur");
                s.setIdVendeur(rs.wasNull() ? null : idVendeur);
                list.add(s);
            }
        } catch (SQLException e) { LOGGER.log(Level.SEVERE, "Erreur d'accès aux données", e); }
        return list;
    }

    public boolean insert(Spectacle s) {
        // Colonnes optionnelles (image_path, id_vendeur) ajoutées dans le même
        // ordre côté colonnes et côté valeurs pour garder un binding cohérent.
        StringBuilder cols = new StringBuilder("titre, lieu, affiche, tags, duree, description_courte, description_longue, langue, age_minimum, photos");
        StringBuilder vals = new StringBuilder("?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
        if (hasImagePathColumn) { cols.append(", image_path"); vals.append(", ?"); }
        if (hasVendeurColumn)   { cols.append(", id_vendeur"); vals.append(", ?"); }
        String sql = "INSERT INTO spectacle (" + cols + ") VALUES (" + vals + ")";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            int i = bindBaseFields(stmt, s);
            if (hasImagePathColumn) {
                stmt.setString(i++, s.getImagePath());
            }
            if (hasVendeurColumn) {
                setIntOrNull(stmt, i++, s.getIdVendeur());
            }

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { LOGGER.log(Level.SEVERE, "Erreur d'accès aux données", e); }

        return false;
    }

    public boolean update(Spectacle s) {
        StringBuilder set = new StringBuilder("titre=?, lieu=?, affiche=?, tags=?, duree=?, description_courte=?, description_longue=?, langue=?, age_minimum=?, photos=?");
        if (hasImagePathColumn) { set.append(", image_path=?"); }
        if (hasVendeurColumn)   { set.append(", id_vendeur=?"); }
        String sql = "UPDATE spectacle SET " + set + " WHERE id=?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            int i = bindBaseFields(stmt, s);
            if (hasImagePathColumn) {
                stmt.setString(i++, s.getImagePath());
            }
            if (hasVendeurColumn) {
                setIntOrNull(stmt, i++, s.getIdVendeur());
            }
            stmt.setInt(i, s.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { LOGGER.log(Level.SEVERE, "Erreur d'accès aux données", e); }

        return false;
    }

    /** Lie les 10 colonnes de base et renvoie l'index du prochain paramètre. */
    private int bindBaseFields(PreparedStatement stmt, Spectacle s) throws SQLException {
        stmt.setString(1, s.getTitre());
        stmt.setString(2, s.getLieu());
        stmt.setString(3, s.getAffiche());
        stmt.setString(4, s.getTags());
        stmt.setInt(5, s.getDuree());
        stmt.setString(6, s.getDescriptionCourte());
        stmt.setString(7, s.getDescriptionLongue());
        stmt.setString(8, s.getLangue());
        stmt.setInt(9, s.getAgeMinimum());
        stmt.setString(10, s.getPhotos());
        return 11;
    }

    private void setIntOrNull(PreparedStatement stmt, int index, Integer value) throws SQLException {
        if (value == null) {
            stmt.setNull(index, Types.INTEGER);
        } else {
            stmt.setInt(index, value);
        }
    }

    public boolean delete(int id) {
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM spectacle WHERE id=?")) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { LOGGER.log(Level.SEVERE, "Erreur d'accès aux données", e); }
        return false;
    }

    public int getTotalSpectacles() {
        String sql = "SELECT COUNT(*) AS total FROM spectacle";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur d'accès aux données", e);
        }
        return 0;
    }

    public List<SpectacleStat> getTopSpectacles() {
        List<SpectacleStat> stats = new ArrayList<>();
        String sql = "SELECT s.titre, COUNT(b.id) AS billets_vendus, COALESCE(SUM(b.prix), 0) AS chiffre_affaires " +
                "FROM spectacle s " +
                "JOIN representation r ON r.id_spectacle = s.id " +
                "LEFT JOIN billet b ON b.id_representation = r.id " +
                "GROUP BY s.id, s.titre " +
                "ORDER BY billets_vendus DESC " +
                "LIMIT 5";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                stats.add(new SpectacleStat(
                        rs.getString("titre"),
                        rs.getInt("billets_vendus"),
                        rs.getDouble("chiffre_affaires")
                ));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur d'accès aux données", e);
        }

        return stats;
    }
}
