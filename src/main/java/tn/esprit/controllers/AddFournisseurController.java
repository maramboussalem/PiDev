package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.entities.Fournisseur;
import tn.esprit.services.FournisseurService;

public class AddFournisseurController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField addressField;
    @FXML private TextField phoneField;
    @FXML private Button ajouter;
    @FXML private Button cancelBtn;

    private final FournisseurService service = new FournisseurService();
    private Runnable refreshCallback;

    public void setRefreshCallback(Runnable callback) {
        this.refreshCallback = callback;
    }

    @FXML
    private void handleSave() {
        if (!validateFields()) return;

        try {
            Fournisseur newSupplier = new Fournisseur(
                    nameField.getText(),
                    addressField.getText(),
                    phoneField.getText(),
                    emailField.getText()
            );

            service.ajouter(newSupplier);

            if (refreshCallback != null) {
                refreshCallback.run();
            }

            closeWindow();
        } catch (Exception e) {
            showAlert("Error", "Failed to add supplier: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private boolean validateFields() {
        if (nameField.getText().trim().isEmpty() ||
                emailField.getText().trim().isEmpty() ||
                addressField.getText().trim().isEmpty() ||
                phoneField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Please fill in all fields", Alert.AlertType.ERROR);
            return false;
        }

        if (!emailField.getText().trim().matches("^[\\w-_.+]*[\\w-_.]@([\\w]+\\.)+[\\w]+[\\w]$")) {
            showAlert("Validation Error", "Please enter a valid email address", Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }



    private void closeWindow() {
        if (cancelBtn != null && cancelBtn.getScene() != null) {
            Stage stage = (Stage) cancelBtn.getScene().getWindow();
            if (stage != null) {
                stage.close();
            }
        }
    }


    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}