package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import tn.esprit.entities.Medicament;
import tn.esprit.services.FournisseurService;

import java.io.InputStream;

public class ShowMedicamentsController {
    @FXML private Label titleLabel;
    @FXML private ImageView medicamentImage;
    @FXML private Label nameLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label priceLabel;
    @FXML private Label quantityLabel;
    @FXML private Label typeLabel;
    @FXML private Label expiryLabel;
    @FXML private Label fournisseurLabel;

    private Medicament medicament;
    private final FournisseurService fournisseurService = new FournisseurService();

    public void setMedicament(Medicament medicament) {
        this.medicament = medicament;
        displayMedicamentDetails();
    }

    private void displayMedicamentDetails() {
        if (medicament != null) {
            titleLabel.setText("Medication Details");
            nameLabel.setText("Name: " + medicament.getNom());
            descriptionLabel.setText("Description: " +
                    (medicament.getDescription() != null ? medicament.getDescription() : "N/A"));
            priceLabel.setText("Price: " + medicament.getPrix() + " DT");
            quantityLabel.setText("Quantity: " + medicament.getQuantite());
            typeLabel.setText("Type: " + medicament.getType());
            expiryLabel.setText("Expiry Date: " + medicament.getExpireAt());

            // Get fournisseur name
            String fournisseurName = fournisseurService.findById(medicament.getFournisseurId()).getNom_fournisseur();
            fournisseurLabel.setText("Supplier: " + fournisseurName);

            // Load image with multiple fallback options
            loadMedicamentImage(medicament.getImage());
        }
    }

    private void loadMedicamentImage(String imageName) {
        try {
            if (imageName == null || imageName.isEmpty()) {
                imageName = "default-medicament.png";
            }

            // Try multiple possible locations
            InputStream imageStream = tryImageLocations(imageName);

            if (imageStream != null) {
                medicamentImage.setImage(new Image(imageStream));
            } else {
                System.err.println("Could not load image: " + imageName);
                loadDefaultImage();
            }
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
            loadDefaultImage();
        }
    }

    private InputStream tryImageLocations(String imageName) {
        // Try multiple possible paths
        String[] possiblePaths = {
                "/tn/esprit/images/medicaments/" + imageName,
                "/images/medicaments/" + imageName,
                "/tn/esprit/images/default-medicament.png",
                "/images/default-medicament.png"
        };

        for (String path : possiblePaths) {
            InputStream stream = getClass().getResourceAsStream(path);
            if (stream != null) {
                return stream;
            }
        }
        return null;
    }

    private void loadDefaultImage() {
        try {
            InputStream defaultStream = getClass().getResourceAsStream("/tn/esprit/images/default-medicament.png");
            if (defaultStream == null) {
                defaultStream = getClass().getResourceAsStream("/images/default-medicament.png");
            }
            if (defaultStream != null) {
                medicamentImage.setImage(new Image(defaultStream));
            }
        } catch (Exception e) {
            System.err.println("Failed to load default image");
        }
    }

    @FXML
    private void handleBack() {
        medicamentImage.getScene().getWindow().hide();
    }
}