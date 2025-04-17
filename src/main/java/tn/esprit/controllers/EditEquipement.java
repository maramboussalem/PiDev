package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.entities.Equipement;
import tn.esprit.services.EquipementService;

public class EditEquipement {

    @FXML private TextField tfNom;
    @FXML private TextField tfDescription;
    @FXML private TextField tfQuantite;
    @FXML private TextField tfPrix;
    @FXML private TextField tfEtat;
    @FXML private DatePicker dpDateAchat;
    @FXML private TextField tfImg;

    private Equipement equipement;
    private final EquipementService service = new EquipementService();

    public void setEquipement(Equipement e) {
        this.equipement = e;

        // Pré-remplir les champs
        tfNom.setText(e.getNomEquipement());
        tfDescription.setText(e.getDescription());
        tfQuantite.setText(String.valueOf(e.getQuantiteStock()));
        tfPrix.setText(String.valueOf(e.getPrixUnitaire()));
        tfEtat.setText(e.getEtatEquipement());
        dpDateAchat.setValue(e.getDateAchat());
        tfImg.setText(e.getImg());
    }

    @FXML
    private void handleUpdateEquipement() {
        try {
            // Mettre à jour les valeurs de l'équipement
            equipement.setNomEquipement(tfNom.getText());
            equipement.setDescription(tfDescription.getText());
            equipement.setQuantiteStock(Integer.parseInt(tfQuantite.getText()));
            equipement.setPrixUnitaire(Double.parseDouble(tfPrix.getText()));
            equipement.setEtatEquipement(tfEtat.getText());
            equipement.setDateAchat(dpDateAchat.getValue());
            equipement.setImg(tfImg.getText());

            service.modifier(equipement);

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Équipement modifié avec succès !");
            alert.show();

            // Fermer la fenêtre après modification
            Stage stage = (Stage) tfNom.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur : " + e.getMessage());
            alert.show();
        }
    }
}
