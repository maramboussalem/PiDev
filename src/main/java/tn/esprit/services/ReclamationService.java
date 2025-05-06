package tn.esprit.services;

import tn.esprit.entities.Reclamation;
import tn.esprit.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReclamationService implements IService<Reclamation> {

    private final Connection cnx;

    public ReclamationService() {
        this.cnx = MyDataBase.getInstance().getMyConnection();
        if (cnx == null) {
            System.err.println("Erreur: Connexion à la base de données échouée !");
        }
    }

    @Override
    public void ajouter(Reclamation r) throws SQLException {
        String req = "INSERT INTO reclamation (consultation_id, utilisateur_id, sujet, description, statut, date_creation, reponse) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = cnx.prepareStatement(req)) {
            if (r.getConsultationId() != null) {
                pstmt.setInt(1, r.getConsultationId());
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }
            pstmt.setString(2, r.getUtilisateurId());
            pstmt.setString(3, r.getSujet());
            pstmt.setString(4, r.getDescription());
            pstmt.setString(5, r.getStatut());
            pstmt.setTimestamp(6, Timestamp.valueOf(r.getDateCreation()));
            pstmt.setString(7, r.getReponse());

            pstmt.executeUpdate();
            System.out.println("Reclamation ajoutée avec succès !");
        }
    }

    @Override
    public List<Reclamation> afficher() throws SQLException {
        List<Reclamation> list = new ArrayList<>();
        String req = "SELECT * FROM reclamation";

        try (PreparedStatement pstmt = cnx.prepareStatement(req);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Reclamation r = new Reclamation();
                r.setId(rs.getInt("id"));
                r.setConsultationId(rs.getObject("consultation_id") != null ? rs.getInt("consultation_id") : null);
                r.setUtilisateurId(rs.getString("utilisateur_id"));
                r.setSujet(rs.getString("sujet"));
                r.setDescription(rs.getString("description"));
                r.setStatut(rs.getString("statut"));
                r.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
                r.setReponse(rs.getString("reponse"));

                list.add(r);
            }
        }
        return list;
    }

    @Override
    public void supprimer(int id) throws SQLException {
        // First, delete associated messages from conversation_messages
        String deleteMessagesQuery = "DELETE FROM conversation_messages WHERE conversation_id = ?";
        try (PreparedStatement pstmt = cnx.prepareStatement(deleteMessagesQuery)) {
            pstmt.setInt(1, id); // conversation_id is set to reclamation.getId()
            int rowsDeleted = pstmt.executeUpdate();
            System.out.println(rowsDeleted + " messages supprimés pour la réclamation ID: " + id);
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la suppression des messages: " + e.getMessage());
            throw e;
        }

        // Then, delete the reclamation
        String deleteReclamationQuery = "DELETE FROM reclamation WHERE id = ?";
        try (PreparedStatement pstmt = cnx.prepareStatement(deleteReclamationQuery)) {
            pstmt.setInt(1, id);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Reclamation supprimée avec succès !");
            } else {
                System.err.println("Aucune reclamation trouvée avec l'id donné.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la suppression de la réclamation: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void modifier(Reclamation r) throws SQLException {
        String req = "UPDATE reclamation SET consultation_id = ?, utilisateur_id = ?, sujet = ?, description = ?, statut = ?, date_creation = ?, reponse = ? WHERE id = ?";

        try (PreparedStatement pstmt = cnx.prepareStatement(req)) {
            if (r.getConsultationId() != null) {
                pstmt.setInt(1, r.getConsultationId());
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }
            pstmt.setString(2, r.getUtilisateurId());
            pstmt.setString(3, r.getSujet());
            pstmt.setString(4, r.getDescription());
            pstmt.setString(5, r.getStatut());
            pstmt.setTimestamp(6, Timestamp.valueOf(r.getDateCreation()));
            pstmt.setString(7, r.getReponse());
            pstmt.setInt(8, r.getId());

            int updated = pstmt.executeUpdate();
            if (updated > 0) {
                System.out.println("Reclamation mise à jour avec succès !");
            } else {
                System.err.println("Reclamation non trouvée.");
            }
        }
    }

    public String getUtilisateurTelephoneById(int utilisateurId) throws SQLException {
        String telephone = null;
        String req = "SELECT telephone FROM utilisateur WHERE id = ?";

        try (PreparedStatement pstmt = cnx.prepareStatement(req)) {
            pstmt.setInt(1, utilisateurId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                telephone = rs.getString("telephone");
            }
        }
        return telephone;
    }
}