package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.esprit.entities.Medicament;
import tn.esprit.services.MedicamentService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.time.LocalDate;

public class EditMedicament {
    @FXML private TextField nomField;
    @FXML private TextField descriptionField;
    @FXML private TextField prixField;
    @FXML private TextField quantiteField;
    @FXML private ComboBox<String> typeCombo;
    @FXML private DatePicker expireAtPicker;
    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;
    @FXML private ImageView imagePreview;
    @FXML private Button uploadImageBtn;

    private String imagePath;
    private Medicament currentMedicament;
    private Runnable refreshCallback;

    public void setRefreshCallback(Runnable callback) {
        this.refreshCallback = callback;
    }

    public void setMedicamentData(Medicament medicament) {
        this.currentMedicament = medicament;
        nomField.setText(medicament.getNom());
        descriptionField.setText(medicament.getDescription());
        prixField.setText(String.valueOf(medicament.getPrix()));
        quantiteField.setText(String.valueOf(medicament.getQuantite()));
        typeCombo.setValue(medicament.getType());
        expireAtPicker.setValue(medicament.getExpireAt().toLocalDate());
        imagePath = medicament.getImage();

        // Load image with multiple fallback options
        loadMedicamentImage(medicament.getImage());
    }

    @FXML
    private void initialize() {
        typeCombo.getItems().addAll("Antibiotic", "Analgesic", "Antihistamine", "Antidepressant", "Other");
    }

    private void loadMedicamentImage(String imageName) {
        Path imagePath = Paths.get("src/main/resources/images/medicaments/" + imageName);
        imagePreview.setImage(new Image(imagePath.toUri().toString()));



    }



    @FXML
    private void handleImageUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Medication Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(uploadImageBtn.getScene().getWindow());

        if (selectedFile != null) {
            try {
                // Copy the file to the target directory
                Path destination = Paths.get("src/main/resources/images/medicaments/" + selectedFile.getName());
                Files.copy(selectedFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);

                // Update the image path and display
                imagePath = selectedFile.getName();
                imagePreview.setImage(new Image(destination.toUri().toString()));
            } catch (IOException e) {
                showAlert("Error", "Failed to upload image: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }
    @FXML
    private void handleSubmit(ActionEvent event) {
        if (!validateInputs()) return;

        try {
            currentMedicament.setNom(nomField.getText());
            currentMedicament.setDescription(descriptionField.getText());
            currentMedicament.setPrix(Double.parseDouble(prixField.getText()));
            currentMedicament.setQuantite(Integer.parseInt(quantiteField.getText()));
            currentMedicament.setType(typeCombo.getValue());
            currentMedicament.setExpireAt(Date.valueOf(expireAtPicker.getValue()));

            if (imagePath != null) {
                currentMedicament.setImage(imagePath);
            }

            new MedicamentService().modifier(currentMedicament);

            if (refreshCallback != null) {
                refreshCallback.run();
            }

            Node source = (Node) event.getSource(); // get the source of the event
            source.getScene().getWindow().hide();
        } catch (Exception e) {
            showAlert("Error", "Failed to update medication: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private boolean validateInputs() {
        String nom = nomField.getText().trim();
        String description = descriptionField.getText().trim();
        String quantiteStr = quantiteField.getText().trim();
        String prixStr = prixField.getText().trim();
        LocalDate expireAt = expireAtPicker.getValue();
        StringBuilder errors = new StringBuilder();

        // Nom : requis, longueur min 2, max 100
        if (nom.isEmpty()) {
            errors.append("Le nom du médicament est requis.\n");
        } else if (nom.length() < 2 || nom.length() > 100) {
            errors.append("Le nom doit contenir entre 2 et 100 caractères.\n");
        }

        // Description : requise, longueur max 500
        if (description.isEmpty()) {
            errors.append("La description est requise.\n");
        } else if (description.length() > 500) {
            errors.append("La description ne doit pas dépasser 500 caractères.\n");
        }

        // Quantité : nombre entier >= 0
        if (quantiteStr.isEmpty()) {
            errors.append("La quantité est requise.\n");
        } else {
            try {
                int q = Integer.parseInt(quantiteStr);
                if (q < 0) errors.append("La quantité ne peut pas être négative.\n");
            } catch (NumberFormatException e) {
                errors.append("La quantité doit être un nombre entier.\n");
            }
        }

        // Prix : nombre décimal >= 0
        if (prixStr.isEmpty()) {
            errors.append("Le prix est requis.\n");
        } else {
            try {
                double p = Double.parseDouble(prixStr);
                if (p < 0) errors.append("Le prix ne peut pas être négatif.\n");
            } catch (NumberFormatException e) {
                errors.append("Le prix doit être un nombre décimal.\n");
            }
        }

        // Type : combo obligatoire
        if (typeCombo.getValue() == null) {
            errors.append("Le type de médicament est requis.\n");
        }

        // Date d'expiration : obligatoire et future
        if (expireAt == null) {
            errors.append("La date d'expiration est requise.\n");
        } else if (expireAt.isBefore(LocalDate.now())) {
            errors.append("La date d'expiration doit être ultérieure à aujourd'hui.\n");
        }




        if (expireAt == null) {
            errors.append("La date d'expiration est requise.\n");
        } else {
            LocalDate minDate = LocalDate.now().plusDays(3);
            if (expireAt.isBefore(minDate)) {
                errors.append("La date d'expiration doit être au moins dans 3 jours (à partir d'aujourd'hui).\n");
            }
        }




        // Affichage des erreurs s'il y en a
        if (errors.length() > 0) {
            showAlert("Champs invalides", errors.toString(), Alert.AlertType.WARNING);
            return false;
        }

        return true;
    }

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