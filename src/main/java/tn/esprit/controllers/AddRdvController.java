package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import tn.esprit.entities.Rdv;
import tn.esprit.entities.ServiceMed;
import tn.esprit.services.RdvService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AddRdvController {

    @FXML
    private ComboBox<ServiceMed> serviceComboBox;

    @FXML
    private DatePicker datePicker;

    @FXML
    private ComboBox<String> timeComboBox;

    @FXML
    private Button addButton;

    @FXML
    private Button cancelButton;

    @FXML
    private ImageView gifImageView; // Inject the ImageView for the GIF

    private RdvService rdvService;
    private List<Rdv> existingRdvs;

    public AddRdvController() {
        this.rdvService = new RdvService();
    }

    @FXML
    public void initialize() {
        // Load the GIF into the ImageView with debugging
        try {
            // Debug: Check if the resource exists
            String gifPath = "/images/rdv-rendez-vous.gif";
            System.out.println("Tentative de chargement du GIF depuis : " + gifPath);
            System.out.println("Resource URL : " + getClass().getResource(gifPath));

            // Load the GIF
            Image gifImage = new Image(getClass().getResourceAsStream(gifPath));
            if (gifImage.isError()) {
                throw new IOException("Erreur lors du chargement du GIF : image non valide ou corrompue");
            }
            gifImageView.setImage(gifImage);
            System.out.println("GIF chargé avec succès !");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement du GIF : " + e.getMessage());
            // Show an alert to the user
            showErrorAlert("Impossible de charger l'animation GIF : " + e.getMessage());
        }

        // Set tooltips for the controls
        serviceComboBox.setTooltip(new Tooltip("Choisissez un service médical (ex. Cardiologue)"));
        datePicker.setTooltip(new Tooltip("Sélectionnez la date de votre rendez-vous"));
        timeComboBox.setTooltip(new Tooltip("Choisissez une heure disponible"));

        // Populate serviceComboBox with ServiceMed objects
        try {
            List<ServiceMed> services = rdvService.getServices();
            serviceComboBox.setItems(FXCollections.observableArrayList(services));
            serviceComboBox.setCellFactory(param -> new ListCell<ServiceMed>() {
                @Override
                protected void updateItem(ServiceMed item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setTooltip(null);
                    } else {
                        setText(item.getNomService());
                        Tooltip tooltip = new Tooltip(item.getDescriptionMed());
                        setTooltip(tooltip);
                    }
                }
            });
            serviceComboBox.setButtonCell(new ListCell<ServiceMed>() {
                @Override
                protected void updateItem(ServiceMed item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getNomService());
                    }
                }
            });
        } catch (SQLException e) {
            showErrorAlert("Erreur lors de la récupération des services: " + e.getMessage());
        }

        // Restrict DatePicker to future dates
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });

        // Fetch existing RDVs for validation
        try {
            existingRdvs = rdvService.afficher();
        } catch (SQLException e) {
            showErrorAlert("Erreur lors de la récupération des rendez-vous existants: " + e.getMessage());
            existingRdvs = new ArrayList<>();
        }

        // Update timeComboBox when service or date changes
        serviceComboBox.valueProperty().addListener((obs, oldVal, newVal) -> updateTimeSlots());
        datePicker.valueProperty().addListener((obs, oldVal, newVal) -> updateTimeSlots());
    }

    private void updateTimeSlots() {
        if (serviceComboBox.getValue() == null || datePicker.getValue() == null) {
            timeComboBox.setItems(FXCollections.observableArrayList());
            return;
        }

        // Generate possible time slots (e.g., 08:00 to 18:00)
        List<String> allTimes = new ArrayList<>();
        for (int hour = 8; hour <= 18; hour++) {
            allTimes.add(String.format("%02d:00", hour));
        }

        // Filter out booked time slots
        Integer serviceId = serviceComboBox.getValue().getId();
        LocalDate selectedDate = datePicker.getValue();

        List<String> bookedTimes = existingRdvs.stream()
                .filter(rdv -> rdv.getServiceNameId() != null && rdv.getServiceNameId().equals(serviceId) &&
                        rdv.getDateRdv().toLocalDate().equals(selectedDate))
                .map(rdv -> rdv.getDateRdv().format(DateTimeFormatter.ofPattern("HH:mm")))
                .collect(Collectors.toList());

        List<String> availableTimes = allTimes.stream()
                .filter(time -> !bookedTimes.contains(time))
                .collect(Collectors.toList());

        timeComboBox.setItems(FXCollections.observableArrayList(availableTimes));
        if (!availableTimes.isEmpty()) {
            timeComboBox.setValue(availableTimes.get(0));
        } else {
            timeComboBox.setValue(null);
            showWarningAlert("Aucune heure disponible pour cette date et ce service.");
        }
    }

    @FXML
    private void addRdv() {
        // Validate inputs
        if (!validateInputs()) {
            return;
        }

        try {
            ServiceMed selectedService = serviceComboBox.getValue();
            Integer serviceId = selectedService.getId();
            LocalDate date = datePicker.getValue();
            LocalTime time = LocalTime.parse(timeComboBox.getValue());
            LocalDateTime dateRdv = LocalDateTime.of(date, time);

            // Check for time slot conflict
            boolean slotTaken = existingRdvs.stream()
                    .anyMatch(rdv -> rdv.getServiceNameId() != null && rdv.getServiceNameId().equals(serviceId) &&
                            rdv.getDateRdv().equals(dateRdv));
            if (slotTaken) {
                showWarningAlert("Ce créneau horaire est déjà réservé pour ce service.");
                return;
            }

            // Show confirmation dialog
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirmation");
            confirmAlert.setHeaderText("Confirmer le rendez-vous");
            confirmAlert.setContentText("Service: " + selectedService.getNomService() + "\n" +
                    "Date: " + dateRdv.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + "\n" +
                    "Voulez-vous confirmer ce rendez-vous ?");
            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isEmpty() || result.get() != ButtonType.OK) {
                return;
            }

            // Disable the Add button while saving
            addButton.setDisable(true);
            addButton.setText("Ajout en cours...");

            // Create and save Rdv
            Rdv rdv = new Rdv();
            rdv.setServiceNameId(serviceId);
            rdv.setDateRdv(dateRdv);
            rdvService.ajouter(rdv);

            // Update existingRdvs
            existingRdvs.add(rdv);

            // Show success message
            showInfoAlert("Rendez-vous ajouté avec succès !");
            closeWindow();
        } catch (SQLException e) {
            showErrorAlert("Erreur SQL lors de l'ajout du rendez-vous: " + e.getMessage());
        } catch (Exception e) {
            showErrorAlert("Erreur lors de l'ajout du rendez-vous: " + e.getMessage());
        } finally {
            addButton.setDisable(false);
            addButton.setText("Ajouter");
        }
    }

    private boolean validateInputs() {
        boolean isValid = true;
        StringBuilder errorMessage = new StringBuilder("Veuillez corriger les erreurs suivantes :\n");

        if (serviceComboBox.getValue() == null) {
            errorMessage.append("- Sélectionnez un service.\n");
            setInvalidStyle(serviceComboBox);
            isValid = false;
        } else {
            resetStyle(serviceComboBox);
        }

        if (datePicker.getValue() == null) {
            errorMessage.append("- Sélectionnez une date.\n");
            setInvalidStyle(datePicker);
            isValid = false;
        } else {
            resetStyle(datePicker);
        }

        if (timeComboBox.getValue() == null) {
            errorMessage.append("- Sélectionnez une heure.\n");
            setInvalidStyle(timeComboBox);
            isValid = false;
        } else {
            resetStyle(timeComboBox);
        }

        if (!isValid) {
            showWarningAlert(errorMessage.toString());
        }
        return isValid;
    }

    private void setInvalidStyle(Control control) {
        control.setStyle("-fx-border-color: #ff4d4f; -fx-border-width: 2; -fx-background-radius: 10; -fx-border-radius: 10; -fx-background-color: #ffffff;");
    }

    private void resetStyle(Control control) {
        control.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1; -fx-background-radius: 10; -fx-border-radius: 10; -fx-background-color: #ffffff;");
    }

    @FXML
    private void openCalendar(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Rdv/Calendar.fxml"));
            Parent calendarView = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Calendrier des Rendez-vous");
            stage.setScene(new Scene(calendarView));
            stage.show();
        } catch (IOException e) {
            showErrorAlert("Erreur de chargement de l'interface Calendrier : " + e.getMessage());
        }
    }

    @FXML
    private void cancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) addButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void onHover(MouseEvent event) {
        Control control = (Control) event.getSource();
        control.setStyle("-fx-border-color: #3498db; -fx-border-width: 2; -fx-background-radius: 10; -fx-border-radius: 10; -fx-background-color: #f8f9fa; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);");
    }

    @FXML
    private void onExit(MouseEvent event) {
        Control control = (Control) event.getSource();
        if (control.getStyle().contains("-fx-border-color: #ff4d4f")) {
            return; // Don't reset if the field is invalid
        }
        control.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1; -fx-background-radius: 10; -fx-border-radius: 10; -fx-background-color: #ffffff;");
    }

    @FXML
    private void onHoverButton(MouseEvent event) {
        Button button = (Button) event.getSource();
        String baseColor = button.getId().equals("addButton") ? "#4CAF50" : button.getId().equals("calendarButton") ? "#2196F3" : "#f44336";
        button.setStyle("-fx-background-color: linear-gradient(to right, " + baseColor + ", " + baseColor + "); -fx-text-fill: white; -fx-font-size: 14; -fx-font-weight: bold; -fx-padding: 10 30; -fx-background-radius: 30; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0, 0, 5); -fx-scale-x: 1.05; -fx-scale-y: 1.05;");
    }

    @FXML
    private void onExitButton(MouseEvent event) {
        Button button = (Button) event.getSource();
        String baseColor = button.getId().equals("addButton") ? "#4CAF50" : button.getId().equals("calendarButton") ? "#2196F3" : "#f44336";
        String secondaryColor = button.getId().equals("addButton") ? "#45a049" : button.getId().equals("calendarButton") ? "#1e88e5" : "#e53935";
        button.setStyle("-fx-background-color: linear-gradient(to right, " + baseColor + ", " + secondaryColor + "); -fx-text-fill: white; -fx-font-size: 14; -fx-font-weight: bold; -fx-padding: 10 30; -fx-background-radius: 30; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 3);");
    }

    private void showInfoAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarningAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Attention");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}