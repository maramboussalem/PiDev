package tn.esprit.services;

import tn.esprit.entities.ParametresVitaux;
import tn.esprit.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParametresVitauxService implements IService<ParametresVitaux> {

    private final Connection cnx;

    public ParametresVitauxService() {
        this.cnx = MyDataBase.getInstance().getMyConnection();
        if (cnx == null) {
            System.err.println("Erreur: Connexion à la base de données échouée !");
        }
    }

    @Override
    public void ajouter(ParametresVitaux pv) throws SQLException {
        // Modify the SQL query to exclude user_id
        String req = "INSERT INTO parametres_viteaux (name, fc, fr, ecg, tas, tad, age, spo2, gsc, gad, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = cnx.prepareStatement(req)) {
            pstmt.setString(1, pv.getName());
            pstmt.setInt(2, pv.getFc());
            pstmt.setInt(3, pv.getFr());
            pstmt.setString(4, pv.getEcg()); // String
            pstmt.setInt(5, pv.getTas());
            pstmt.setInt(6, pv.getTad());
            pstmt.setInt(7, pv.getAge());
            pstmt.setInt(8, pv.getSpo2());
            pstmt.setInt(9, pv.getGsc());
            pstmt.setFloat(10, (float) pv.getGad());
            pstmt.setTimestamp(11, pv.getCreated_at()); // Timestamp

            // Execute the update
            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Paramètres vitaux ajoutés avec succès !");
            } else {
                System.err.println("Erreur: Les paramètres vitaux n’ont pas été ajoutés.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de l'ajout des paramètres vitaux: " + e.getMessage());
            throw e;
        }
    }



    @Override
    public List<ParametresVitaux> afficher() throws SQLException {
        List<ParametresVitaux> liste = new ArrayList<>();
        String req = "SELECT * FROM parametres_viteaux";

        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ParametresVitaux pv = new ParametresVitaux(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("fc"),
                        rs.getInt("fr"),
                        rs.getString("ecg"), // String
                        rs.getInt("tas"),
                        rs.getInt("tad"),
                        rs.getInt("age"),
                        rs.getInt("spo2"),
                        rs.getInt("gsc"),
                        rs.getFloat("gad"),
                        rs.getInt("user_id"),
                        rs.getTimestamp("created_at") // Timestamp
                );
                liste.add(pv);
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la récupération des paramètres vitaux: " + e.getMessage());
            throw e;
        }

        return liste;
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM parametres_viteaux WHERE id = ?";

        try (PreparedStatement pstmt = cnx.prepareStatement(req)) {
            pstmt.setInt(1, id);
            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Paramètres vitaux supprimés avec succès !");
            } else {
                System.err.println("Erreur: Paramètres vitaux non trouvés.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la suppression: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void modifier(ParametresVitaux pv) throws SQLException {
        String req = "UPDATE parametres_viteaux SET name = ?, fc = ?, fr = ?, ecg = ?, tas = ?, tad = ?, age = ?, spo2 = ?, gsc = ?, gad = ?, user_id = ?, created_at = ? WHERE id = ?";

        try (PreparedStatement pstmt = cnx.prepareStatement(req)) {
            pstmt.setString(1, pv.getName());
            pstmt.setInt(2, pv.getFc());
            pstmt.setInt(3, pv.getFr());
            pstmt.setString(4, pv.getEcg()); // String
            pstmt.setInt(5, pv.getTas());
            pstmt.setInt(6, pv.getTad());
            pstmt.setInt(7, pv.getAge());
            pstmt.setInt(8, pv.getSpo2());
            pstmt.setInt(9, pv.getGsc());
            pstmt.setFloat(10, (float) pv.getGad());
            pstmt.setInt(11, pv.getUserId());
            pstmt.setTimestamp(12, pv.getCreated_at()); // Timestamp
            pstmt.setInt(13, pv.getId());

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Paramètres vitaux mis à jour avec succès !");
            } else {
                System.err.println("Erreur: Paramètres vitaux non trouvés.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la mise à jour: " + e.getMessage());
            throw e;
        }
    }
}
