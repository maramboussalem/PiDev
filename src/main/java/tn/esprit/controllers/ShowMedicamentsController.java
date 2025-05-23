package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import tn.esprit.entities.Medicament;
import tn.esprit.services.FournisseurService;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

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


            Path imagePath = Paths.get("src/main/resources/images/medicaments/" + imageName);
            medicamentImage.setImage(new Image(imagePath.toUri().toString()));


    }



    @FXML
    private void handleBack() {
        medicamentImage.getScene().getWindow().hide();
    }
}