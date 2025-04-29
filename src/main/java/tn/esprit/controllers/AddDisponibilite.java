package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import tn.esprit.entities.Disponibilite;
import tn.esprit.entities.ServiceMed;
import tn.esprit.services.DisponibiliteService;
import tn.esprit.services.ServiceMedService;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.IntStream;

public class AddDisponibilite implements Initializable {

    @FXML private DatePicker dpDate;
    @FXML private CheckBox cbReserve;
    @FXML private ComboBox<ServiceMed> cbServiceMed;
    @FXML private ComboBox<Integer> cbHeure;
    @FXML private ComboBox<Integer> cbMinute;

    private final DisponibiliteService dispoService = new DisponibiliteService();
    private final ServiceMedService serviceMedService = new ServiceMedService();

    // Nouveau : Champ pour stocker un service sélectionné
    private ServiceMed selectedServiceMed;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Chargement des services médicaux
            List<ServiceMed> services = serviceMedService.afficher();
            cbServiceMed.setItems(FXCollections.observableArrayList(services));

            // Initialisation des options d'heure et minute
            cbHeure.setItems(FXCollections.observableArrayList(
                    IntStream.range(0, 24).boxed().toList()));
            cbMinute.setItems(FXCollections.observableArrayList(
                    IntStream.range(0, 60).boxed().toList()));

            // Si un service a été passé avant l'initialisation, on le sélectionne
            if (selectedServiceMed != null) {
                cbServiceMed.setValue(selectedServiceMed);
            }

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erreur chargement services : " + e.getMessage()).show();
        }
    }

    @FXML
    private void ajouterDisponibilite() {
        try {
            // Récupération des valeurs des champs
            LocalDate date = dpDate.getValue();
            Integer heure = cbHeure.getValue();
            Integer minute = cbMinute.getValue();
            boolean estReserve = cbReserve.isSelected();
            ServiceMed serviceMed = cbServiceMed.getValue();

            // Vérification des champs non remplis
            if (date == null || heure == null || minute == null || serviceMed == null) {
                new Alert(Alert.AlertType.WARNING, "Tous les champs doivent être remplis !").show();
                return;
            }

            // Création de l'objet Disponibilite
            LocalTime time = LocalTime.of(heure, minute);
            LocalDateTime dateTime = LocalDateTime.of(date, time);

            Disponibilite dispo = new Disponibilite(0, dateTime, estReserve, serviceMed.getId());
            dispoService.ajouter(dispo);

            // Confirmation du succès
            new Alert(Alert.AlertType.INFORMATION, "Disponibilité ajoutée avec succès !").show();

            // Réinitialiser les champs après l'ajout
            resetFields();

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erreur lors de l'ajout : " + e.getMessage()).show();
        }
    }

    // Nouveau : Méthode pour recevoir un ServiceMed depuis l'extérieur
    public void setServiceMed(ServiceMed serviceMed) {
        this.selectedServiceMed = serviceMed;
    }

    // Méthode pour réinitialiser les champs
    private void resetFields() {
        dpDate.setValue(null);
        cbHeure.setValue(null);
        cbMinute.setValue(null);
        cbReserve.setSelected(false);
        cbServiceMed.setValue(null);
    }
}
