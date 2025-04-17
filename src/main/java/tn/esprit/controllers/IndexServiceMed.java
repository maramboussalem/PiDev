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
import tn.esprit.entities.Disponibilite;
import tn.esprit.entities.ServiceMed;
import tn.esprit.services.ServiceMedService;

import java.io.File;
import java.io.IOException;

public class IndexServiceMed {

    @FXML
    private ListView<ServiceMed> listViewServiceMed;

    @FXML
    private ListView<Disponibilite> listViewDisponibilites;

    private final ServiceMedService service = new ServiceMedService();

    @FXML
    public void initialize() {
        loadServiceMeds();

        // Listener pour charger les disponibilités liées au service sélectionné
        listViewServiceMed.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadDisponibilites(newVal);
            } else {
                listViewDisponibilites.getItems().clear();
            }
        });
    }

    private void loadServiceMeds() {
        try {
            ObservableList<ServiceMed> data = FXCollections.observableArrayList(service.afficher());
            listViewServiceMed.setItems(data);

            listViewServiceMed.setCellFactory(param -> new ListCell<>() {
                private final ImageView imageView = new ImageView();

                @Override
                protected void updateItem(ServiceMed item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        imageView.setFitWidth(60);
                        imageView.setFitHeight(60);
                        imageView.setPreserveRatio(true);

                        try {
                            File file = new File(item.getImageM());
                            if (file.exists()) {
                                Image img = new Image(file.toURI().toString());
                                imageView.setImage(img);
                            } else {
                                System.out.println("Image non trouvée: " + item.getImageM());
                                imageView.setImage(null);
                            }
                        } catch (Exception e) {
                            System.out.println("Erreur image: " + e.getMessage());
                            imageView.setImage(null);
                        }

                        setText("Nom: " + item.getNomService() + "\nConsultation: " + item.getDescriptionMed());
                        setGraphic(imageView);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur lors du chargement des services : " + e.getMessage()).show();
        }
    }

    private void loadDisponibilites(ServiceMed serviceMed) {
        try {
            ObservableList<Disponibilite> dispos = FXCollections.observableArrayList(serviceMed.getDisponibilites());
            listViewDisponibilites.setItems(dispos);
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur lors du chargement des disponibilités : " + e.getMessage()).show();
        }
    }

    @FXML
    private void supprimerServiceMed() {
        ServiceMed selected = listViewServiceMed.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner un service à supprimer !").show();
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Êtes-vous sûr de vouloir supprimer ce service ?");
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    service.supprimer(selected.getId());
                    listViewServiceMed.getItems().remove(selected);
                    listViewDisponibilites.getItems().clear();
                    new Alert(Alert.AlertType.INFORMATION, "Service supprimé avec succès !").show();
                } catch (Exception e) {
                    e.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "Erreur lors de la suppression : " + e.getMessage()).show();
                }
            }
        });
    }

    @FXML
    private void modifierServiceMed() {
        ServiceMed selected = listViewServiceMed.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner un service à modifier !").show();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditServiceMed.fxml"));
            Parent root = loader.load();

            EditServiceMed controller = loader.getController();
            controller.setServiceMed(selected);

            Stage stage = new Stage();
            stage.setTitle("Modifier Service Médical");
            stage.setScene(new Scene(root));
            stage.show();

            stage.setOnHidden(event -> loadServiceMeds());  // Recharge la liste après modification

        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur chargement de la fenêtre de modification : " + e.getMessage()).show();
        }
    }
}
