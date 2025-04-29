package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import tn.esprit.entities.Equipement;
import tn.esprit.services.EquipementService;
import java.io.File;

public class EditEquipement {

    @FXML private TextField tfNom;
    @FXML private TextField tfDescription;
    @FXML private TextField tfQuantite;
    @FXML private TextField tfPrix;
    @FXML private ComboBox<String> cbEtat;
    @FXML private DatePicker dpDateAchat;
    @FXML private TextField tfImg;
    @FXML private ImageView imgPreview; // Pour l'aperçu de l'image

    private Equipement equipement;
    private final EquipementService service = new EquipementService();
    private File selectedImageFile;

    public void setEquipement(Equipement e) {
        this.equipement = e;

        // Pré-remplir les champs
        tfNom.setText(e.getNomEquipement());
        tfDescription.setText(e.getDescription());
        tfQuantite.setText(String.valueOf(e.getQuantiteStock()));
        tfPrix.setText(String.valueOf(e.getPrixUnitaire()));
        cbEtat.setValue(e.getEtatEquipement());
        dpDateAchat.setValue(e.getDateAchat());
        tfImg.setText(e.getImg());

        // Pré-charger l'aperçu de l'image
        if (e.getImg() != null && !e.getImg().isEmpty()) {
            Image image = new Image("file:" + e.getImg());
            imgPreview.setImage(image);
        }
    }

    @FXML
    public void initialize() {
        // Initialisation des options de la ComboBox pour l'état de l'équipement
        cbEtat.getItems().addAll("Neuf", "Abimé", "Réparé");
    }

    @FXML
    private void handleImageSelect() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );
        selectedImageFile = fileChooser.showOpenDialog(new Stage());

        if (selectedImageFile != null) {
            tfImg.setText(selectedImageFile.getAbsolutePath());

            // Mettre à jour l'aperçu de l'image
            Image image = new Image("file:" + selectedImageFile.getAbsolutePath());
            imgPreview.setImage(image);
        }
    }

    @FXML
    private void handleUpdateEquipement() {
        try {
            // Validation des champs
            if (tfNom.getText().isEmpty() || tfDescription.getText().isEmpty() || tfQuantite.getText().isEmpty() || tfPrix.getText().isEmpty() || cbEtat.getValue() == null || dpDateAchat.getValue() == null) {
                throw new Exception("Tous les champs doivent être remplis.");
            }

            // Validation de la quantité et du prix
            int quantite = Integer.parseInt(tfQuantite.getText());
            double prix = Double.parseDouble(tfPrix.getText());

            if (quantite <= 0 || prix <= 0) {
                throw new Exception("La quantité et le prix doivent être supérieurs à zéro.");
            }

            // Mettre à jour les valeurs de l'équipement
            equipement.setNomEquipement(tfNom.getText());
            equipement.setDescription(tfDescription.getText());
            equipement.setQuantiteStock(quantite);
            equipement.setPrixUnitaire(prix);
            equipement.setEtatEquipement(cbEtat.getValue());
            equipement.setDateAchat(dpDateAchat.getValue());
            equipement.setImg(tfImg.getText());

            service.modifier(equipement);

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Équipement modifié avec succès !");
            alert.show();

            // Fermer la fenêtre après modification
            Stage stage = (Stage) tfNom.getScene().getWindow();
            stage.close();

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur dans les valeurs numériques : " + e.getMessage());
            alert.show();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur : " + e.getMessage());
            alert.show();
        }
    }

}
