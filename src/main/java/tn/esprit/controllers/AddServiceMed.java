package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.esprit.entities.ServiceMed;
import tn.esprit.services.ServiceMedService;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class AddServiceMed implements Initializable {

    @FXML
    private TextField tfNomService;
    @FXML
    private TextField tfDescriptionMed;
    @FXML
    private TextField tfImageM;
    @FXML
    private ImageView previewImage;

    private final ServiceMedService service = new ServiceMedService();
    private final FileChooser fileChooser = new FileChooser();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configuration du FileChooser
        fileChooser.setTitle("Sélectionner une image");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images (*.png, *.jpg, *.jpeg, *.gif)",
                        "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
    }

    @FXML
    private void selectImage() {
        Stage stage = (Stage) tfNomService.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            tfImageM.setText(file.getAbsolutePath());
            previewImage.setImage(new Image(file.toURI().toString()));
        }
    }

    @FXML
    private void handleAjouterServiceMed() {
        String nom = tfNomService.getText().trim();
        String description = tfDescriptionMed.getText().trim();
        String imagePath = tfImageM.getText().trim();

        if (nom.isEmpty() || description.isEmpty() || imagePath.isEmpty()) {
            new Alert(Alert.AlertType.WARNING,
                    "Veuillez remplir tous les champs avant d'enregistrer.")
                    .show();
            return;
        }

        ServiceMed newService = new ServiceMed();
        newService.setNomService(nom);
        newService.setDescriptionMed(description);
        newService.setImageM(imagePath);

        try {
            service.ajouter(newService);
            new Alert(Alert.AlertType.INFORMATION,
                    "Service médical ajouté avec succès !").show();

            // Réinitialisation du formulaire
            tfNomService.clear();
            tfDescriptionMed.clear();
            tfImageM.clear();
            previewImage.setImage(null);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR,
                    "Erreur lors de l'ajout : " + e.getMessage()).show();
        }
    }
}