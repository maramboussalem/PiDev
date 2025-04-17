package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.entities.Equipement;
import tn.esprit.services.EquipementService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

public class AddEquipement {

    @FXML private TextField tfNom, tfDescription, tfQuantite, tfPrix, tfImg;
    @FXML private DatePicker dpDateAchat;
    @FXML private ComboBox<String> cbEtat;

    private final EquipementService service = new EquipementService();

    @FXML
    public void initialize() {
        cbEtat.getItems().addAll("Neuf", "Abimé", "Réparé");
    }

    @FXML
    private void ajouterEquipement() {
        if (tfNom.getText().isEmpty() || tfDescription.getText().isEmpty() || tfQuantite.getText().isEmpty()
                || tfPrix.getText().isEmpty() || cbEtat.getValue() == null || dpDateAchat.getValue() == null) {
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
            String img = tfImg.getText();

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

            // Redirection vers la liste des équipements
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/IndexEquipement.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) tfNom.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur SQL : " + e.getMessage());
            alert.show();
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Quantité et prix doivent être des nombres valides !");
            alert.show();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors du chargement de la vue !");
            alert.show();
        }
    }
}
