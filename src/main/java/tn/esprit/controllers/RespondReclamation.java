package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import tn.esprit.entities.Reclamation;
import tn.esprit.entities.Utilisateur;
import tn.esprit.services.ReclamationService;
import tn.esprit.services.WhatsAppSender;

import java.sql.SQLException;

public class RespondReclamation {

    private Reclamation reclamation;
    private AdminReclamation adminReclamation;
    private final ReclamationService service = new ReclamationService();

    @FXML
    private TextArea responseText;

    @FXML
    private ComboBox<String> statusCombo;

    // Method to set Reclamation and AdminReclamation
    public void setReclamation(Reclamation reclamation, AdminReclamation adminReclamation) {
        this.reclamation = reclamation;
        this.adminReclamation = adminReclamation;
    }

    @FXML
    public void handleSubmit() {
        String response = responseText.getText();
        String status = statusCombo.getValue();

        // Update the reclamation object
        reclamation.setReponse(response);
        reclamation.setStatut(status);

        try {
            // Update reclamation in DB
            service.modifier(reclamation);

            // For testing, directly set the phone number (bypassing DB fetch)
            String utilisateurTelephone = "92856008";  // Set the phone number directly for testing

            if (utilisateurTelephone != null && !utilisateurTelephone.isBlank()) {
                // Create a Utilisateur with the retrieved phone number
                Utilisateur utilisateur = new Utilisateur();
                utilisateur.setTelephone(utilisateurTelephone);  // Set the phone number directly

                // Prepare the message
                String messageBody = "✅ Votre réclamation a été traitée !\n\n" +
                        "📩 Réponse: " + response + "\n" +
                        "📌 Statut: " + status;

                // Send WhatsApp notification
                WhatsAppSender.sendWhatsAppMessage(utilisateur, messageBody);
            }

            // Refresh the reclamation list in admin view
            if (adminReclamation != null) {
                adminReclamation.refreshList();
            }

            // Close the window
            Stage stage = (Stage) responseText.getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}