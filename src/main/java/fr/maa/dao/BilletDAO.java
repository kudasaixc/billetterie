package fr.maa.dao;

import fr.maa.models.Billet;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BilletDAO {

    private final Connection conn = Database.getConnection();

    public List<Billet> getAll() {
        List<Billet> list = new ArrayList<>();
        String sql = "SELECT b.*, r.date_heure, s.titre AS spectacle_title, c.nom AS client_nom, c.prenom AS client_prenom " +
                "FROM billet b " +
                "JOIN representation r ON b.id_representation = r.id " +
                "JOIN spectacle s ON r.id_spectacle = s.id " +
                "JOIN client c ON b.id_client = c.id";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Billet b = new Billet(
                        rs.getInt("id"),
                        rs.getString("numero"),
                        rs.getInt("id_representation"),
                        rs.getInt("id_client")
                );
                b.setRepresentationLabel(rs.getString("spectacle_title") + " - " + rs.getTimestamp("date_heure").toLocalDateTime());
                b.setClientName(rs.getString("client_prenom") + " " + rs.getString("client_nom"));
                list.add(b);
            }

        } catch (SQLException e) { e.printStackTrace(); }

        return list;
    }

    public boolean insert(Billet b) {
        String sql = "INSERT INTO billet (numero, id_representation, id_client) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, b.getNumero());
            stmt.setInt(2, b.getIdRepresentation());
            stmt.setInt(3, b.getIdClient());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }

        return false;
    }

    public boolean update(Billet b) {
        String sql = "UPDATE billet SET numero=?, id_representation=?, id_client=? WHERE id=?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, b.getNumero());
            stmt.setInt(2, b.getIdRepresentation());
            stmt.setInt(3, b.getIdClient());
            stmt.setInt(4, b.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }

        return false;
    }

    public boolean delete(int id) {
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM billet WHERE id=?")) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
}
