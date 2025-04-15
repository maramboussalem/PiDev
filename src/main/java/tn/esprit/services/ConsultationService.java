package tn.esprit.services;

import tn.esprit.entities.Consultation;
import tn.esprit.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConsultationService implements IService<Consultation> {

    private final Connection cnx;

    public ConsultationService() {
        this.cnx = MyDataBase.getInstance().getMyConnection(); // ✅ Changed to match your method
        if (cnx == null) {
            System.err.println("Erreur: Connexion à la base de données échouée !");
        }
    }

    @Override
    public void ajouter(Consultation consultation) throws SQLException {
        String req = "INSERT INTO consultation (nom_patient, date_consultation, diagnostic, user_id) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = cnx.prepareStatement(req)) {
            pstmt.setString(1, consultation.getNomPatient());
            pstmt.setDate(2, new Date(consultation.getDateConsultation().getTime()));
            pstmt.setString(3, consultation.getDiagnostic());
            pstmt.setInt(4, consultation.getUserId());

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Consultation ajoutée avec succès !");
            } else {
                System.err.println("Erreur: La consultation n’a pas été ajoutée.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de l'ajout de la consultation: " + e.getMessage());
            throw e; // Re-throw exception to maintain consistency with interface
        }
    }

    @Override
    public List<Consultation> afficher() throws SQLException {
        List<Consultation> consultations = new ArrayList<>();
        String req = "SELECT * FROM consultation";

        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Consultation consultation = new Consultation(
                        rs.getInt("id"),
                        rs.getString("nom_patient"),
                        rs.getDate("date_consultation"),
                        rs.getString("diagnostic"),
                        rs.getInt("user_id")
                );
                consultations.add(consultation);
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la récupération des consultations: " + e.getMessage());
            throw e; // Re-throw exception to maintain consistency with interface
        }

        return consultations;
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM consultation WHERE id = ?";

        try (PreparedStatement pstmt = cnx.prepareStatement(req)) {
            pstmt.setInt(1, id);
            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Consultation supprimée avec succès !");
            } else {
                System.err.println("Erreur: Consultation non trouvée.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la suppression de la consultation: " + e.getMessage());
            throw e; // Re-throw exception to maintain consistency with interface
        }
    }

    @Override
    public void modifier(Consultation consultation) throws SQLException {
        String req = "UPDATE consultation SET nom_patient = ?, date_consultation = ?, diagnostic = ?, user_id = ? WHERE id = ?";

        try (PreparedStatement pstmt = cnx.prepareStatement(req)) {
            pstmt.setString(1, consultation.getNomPatient());
            pstmt.setDate(2, new Date(consultation.getDateConsultation().getTime()));
            pstmt.setString(3, consultation.getDiagnostic());
            pstmt.setInt(4, consultation.getUserId());
            pstmt.setInt(5, consultation.getId());

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Consultation mise à jour avec succès !");
            } else {
                System.err.println("Erreur: Consultation non trouvée.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la mise à jour de la consultation: " + e.getMessage());
            throw e; // Re-throw exception to maintain consistency with interface
        }
    }
}
