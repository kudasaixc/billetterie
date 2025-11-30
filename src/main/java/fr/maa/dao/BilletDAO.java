package fr.maa.dao;

import fr.maa.models.Billet;
import fr.maa.models.SpectacleStat;
import fr.maa.models.VenteParJour;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            while (rs.next()) {
                Billet b = new Billet(
                        rs.getInt("id"),
                        rs.getString("numero"),
                        rs.getInt("id_representation"),
                        rs.getInt("id_client"),
                        rs.getDouble("prix"),
                        rs.getTimestamp("date_achat").toLocalDateTime()
                );
                b.setRepresentationLabel(rs.getString("spectacle_title") + " - " +
                        rs.getTimestamp("date_heure").toLocalDateTime().format(formatter));
                b.setSpectacleTitle(rs.getString("spectacle_title"));
                b.setClientName(rs.getString("client_prenom") + " " + rs.getString("client_nom"));
                list.add(b);
            }

        } catch (SQLException e) { e.printStackTrace(); }

        return list;
    }

    public List<Billet> getAllByClient(int clientId) {
        List<Billet> list = new ArrayList<>();
        String sql = "SELECT b.*, r.date_heure, s.titre AS spectacle_title, c.nom AS client_nom, c.prenom AS client_prenom " +
                "FROM billet b " +
                "JOIN representation r ON b.id_representation = r.id " +
                "JOIN spectacle s ON r.id_spectacle = s.id " +
                "JOIN client c ON b.id_client = c.id " +
                "WHERE b.id_client = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clientId);
            ResultSet rs = stmt.executeQuery();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            while (rs.next()) {
                Billet b = new Billet(
                        rs.getInt("id"),
                        rs.getString("numero"),
                        rs.getInt("id_representation"),
                        rs.getInt("id_client"),
                        rs.getDouble("prix"),
                        rs.getTimestamp("date_achat").toLocalDateTime()
                );
                b.setRepresentationLabel(rs.getString("spectacle_title") + " - " +
                        rs.getTimestamp("date_heure").toLocalDateTime().format(formatter));
                b.setSpectacleTitle(rs.getString("spectacle_title"));
                b.setClientName(rs.getString("client_prenom") + " " + rs.getString("client_nom"));
                list.add(b);
            }

        } catch (SQLException e) { e.printStackTrace(); }

        return list;
    }

    public boolean insert(Billet b) {
        String sql = "INSERT INTO billet (numero, id_representation, id_client, prix, date_achat) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, b.getNumero());
            stmt.setInt(2, b.getIdRepresentation());
            stmt.setInt(3, b.getIdClient());
            stmt.setDouble(4, b.getPrix());
            stmt.setTimestamp(5, Timestamp.valueOf(b.getDateAchat()));

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }

        return false;
    }

    public boolean update(Billet b) {
        String sql = "UPDATE billet SET numero=?, id_representation=?, id_client=?, prix=?, date_achat=? WHERE id=?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, b.getNumero());
            stmt.setInt(2, b.getIdRepresentation());
            stmt.setInt(3, b.getIdClient());
            stmt.setDouble(4, b.getPrix());
            stmt.setTimestamp(5, Timestamp.valueOf(b.getDateAchat()));
            stmt.setInt(6, b.getId());

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

    public int getTotalBilletsVendus() {
        String sql = "SELECT COUNT(*) AS total FROM billet";
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

    public double getChiffreAffaires() {
        String sql = "SELECT COALESCE(SUM(prix), 0) AS ca FROM billet";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("ca");
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

    public List<VenteParJour> getVentesParJour() {
        List<VenteParJour> ventes = new ArrayList<>();
        String sql = "SELECT DATE(date_achat) AS jour, COUNT(*) AS total FROM billet GROUP BY jour ORDER BY jour";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ventes.add(new VenteParJour(
                        rs.getDate("jour").toLocalDate(),
                        rs.getInt("total")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ventes;
    }

    public String generateNumero() {
        int year = LocalDate.now().getYear();
        String prefix = "B" + year + "-";
        String sql = "SELECT numero FROM billet WHERE numero LIKE ? ORDER BY numero DESC LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, prefix + "%");
            ResultSet rs = stmt.executeQuery();
            int next = 1;
            if (rs.next()) {
                String numero = rs.getString("numero");
                String[] parts = numero.split("-");
                if (parts.length == 2) {
                    try {
                        next = Integer.parseInt(parts[1]) + 1;
                    } catch (NumberFormatException ignored) {
                        next = 1;
                    }
                }
            }
            return String.format("%s%06d", prefix, next);
        } catch (SQLException e) {
            e.printStackTrace();
            return String.format("%s%06d", prefix, (int) (System.currentTimeMillis() % 1_000_000));
        }
    }
}
