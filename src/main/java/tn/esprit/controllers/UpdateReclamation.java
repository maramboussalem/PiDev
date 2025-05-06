package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import tn.esprit.entities.Reclamation;
import tn.esprit.services.ReclamationService;

import java.sql.SQLException;

public class UpdateReclamation {

    @FXML
    private ComboBox<String> sujetComboBox;  // Changed from TextField to ComboBox for subject selection

    @FXML
    private TextArea descriptionField;

    @FXML
    private Button updateButton;

    private final ReclamationService service = new ReclamationService();
    private Reclamation reclamationToUpdate;

    // This is a reference to IndexReclamation controller to update the list after the update
    private IndexReclamation indexReclamationController;

    // Initialize method to populate the ComboBox with predefined subjects
    @FXML
    void initialize() {
        sujetComboBox.getItems().addAll(
                "Problème technique",
                "Manque de communication avec le docteur",
                "Erreur de facturation",
                "Service non satisfaisant"
        );

        // You can preselect the existing subject here if needed
        if (reclamationToUpdate != null) {
            sujetComboBox.setValue(reclamationToUpdate.getSujet());
            descriptionField.setText(reclamationToUpdate.getDescription());
        }
    }

    // Set the Reclamation object that will be updated
    public void setReclamation(Reclamation reclamation, IndexReclamation indexController) {
        this.reclamationToUpdate = reclamation;
        this.indexReclamationController = indexController;
    }

    // Handle the update action
    @FXML
    private void updateReclamation() {
        if (reclamationToUpdate != null) {
            String selectedSujet = sujetComboBox.getValue();
            String newDescription = descriptionField.getText();

            // Set the new values for the reclamation
            reclamationToUpdate.setSujet(selectedSujet);
            reclamationToUpdate.setDescription(newDescription);

            // Call the modifier method to update the reclamation in the database
            try {
                service.modifier(reclamationToUpdate);
                // Optionally, show a success message
                showSuccessMessage();

                // Inform the IndexReclamation controller to refresh the list
                if (indexReclamationController != null) {
                    indexReclamationController.refreshList();
                }

            } catch (SQLException e) {
                e.printStackTrace();
                showErrorMessage("Erreur lors de la mise à jour de la réclamation.");
            }
        }
    }

    // Show success message after update
    private void showSuccessMessage() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Mise à jour réussie");
        alert.setHeaderText(null);
        alert.setContentText("Réclamation mise à jour avec succès.");
        alert.showAndWait();
    }

    // Show error message if update fails
    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
