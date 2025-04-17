package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import tn.esprit.entities.Equipement;
import tn.esprit.services.EquipementService;

import java.io.IOException;

public class IndexEquipement {

    @FXML
    private ListView<Equipement> listViewEquipements;

    private final EquipementService service = new EquipementService();

    @FXML
    public void initialize() {
        loadEquipements();
    }

    private void loadEquipements() {
        try {
            ObservableList<Equipement> data = FXCollections.observableArrayList(service.afficher());
            listViewEquipements.setItems(data);

            listViewEquipements.setCellFactory(param -> new ListCell<Equipement>() {
                private final ImageView imageView = new ImageView();

                @Override
                protected void updateItem(Equipement item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        imageView.setFitWidth(60);
                        imageView.setFitHeight(60);
                        imageView.setPreserveRatio(true);
                        try {
                            Image img = new Image("file:" + item.getImg());
                            imageView.setImage(img);
                        } catch (Exception e) {
                            System.out.println("Erreur chargement image: " + e.getMessage());
                        }

                        setText("Nom: " + item.getNomEquipement() +
                                "\nDescription: " + item.getDescription() +
                                "\nQuantité: " + item.getQuantiteStock() +
                                "\nPrix: " + item.getPrixUnitaire() +
                                "\nÉtat: " + item.getEtatEquipement() +
                                "\nDate: " + item.getDateAchat());

                        setGraphic(imageView);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void supprimerEquipement() {
        Equipement selected = listViewEquipements.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner un équipement à supprimer !");
            alert.show();
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Êtes-vous sûr de vouloir supprimer cet équipement ?");
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    service.supprimer(selected.getId());
                    listViewEquipements.getItems().remove(selected);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Équipement supprimé avec succès !");
                    alert.show();
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de la suppression : " + e.getMessage());
                    alert.show();
                }
            }
        });
    }

    @FXML
    private void modifierEquipement() {
        Equipement selected = listViewEquipements.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner un équipement à modifier !");
            alert.show();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditEquipement.fxml"));
            Parent root = loader.load();

            EditEquipement controller = loader.getController();
            controller.setEquipement(selected);

            Stage stage = new Stage();
            stage.setTitle("Modifier Équipement");
            stage.setScene(new Scene(root));
            stage.show();

            stage.setOnHidden(event -> loadEquipements());

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur chargement fenêtre de modification : " + e.getMessage());
            alert.show();
        }
    }
}
