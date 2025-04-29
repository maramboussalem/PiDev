package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Parent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.esprit.entities.Equipement;
import tn.esprit.services.EquipementService;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

public class AddEquipement {

    @FXML private TextField tfNom, tfDescription, tfQuantite, tfPrix, tfImg;
    @FXML private DatePicker dpDateAchat;
    @FXML private ComboBox<String> cbEtat;
    @FXML private ImageView imgPreview;  // Ajout de l'ImageView pour l'aperçu de l'image

    private File selectedImageFile;
    private final EquipementService service = new EquipementService();

    @FXML
    public void initialize() {
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
            tfImg.setText(selectedImageFile.getName());  // Affiche le nom de l'image

            // Mettre à jour l'aperçu de l'image
            Image image = new Image("file:" + selectedImageFile.getAbsolutePath());
            imgPreview.setImage(image);  // Met l'image dans l'ImageView
        }
    }

    @FXML
    private void ajouterEquipement() {
        if (tfNom.getText().isEmpty() || tfDescription.getText().isEmpty() || tfQuantite.getText().isEmpty()
                || tfPrix.getText().isEmpty() || cbEtat.getValue() == null || dpDateAchat.getValue() == null || selectedImageFile == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Tous les champs doivent être remplis !");
            alert.show();
            return;
        }

        try {
            String nom = tfNom.getText();
            String desc = tfDescription.getText();
            int quantite = Integer.parseInt(tfQuantite.getText());
            double prix = Double.parseDouble(tfPrix.getText());
            String etat = cbEtat.getValue();
            LocalDate date = dpDateAchat.getValue();
            String img = selectedImageFile.getAbsolutePath(); // Chemin de l'image

            Equipement equipement = new Equipement(0, desc, nom, quantite, prix, etat, date, img);
            service.ajouter(equipement);

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Équipement ajouté avec succès !");
            alert.show();

            // Effacer les champs
            tfNom.clear();
            tfDescription.clear();
            tfQuantite.clear();
            tfPrix.clear();
            tfImg.clear();
            cbEtat.setValue(null);
            dpDateAchat.setValue(null);
            selectedImageFile = null;

            // Redirection vers la liste
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/IndexEquipement.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) tfNom.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (SQLException e) {
            showError("Erreur SQL : " + e.getMessage());
        } catch (NumberFormatException e) {
            showError("Quantité et prix doivent être des nombres valides !");
        } catch (IOException e) {
            showError("Erreur lors du chargement de la vue !");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.show();
    }
}
