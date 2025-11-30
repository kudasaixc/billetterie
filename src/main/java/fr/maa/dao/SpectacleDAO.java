package fr.maa.dao;

import fr.maa.models.Spectacle;
import fr.maa.models.SpectacleStat;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SpectacleDAO {

    private final Connection conn = Database.getConnection();
    private final boolean hasImagePathColumn = checkColumnExists("spectacle", "image_path");

    private boolean checkColumnExists(String tableName, String columnName) {
        try (ResultSet rs = conn.getMetaData().getColumns(null, null, tableName, columnName)) {
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Spectacle> getAll() {
        List<Spectacle> list = new ArrayList<>();
        String sql = hasImagePathColumn
                ? "SELECT * FROM spectacle"
                : "SELECT id, titre, lieu, affiche, tags, duree, description_courte, description_longue, langue, age_minimum, photos FROM spectacle";

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
                list.add(s);
            }

        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean insert(Spectacle s) {
        String sql = hasImagePathColumn
                ? "INSERT INTO spectacle (titre, lieu, affiche, tags, duree, description_courte, description_longue, langue, age_minimum, photos, image_path) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                : "INSERT INTO spectacle (titre, lieu, affiche, tags, duree, description_courte, description_longue, langue, age_minimum, photos) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
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
            if (hasImagePathColumn) {
                stmt.setString(11, s.getImagePath());
            }

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }

        return false;
    }

    public boolean update(Spectacle s) {
        String sql = hasImagePathColumn
                ? "UPDATE spectacle SET titre=?, lieu=?, affiche=?, tags=?, duree=?, description_courte=?, description_longue=?, langue=?, age_minimum=?, photos=?, image_path=? WHERE id=?"
                : "UPDATE spectacle SET titre=?, lieu=?, affiche=?, tags=?, duree=?, description_courte=?, description_longue=?, langue=?, age_minimum=?, photos=? WHERE id=?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
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
            if (hasImagePathColumn) {
                stmt.setString(11, s.getImagePath());
                stmt.setInt(12, s.getId());
            } else {
                stmt.setInt(11, s.getId());
            }

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }

        return false;
    }

    public boolean delete(int id) {
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM spectacle WHERE id=?")) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
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
            e.printStackTrace();
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
            e.printStackTrace();
        }

        return stats;
    }
}
