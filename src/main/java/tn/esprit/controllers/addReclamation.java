package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.entities.Consultation;
import tn.esprit.entities.Reclamation;
import tn.esprit.services.ConsultationService;
import tn.esprit.services.ReclamationService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class addReclamation {

    @FXML
    private ComboBox<String> sujet;

    @FXML
    private TextArea description;

    @FXML
    private TextField utilisateurId;

    @FXML
    private TextField consultationId;  // This will display nomPatient but store consultation ID internally

    @FXML
    private Label errorMessage;

    private final ReclamationService service = new ReclamationService();
    private final ConsultationService consultationService = new ConsultationService();
    private int consultationIdInternal; // Variable to store the actual consultationId for submission

    // List of bad words to filter (you can expand this list as needed)
    private final List<String> badWords = Arrays.asList(
            "fuck", "bitch", "shit", "asshole", "damn", "bastard", "cunt", "dick", "piss", "whore"
    );

    @FXML
    void initialize() {
        // Populate the ComboBox with predefined subjects
        List<String> sujets = Arrays.asList(
                "Problème technique",
                "Manque de communication avec le docteur",
                "Erreur de facturation",
                "Service non satisfaisant"
        );
        sujet.getItems().addAll(sujets);
    }

    @FXML
    void submitReclamation(ActionEvent event) throws SQLException {
        errorMessage.setText(""); // Clear previous error messages

        String sujetText = sujet.getValue();
        String descriptionText = description.getText().trim();
        String userId = utilisateurId.getText().trim();
        String consultIdText = consultationId.getText().trim();

        // Validate required fields
        if (sujetText == null || sujetText.isEmpty() || descriptionText.isEmpty() || userId.isEmpty()) {
            errorMessage.setText("Tous les champs obligatoires doivent être remplis.");
            return;
        }

        // Check if consultationId is entered
        if (consultIdText.isEmpty()) {
            errorMessage.setText("Le champ Consultation est obligatoire.");
            return;
        }

        // Check for bad words in the description
        if (containsBadWords(descriptionText)) {
            errorMessage.setText("La description contient des mots inappropriés. Veuillez utiliser un langage respectueux.");
            return;
        }

        // Proceed with submission
        Reclamation r = new Reclamation();
        r.setSujet(sujetText);
        r.setDescription(descriptionText);
        r.setUtilisateurId(userId);
        r.setStatut("Attende");
        r.setDateCreation(LocalDateTime.now());
        r.setConsultationId(consultationIdInternal);

        // Add the Reclamation to the database
        service.ajouter(r);
        goToList(event); // Navigate to the list of reclamations
    }

    // Method to check for bad words in the description
    private boolean containsBadWords(String text) {
        String lowerText = text.toLowerCase();
        for (String badWord : badWords) {
            // Check for the bad word as a whole word (using word boundaries)
            String regex = "\\b" + badWord + "\\b";
            if (lowerText.matches(".*" + regex + ".*")) {
                return true;
            }
            // Also check for common variations (e.g., "f*ck", "fu*k")
            String obfuscatedPattern = badWord.replaceAll("(.)", "$1[^a-zA-Z0-9]*");
            if (lowerText.matches(".*" + obfuscatedPattern + ".*")) {
                return true;
            }
        }
        return false;
    }

    @FXML
    void goToList(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Reclamation/IndexReclamation.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des Réclamations");
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur de navigation : " + e.getMessage());
        }
    }

    public void prefillConsultation(int consultationId) {
        if (this.consultationId != null) {
            try {
                Consultation consultation = consultationService.getConsultationById(consultationId);
                if (consultation != null) {
                    this.consultationId.setText(consultation.getNomPatient());
                    this.consultationIdInternal = consultation.getId();
                } else {
                    errorMessage.setText("Consultation introuvable.");
                }
            } catch (SQLException e) {
                errorMessage.setText("Erreur lors de la récupération de la consultation : " + e.getMessage());
            }
        }
    }
}