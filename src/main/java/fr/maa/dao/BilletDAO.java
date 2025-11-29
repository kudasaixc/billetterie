package fr.maa.dao;

import fr.maa.models.Billet;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BilletDAO {

    private final Connection conn = Database.getConnection();

    public List<Billet> getAll() {
        List<Billet> list = new ArrayList<>();
        String sql = "SELECT * FROM billet";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Billet b = new Billet(
                        rs.getInt("id"),
                        rs.getString("numero"),
                        rs.getInt("id_representation"),
                        rs.getInt("id_client")
                );
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
