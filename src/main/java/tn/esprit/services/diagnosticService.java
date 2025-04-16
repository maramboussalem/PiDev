package tn.esprit.services;

import tn.esprit.entities.Diagnostic;
import tn.esprit.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class diagnosticService implements IService<Diagnostic> {

    private final Connection cnx;

    public diagnosticService() {
        this.cnx = MyDataBase.getInstance().getMyConnection();
        if (cnx == null) {
            System.err.println("Erreur: Connexion à la base de données échouée !");
        }
    }

    @Override
    public void ajouter(Diagnostic diagnostic) throws SQLException {
        String req = "INSERT INTO diagnostic (name, description, date_diagnostic, patient_id, medecin_id) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = cnx.prepareStatement(req)) {
            pstmt.setString(1, diagnostic.getName());
            pstmt.setString(2, diagnostic.getDescription());
            pstmt.setDate(3, diagnostic.getDate_diagnostic());
            pstmt.setInt(4, diagnostic.getPatient_id());
            pstmt.setInt(5, diagnostic.getMedecin_id());

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Diagnostic ajouté avec succès !");
            } else {
                System.err.println("Erreur: Le diagnostic n’a pas été ajouté.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de l'ajout du diagnostic: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Diagnostic> afficher() throws SQLException {
        List<Diagnostic> diagnostics = new ArrayList<>();
        String req = "SELECT * FROM diagnostic";

        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Diagnostic diagnostic = new Diagnostic(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDate("date_diagnostic"),
                        rs.getInt("patient_id"),
                        rs.getInt("medecin_id")
                );
                diagnostics.add(diagnostic);
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la récupération des diagnostics: " + e.getMessage());
            throw e;
        }

        return diagnostics;
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM diagnostic WHERE id = ?";

        try (PreparedStatement pstmt = cnx.prepareStatement(req)) {
            pstmt.setInt(1, id);
            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Diagnostic supprimé avec succès !");
            } else {
                System.err.println("Erreur: Diagnostic non trouvé.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la suppression du diagnostic: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void modifier(Diagnostic diagnostic) throws SQLException {
        String req = "UPDATE diagnostic SET name = ?, description = ?, date_diagnostic = ?, patient_id = ?, medecin_id = ? WHERE id = ?";

        try (PreparedStatement pstmt = cnx.prepareStatement(req)) {
            pstmt.setString(1, diagnostic.getName());
            pstmt.setString(2, diagnostic.getDescription());
            pstmt.setDate(3, diagnostic.getDate_diagnostic());
            pstmt.setInt(4, diagnostic.getPatient_id());
            pstmt.setInt(5, diagnostic.getMedecin_id());
            pstmt.setInt(6, diagnostic.getId());

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Diagnostic mis à jour avec succès !");
            } else {
                System.err.println("Erreur: Diagnostic non trouvé.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la mise à jour du diagnostic: " + e.getMessage());
            throw e;
        }
    }
}
