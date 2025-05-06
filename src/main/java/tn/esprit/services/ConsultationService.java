package tn.esprit.services;

import tn.esprit.entities.Consultation;
import tn.esprit.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConsultationService {

    private final Connection cnx;

    public ConsultationService() {
        this.cnx = MyDataBase.getInstance().getMyConnection();
        if (cnx == null) {
            System.err.println("ConsultationService: Erreur - Connexion à la base de données échouée !");
        } else {
            System.out.println("ConsultationService: Connexion à la base de données établie avec succès.");
        }
    }

    public void ajouter(Consultation c) throws SQLException {
        String req = "INSERT INTO consultation (user_id, nom_patient, date_consultation, diagnostic) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = cnx.prepareStatement(req)) {
            pstmt.setInt(1, c.getUserId());
            pstmt.setString(2, c.getNomPatient());
            pstmt.setDate(3, c.getDateConsultation());
            pstmt.setString(4, c.getDiagnostic());

            pstmt.executeUpdate();
            System.out.println("Consultation ajoutée avec succès !");
        }
    }

    public List<Consultation> afficher() throws SQLException {
        List<Consultation> list = new ArrayList<>();
        String req = "SELECT * FROM consultation";

        try (PreparedStatement pstmt = cnx.prepareStatement(req);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Consultation c = new Consultation();
                c.setId(rs.getInt("id"));
                c.setUserId(rs.getInt("user_id"));
                c.setNomPatient(rs.getString("nom_patient"));
                c.setDateConsultation(rs.getDate("date_consultation"));
                c.setDiagnostic(rs.getString("diagnostic"));

                list.add(c);
            }
        }
        return list;
    }

    public List<Consultation> afficherByUserId(int userId) throws SQLException {
        if (cnx == null) {
            throw new SQLException("Connexion à la base de données non établie.");
        }

        List<Consultation> list = new ArrayList<>();
        String req = "SELECT * FROM consultation WHERE user_id = ?";

        try (PreparedStatement pstmt = cnx.prepareStatement(req)) {
            pstmt.setInt(1, userId);
            System.out.println("ConsultationService: Executing query: " + req + " with userId: " + userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Consultation c = new Consultation();
                c.setId(rs.getInt("id"));
                c.setUserId(rs.getInt("user_id"));
                c.setNomPatient(rs.getString("nom_patient"));
                c.setDateConsultation(rs.getDate("date_consultation"));
                c.setDiagnostic(rs.getString("diagnostic"));
                list.add(c);
                System.out.println("ConsultationService: Found consultation - " + c);
            }
            System.out.println("ConsultationService: Total consultations found for userId " + userId + ": " + list.size());
        }
        return list;
    }

    public Consultation getConsultationById(int id) throws SQLException {
        if (cnx == null) {
            throw new SQLException("Connexion à la base de données non établie.");
        }

        String req = "SELECT * FROM consultation WHERE id = ?";

        try (PreparedStatement pstmt = cnx.prepareStatement(req)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Consultation c = new Consultation();
                c.setId(rs.getInt("id"));
                c.setUserId(rs.getInt("user_id"));
                c.setNomPatient(rs.getString("nom_patient"));
                c.setDateConsultation(rs.getDate("date_consultation"));
                c.setDiagnostic(rs.getString("diagnostic"));
                return c;
            }
        }
        return null;
    }

    public void modifier(Consultation c) throws SQLException {
        if (cnx == null) {
            throw new SQLException("Connexion à la base de données non établie.");
        }

        String req = "UPDATE consultation SET user_id = ?, nom_patient = ?, date_consultation = ?, diagnostic = ? WHERE id = ?";

        try (PreparedStatement pstmt = cnx.prepareStatement(req)) {
            pstmt.setInt(1, c.getUserId());
            pstmt.setString(2, c.getNomPatient());
            pstmt.setDate(3, c.getDateConsultation());
            pstmt.setString(4, c.getDiagnostic());
            pstmt.setInt(5, c.getId());

            int updated = pstmt.executeUpdate();
            if (updated > 0) {
                System.out.println("Consultation mise à jour avec succès !");
            } else {
                System.err.println("Consultation non trouvée.");
            }
        }
    }

    public void supprimer(int id) throws SQLException {
        if (cnx == null) {
            throw new SQLException("Connexion à la base de données non établie.");
        }

        String req = "DELETE FROM consultation WHERE id = ?";

        try (PreparedStatement pstmt = cnx.prepareStatement(req)) {
            pstmt.setInt(1, id);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Consultation supprimée avec succès !");
            } else {
                System.err.println("Aucune consultation trouvée avec l'id donné.");
            }
        }
    }
}