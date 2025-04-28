package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.entities.Fournisseur;
import tn.esprit.services.FournisseurService;

public class EditFournisseurController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField addressField;
    @FXML private TextField phoneField;
    @FXML private Button cancelBtn;

    private Fournisseur supplier;
    private final FournisseurService service = new FournisseurService();
    private Runnable refreshCallback;

    public void setSupplierData(Fournisseur supplier) {
        this.supplier = supplier;
        nameField.setText(supplier.getNom_fournisseur());
        emailField.setText(supplier.getEmail());
        addressField.setText(supplier.getAdresse());
        phoneField.setText(supplier.getTelephone());
    }

    public void setRefreshCallback(Runnable callback) {
        this.refreshCallback = callback;
    }

    @FXML
    private void handleUpdate() {
        if (!validateFields()) return;

        try {
            supplier.setNom_fournisseur(nameField.getText());
            supplier.setEmail(emailField.getText());
            supplier.setAdresse(addressField.getText());
            supplier.setTelephone(phoneField.getText());

            service.modifier(supplier);

            if (refreshCallback != null) {
                refreshCallback.run();
            }

            closeWindow();
        } catch (Exception e) {
            showAlert("Error", "Failed to update supplier: " + e.getMessage(), Alert.AlertType.ERROR);
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

    @FXML

    private void closeWindow() {
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}