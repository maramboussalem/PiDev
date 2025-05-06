package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tn.esprit.entities.Consultation;
import tn.esprit.services.ConsultationService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class MakeRec {

    @FXML
    private ListView<Consultation> consultationListView;

    @FXML
    private AnchorPane mainContainer; // Updated to AnchorPane as per FXML

    private final ConsultationService consultationService = new ConsultationService();

    private int userId;

    public void setUserId(int userId) {
        this.userId = userId;
        System.out.println("MakeRec: setUserId called with userId: " + userId); // Debug
        // Since MakeRec is embedded, reload consultations when userId is set
        loadConsultations();
    }

    @FXML
    public void initialize() {
        // Initial setup for the ListView
        consultationListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Consultation consultation, boolean empty) {
                super.updateItem(consultation, empty);
                if (empty || consultation == null) {
                    setGraphic(null);
                } else {
                    Text consultationText = new Text("👤 " + consultation.getNomPatient()
                            + " | 📅 " + consultation.getDateConsultation().toString()
                            + " | 🩺 " + consultation.getDiagnostic());
                    consultationText.setStyle("-fx-font-size: 14; -fx-text-fill: #333333;");

                    Button recBtn = new Button("Réclamation");
                    recBtn.setStyle("-fx-background-color: #00b8bb; -fx-text-fill: white; -fx-background-radius: 20; -fx-padding: 8;");
                    recBtn.setOnAction(e -> openAddReclamation(consultation.getId()));

                    HBox hBox = new HBox(20, consultationText, recBtn);
                    hBox.setStyle("-fx-padding: 10; -fx-background-color: #f9f9f9; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #dcdcdc;");
                    hBox.setMaxWidth(Double.MAX_VALUE);

                    setGraphic(hBox);
                }
            }
        });

        // Load consultations if userId is already set
        if (userId != 0) {
            loadConsultations();
        }
    }

    private void loadConsultations() {
        if (userId == 0) {
            System.err.println("MakeRec: Erreur - userId non défini. Impossible de charger les consultations.");
            showNoConsultationsMessage("Erreur : Utilisateur non défini.");
            return;
        }

        try {
            System.out.println("MakeRec: Fetching consultations for userId: " + userId); // Debug

            List<Consultation> consultations = consultationService.afficherByUserId(userId);

            System.out.println("MakeRec: Number of consultations retrieved for userId " + userId + ": " + consultations.size()); // Debug

            for (Consultation consultation : consultations) {
                System.out.println("MakeRec: Consultation found - " + consultation); // Debug
            }

            if (consultations.isEmpty()) {
                showNoConsultationsMessage("Aucune consultation trouvée pour cet utilisateur.");
            } else {
                // Remove any previous error message (keep the ListView and Button)
                mainContainer.getChildren().removeIf(node -> node instanceof Label);
                consultationListView.getItems().setAll(consultations);
            }
        } catch (SQLException e) {
            System.err.println("MakeRec: Erreur de chargement des consultations : " + e.getMessage());
            showNoConsultationsMessage("Erreur lors du chargement des consultations : " + e.getMessage());
        }
    }

    private void showNoConsultationsMessage(String message) {
        // Remove any existing message, but keep the ListView, Button, and Text (title)
        mainContainer.getChildren().removeIf(node -> node instanceof Label);
        // Clear the ListView items
        consultationListView.getItems().clear();
        // Add a centered message below the ListView
        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-size: 16; -fx-text-fill: #666666;");
        AnchorPane.setTopAnchor(messageLabel, 580.0); // Position below the ListView
        AnchorPane.setLeftAnchor(messageLabel, 400.0); // Center horizontally
        mainContainer.getChildren().add(messageLabel);
    }

    private void openAddReclamation(int consultationId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Reclamation/addReclamation.fxml"));
            Parent root = loader.load();

            addReclamation controller = loader.getController();
            controller.prefillConsultation(consultationId);

            Stage stage = new Stage();
            stage.setTitle("Ajouter Réclamation");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            System.err.println("Erreur lors de l'ouverture du formulaire de réclamation : " + e.getMessage());
        }
    }

    @FXML
    public void goToReclamations(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Reclamation/IndexReclamation.fxml"));
            Parent root = loader.load();

            // Since MakeRec is embedded in Home, replace the contentArea
            AnchorPane contentArea = (AnchorPane) consultationListView.getScene().lookup("#contentArea");
            if (contentArea != null) {
                contentArea.getChildren().clear();
                contentArea.getChildren().add(root);
            } else {
                // Fallback: Open in a new Stage
                Stage newStage = new Stage();
                newStage.setTitle("Liste des Réclamations");
                newStage.setScene(new Scene(root));
                newStage.show();
            }

        } catch (IOException e) {
            System.err.println("Erreur de navigation : " + e.getMessage());
        }
    }
}