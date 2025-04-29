package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
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

public class EditDisponibilite implements Initializable {

    @FXML private DatePicker dpDate;
    @FXML private ComboBox<Integer> cbHeure;
    @FXML private ComboBox<Integer> cbMinute;
    @FXML private CheckBox cbReserve;
    @FXML private ComboBox<ServiceMed> cbServiceMed;

    private final DisponibiliteService disponibiliteService = new DisponibiliteService();
    private final ServiceMedService serviceMedService = new ServiceMedService();
    private Disponibilite currentDisponibilite;

    public void setDisponibilite(Disponibilite disponibilite) {
        this.currentDisponibilite = disponibilite;

        if (disponibilite != null) {
            LocalDateTime dateTime = disponibilite.getDateHeure();
            dpDate.setValue(dateTime.toLocalDate());
            cbHeure.setValue(dateTime.getHour());
            cbMinute.setValue(dateTime.getMinute());
            cbReserve.setSelected(disponibilite.isEstReserve());

            // Sélectionner automatiquement le ServiceMed lié
            try {
                List<ServiceMed> services = serviceMedService.afficher();
                cbServiceMed.setItems(FXCollections.observableArrayList(services));
                services.stream()
                        .filter(s -> s.getId() == disponibilite.getServiceMedId())
                        .findFirst()
                        .ifPresent(serviceMed -> cbServiceMed.getSelectionModel().select(serviceMed));
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Erreur chargement services : " + e.getMessage()).show();
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            List<ServiceMed> services = serviceMedService.afficher();
            cbServiceMed.setItems(FXCollections.observableArrayList(services));

            cbHeure.setItems(FXCollections.observableArrayList(
                    IntStream.range(0, 24).boxed().toList()));
            cbMinute.setItems(FXCollections.observableArrayList(
                    IntStream.range(0, 60).boxed().toList()));

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erreur chargement initial : " + e.getMessage()).show();
        }
    }

    @FXML
    private void modifierDisponibilite() {
        try {
            if (currentDisponibilite == null) {
                new Alert(Alert.AlertType.ERROR, "Aucune disponibilité sélectionnée.").show();
                return;
            }

            LocalDate date = dpDate.getValue();
            Integer heure = cbHeure.getValue();
            Integer minute = cbMinute.getValue();
            boolean estReserve = cbReserve.isSelected();
            ServiceMed selectedService = cbServiceMed.getSelectionModel().getSelectedItem();

            if (date == null || heure == null || minute == null || selectedService == null) {
                new Alert(Alert.AlertType.WARNING, "Veuillez remplir tous les champs.").show();
                return;
            }

            LocalDateTime dateTime = LocalDateTime.of(date, LocalTime.of(heure, minute));

            // Mise à jour des données
            currentDisponibilite.setDateHeure(dateTime);
            currentDisponibilite.setEstReserve(estReserve);
            currentDisponibilite.setServiceMedId(selectedService.getId());

            disponibiliteService.update(currentDisponibilite);

            new Alert(Alert.AlertType.INFORMATION, "Disponibilité modifiée avec succès !").show();

            // Fermer la fenêtre après modification
            Stage stage = (Stage) dpDate.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erreur lors de la modification : " + e.getMessage()).show();
        }
    }
}
