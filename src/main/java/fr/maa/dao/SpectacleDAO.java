package fr.maa.dao;

import fr.maa.models.Spectacle;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SpectacleDAO {

    private final Connection conn = Database.getConnection();

    public List<Spectacle> getAll() {
        List<Spectacle> list = new ArrayList<>();
        String sql = "SELECT * FROM spectacle";

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
                        rs.getString("photos")
                );
                list.add(s);
            }

        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean insert(Spectacle s) {
        String sql = "INSERT INTO spectacle (titre, lieu, affiche, tags, duree, description_courte, description_longue, langue, age_minimum, photos) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }

        return false;
    }

    public boolean update(Spectacle s) {
        String sql = "UPDATE spectacle SET titre=?, lieu=?, affiche=?, tags=?, duree=?, description_courte=?, description_longue=?, langue=?, age_minimum=?, photos=? WHERE id=?";

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
            stmt.setInt(11, s.getId());

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
}
