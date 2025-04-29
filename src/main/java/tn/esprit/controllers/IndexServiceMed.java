package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import tn.esprit.entities.ServiceMed;
import tn.esprit.entities.Disponibilite;
import tn.esprit.services.ServiceMedService;
import tn.esprit.services.DisponibiliteService;

import java.io.File;
import java.io.IOException;
import java.time.YearMonth;

public class IndexServiceMed {

    @FXML
    private ListView<ServiceMed> listViewServiceMed;

    @FXML
    private ListView<Disponibilite> listViewDisponibilites;

    @FXML
    private TextField searchField;

    @FXML
    private GridPane calendarGrid;

    private final ServiceMedService service = new ServiceMedService();

    @FXML
    public void initialize() {
        loadServiceMeds();
        searchField.setOnKeyReleased(this::rechercherServiceMed);

        listViewServiceMed.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadDisponibilites(newVal);
            } else {
                listViewDisponibilites.getItems().clear();
            }
        });

        loadCalendar(null); // affiche calendrier vide au démarrage
    }

    @FXML
    private void rechercherServiceMed(KeyEvent event) {
        String keyword = searchField.getText();
        try {
            ObservableList<ServiceMed> allServices = FXCollections.observableArrayList(service.afficher());
            if (keyword == null || keyword.isEmpty()) {
                listViewServiceMed.setItems(allServices);
            } else {
                String lowerKeyword = keyword.toLowerCase();
                ObservableList<ServiceMed> filtered = allServices.filtered(serviceMed ->
                        serviceMed.getNomService().toLowerCase().contains(lowerKeyword) ||
                                serviceMed.getDescriptionMed().toLowerCase().contains(lowerKeyword)
                );
                listViewServiceMed.setItems(filtered);
            }
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur lors de la recherche : " + e.getMessage()).show();
        }
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

                        Rectangle clip = new Rectangle(60, 60);
                        clip.setArcWidth(15);
                        clip.setArcHeight(15);
                        imageView.setClip(clip);

                        try {
                            File file = new File(item.getImageM());
                            if (file.exists()) {
                                Image img = new Image(file.toURI().toString());
                                imageView.setImage(img);
                            } else {
                                imageView.setImage(null);
                            }
                        } catch (Exception e) {
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
            new Alert(Alert.AlertType.ERROR, "Erreur chargement disponibilités : " + e.getMessage()).show();
        }
    }

    private void loadCalendar(ServiceMed serviceMed) {
        calendarGrid.getChildren().clear();
        YearMonth currentMonth = YearMonth.now();
        int daysInMonth = currentMonth.lengthOfMonth();

        int row = 0, col = 0;

        String[] daysOfWeek = {"Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim"};
        for (int i = 0; i < 7; i++) {
            Label dayLabel = new Label(daysOfWeek[i]);
            dayLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #1e90ff;");
            dayLabel.setMinSize(40, 40);
            calendarGrid.add(dayLabel, i, 0);
        }

        for (int day = 1; day <= daysInMonth; day++) {
            final int currentDay = day;
            Button dayButton = new Button(String.valueOf(currentDay));
            dayButton.setMinSize(40, 40);

            if (serviceMed != null) {
                Disponibilite dispo = serviceMed.getDisponibilites().stream()
                        .filter(d -> d.getDateHeure().getDayOfMonth() == currentDay)
                        .findFirst()
                        .orElse(null);

                if (dispo != null) {
                    if (dispo.isEstReserve()) {
                        dayButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
                    } else {
                        dayButton.setStyle("-fx-background-color: green; -fx-text-fill: white;");
                    }
                }
            }

            calendarGrid.add(dayButton, col, row + 1);
            col++;
            if (col == 7) {
                col = 0;
                row++;
            }
        }
    }

    @FXML
    private void afficherDisponibilitesDansCalendrier() {
        ServiceMed selectedService = listViewServiceMed.getSelectionModel().getSelectedItem();
        if (selectedService == null) {
            new Alert(Alert.AlertType.WARNING, "Sélectionnez un service d'abord !").show();
        } else {
            loadCalendar(selectedService);
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
                    calendarGrid.getChildren().clear();
                    loadCalendar(null);
                    new Alert(Alert.AlertType.INFORMATION, "Service supprimé avec succès !").show();
                } catch (Exception e) {
                    e.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "Erreur suppression : " + e.getMessage()).show();
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Service/EditServiceMed.fxml"));
            Parent root = loader.load();
            EditServiceMed controller = loader.getController();
            controller.setServiceMed(selected);
            Stage stage = new Stage();
            stage.setTitle("Modifier Service Médical");
            stage.setScene(new Scene(root));
            stage.show();
            stage.setOnHidden(event -> loadServiceMeds());
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur chargement fenêtre modification : " + e.getMessage()).show();
        }
    }

    @FXML
    private void ouvrirAjoutServiceMed() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Service/AddServiceMed.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter Service Médical");
            stage.setScene(new Scene(root));
            stage.show();
            stage.setOnHidden(event -> loadServiceMeds());
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur ouverture ajout service : " + e.getMessage()).show();
        }
    }

    @FXML
    private void ouvrirAjoutDisponibilite() {
        ServiceMed selectedService = listViewServiceMed.getSelectionModel().getSelectedItem();
        if (selectedService == null) {
            new Alert(Alert.AlertType.WARNING, "Sélectionnez un service avant !").show();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Service/AddDisponibilite.fxml"));
            Parent root = loader.load();
            AddDisponibilite controller = loader.getController();
            controller.setServiceMed(selectedService);
            Stage stage = new Stage();
            stage.setTitle("Ajouter Disponibilité");
            stage.setScene(new Scene(root));
            stage.show();
            stage.setOnHidden(event -> {
                loadDisponibilites(selectedService);
                loadCalendar(selectedService);
            });
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur ouverture ajout disponibilité : " + e.getMessage()).show();
        }
    }

    @FXML
    private void modifierDisponibilite() {
        Disponibilite selectedDispo = listViewDisponibilites.getSelectionModel().getSelectedItem();
        if (selectedDispo == null) {
            new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner une disponibilité à modifier !").show();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Service/EditDisponibilite.fxml"));
            Parent root = loader.load();
            EditDisponibilite controller = loader.getController();
            controller.setDisponibilite(selectedDispo);
            Stage stage = new Stage();
            stage.setTitle("Modifier Disponibilité");
            stage.setScene(new Scene(root));
            stage.show();
            stage.setOnHidden(event -> {
                ServiceMed selectedService = listViewServiceMed.getSelectionModel().getSelectedItem();
                if (selectedService != null) {
                    loadDisponibilites(selectedService);
                    loadCalendar(selectedService);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur ouverture modification disponibilité : " + e.getMessage()).show();
        }
    }

    @FXML
    private void supprimerDisponibilite() {
        Disponibilite selectedDispo = listViewDisponibilites.getSelectionModel().getSelectedItem();
        if (selectedDispo == null) {
            new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner une disponibilité à supprimer !").show();
            return;
        }
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Êtes-vous sûr de vouloir supprimer cette disponibilité ?");
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    DisponibiliteService dispoService = new DisponibiliteService();
                    dispoService.supprimer(selectedDispo.getId());
                    ServiceMed selectedService = listViewServiceMed.getSelectionModel().getSelectedItem();
                    if (selectedService != null) {
                        loadDisponibilites(selectedService);
                        loadCalendar(selectedService);
                    }
                    new Alert(Alert.AlertType.INFORMATION, "Disponibilité supprimée avec succès !").show();
                } catch (Exception e) {
                    e.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "Erreur suppression disponibilité : " + e.getMessage()).show();
                }
            }
        });
    }

    @FXML
    public void ouvrirChatBot() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Service/ChatBotGemini.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("ChatBot Médical 🤖");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void rafraichirDisponibilites() {
        ServiceMed selectedService = listViewServiceMed.getSelectionModel().getSelectedItem();
        if (selectedService == null) {
            new Alert(Alert.AlertType.WARNING, "Sélectionnez un service pour rafraîchir.").show();
            return;
        }
        loadDisponibilites(selectedService);
        loadCalendar(selectedService);
    }
}
