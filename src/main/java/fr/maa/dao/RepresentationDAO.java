package fr.maa.dao;

import fr.maa.models.Representation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepresentationDAO {

    private final Connection conn = Database.getConnection();

    public List<Representation> getAll() {
        List<Representation> list = new ArrayList<>();
        String sql = "SELECT r.*, s.titre AS spectacle_title FROM representation r JOIN spectacle s ON r.id_spectacle = s.id";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Representation r = new Representation(
                        rs.getInt("id"),
                        rs.getInt("id_spectacle"),
                        rs.getTimestamp("date_heure").toLocalDateTime(),
                        rs.getString("salle"),
                        rs.getInt("places_disponibles"),
                        rs.getDouble("prix")
                );
                r.setSpectacleTitle(rs.getString("spectacle_title"));
                list.add(r);
            }

        } catch (SQLException e) { e.printStackTrace(); }

        return list;
    }

    public List<Representation> getBySpectacle(int spectacleId) {
        List<Representation> list = new ArrayList<>();
        String sql = "SELECT r.*, s.titre AS spectacle_title FROM representation r " +
                "JOIN spectacle s ON r.id_spectacle = s.id WHERE r.id_spectacle = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, spectacleId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Representation r = new Representation(
                        rs.getInt("id"),
                        rs.getInt("id_spectacle"),
                        rs.getTimestamp("date_heure").toLocalDateTime(),
                        rs.getString("salle"),
                        rs.getInt("places_disponibles"),
                        rs.getDouble("prix")
                );
                r.setSpectacleTitle(rs.getString("spectacle_title"));
                list.add(r);
            }
        } catch (SQLException e) { e.printStackTrace(); }

        return list;
    }

    public Representation getById(int id) {
        String sql = "SELECT r.*, s.titre AS spectacle_title FROM representation r " +
                "JOIN spectacle s ON r.id_spectacle = s.id WHERE r.id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Representation r = new Representation(
                        rs.getInt("id"),
                        rs.getInt("id_spectacle"),
                        rs.getTimestamp("date_heure").toLocalDateTime(),
                        rs.getString("salle"),
                        rs.getInt("places_disponibles"),
                        rs.getDouble("prix")
                );
                r.setSpectacleTitle(rs.getString("spectacle_title"));
                return r;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean insert(Representation r) {
        String sql = "INSERT INTO representation (id_spectacle, date_heure, salle, places_disponibles, prix) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, r.getIdSpectacle());
            stmt.setTimestamp(2, Timestamp.valueOf(r.getDateHeure()));
            stmt.setString(3, r.getSalle());
            stmt.setInt(4, r.getPlacesDisponibles());
            stmt.setDouble(5, r.getPrix());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }

        return false;
    }

    public boolean decrementPlaces(int id, int quantity) {
        String sql = "UPDATE representation SET places_disponibles = places_disponibles - ? " +
                "WHERE id = ? AND places_disponibles >= ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quantity);
            stmt.setInt(2, id);
            stmt.setInt(3, quantity);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean delete(int id) {
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM representation WHERE id=?")) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
}
