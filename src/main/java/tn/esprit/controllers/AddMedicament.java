package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.esprit.entities.Fournisseur;
import tn.esprit.entities.Medicament;
import tn.esprit.services.FournisseurService;
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

public class AddMedicament {

    public FournisseurService fournisseurService = new FournisseurService();

    @FXML
    private ComboBox<Fournisseur> fournisseurCombo;
    @FXML private TextField nomField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField quantiteField;
    @FXML private TextField prixField;
    @FXML private ComboBox<String> typeCombo;
    @FXML private DatePicker expireAtPicker;
    @FXML private Label imageLabel;
    @FXML private Button browseBtn;
    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;
    @FXML private ImageView imagePreview;

    private File selectedImageFile;
    private final MedicamentService medicamentService = new MedicamentService();
    private Runnable refreshCallback;

    public void setRefreshCallback(Runnable callback) {
        this.refreshCallback = callback;
    }

    @FXML
    public void initialize() {
        typeCombo.getItems().addAll("Antibiotic", "Analgesic", "Antihistamine", "Antidepressant", "Other");
        typeCombo.getSelectionModel().selectFirst();
        expireAtPicker.setValue(LocalDate.now().plusDays(30));
        fournisseurCombo.getItems().addAll(fournisseurService.afficher());
        fournisseurCombo.getSelectionModel().selectFirst();

        // Safe default image loading
        loadDefaultImage();
    }

    private void loadDefaultImage() {
        try {
            // Try multiple possible locations for the default image
            InputStream defaultStream = getClass().getResourceAsStream("/images/medic.png");
            if (defaultStream == null) {
                defaultStream = getClass().getResourceAsStream("/images/medicaments.png");
            }
            if (defaultStream != null) {
                imagePreview.setImage(new Image(defaultStream));
            } else {
                System.err.println("Default image not found in any location");
            }
        } catch (Exception e) {
            System.err.println("Error loading default image: " + e.getMessage());
        }
    }

    @FXML
    private void handleSave() {
        try {
            if (!validateInputs()) return;

            Medicament medicament = new Medicament();
            medicament.setNom(nomField.getText().trim());
            medicament.setDescription(descriptionArea.getText().trim());
            medicament.setQuantite(Integer.parseInt(quantiteField.getText().trim()));
            medicament.setPrix(Double.parseDouble(prixField.getText().trim()));
            medicament.setType(typeCombo.getValue());
            medicament.setExpireAt(Date.valueOf(expireAtPicker.getValue()));
            medicament.setFournisseurId(fournisseurCombo.getValue().getId());

            if (selectedImageFile != null && selectedImageFile.exists()) {
                String imageName = saveImageFile(selectedImageFile);
                medicament.setImage(imageName);
            } else {
                medicament.setImage("medicament.png");
            }

            medicamentService.ajouter(medicament);

            if (refreshCallback != null) {
                refreshCallback.run();
            }

            closeWindow();
        } catch (Exception e) {
            showAlert("Error", "Failed to save medication: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private String saveImageFile(File imageFile) throws IOException {
        // Create target directory if it doesn't exist
        Path targetDir = Paths.get("src/main/resources/images/medicaments/");
        if (!Files.exists(targetDir)) {
            Files.createDirectories(targetDir);
        }

        String originalName = imageFile.getName();
        String extension = originalName.substring(originalName.lastIndexOf("."));
        String uniqueName = System.currentTimeMillis() + extension;

        Path destination = targetDir.resolve(uniqueName);
        Files.copy(imageFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);

        return uniqueName;
    }

    @FXML
    private void handleBrowse() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Medication Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        selectedImageFile = fileChooser.showOpenDialog(browseBtn.getScene().getWindow());
        if (selectedImageFile != null && selectedImageFile.exists()) {
            imageLabel.setText(selectedImageFile.getName());
            try {
                imagePreview.setImage(new Image(selectedImageFile.toURI().toString()));
            } catch (Exception e) {
                showAlert("Error", "Failed to load selected image", Alert.AlertType.ERROR);
                loadDefaultImage();
            }
        }
    }

    private boolean validateInputs() {
        String nom = nomField.getText().trim();
        String description = descriptionArea.getText().trim();
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

        // Fournisseur : combo obligatoire
        if (fournisseurCombo.getValue() == null) {
            errors.append("Le fournisseur est requis.\n");
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