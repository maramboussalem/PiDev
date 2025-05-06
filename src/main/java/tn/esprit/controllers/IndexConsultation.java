package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import tn.esprit.entities.Consultation;
import tn.esprit.services.ConsultationService;
import tn.esprit.services.GoogleCalendarService;
import com.google.api.services.calendar.model.Event;

import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class IndexConsultation implements Initializable {

    private final ConsultationService service = new ConsultationService();
    private final ObservableList<Consultation> consultationList = FXCollections.observableArrayList();
    private GoogleCalendarService calendarService;

    @FXML
    private ListView<Consultation> listview;

    @FXML
    private Button calendar;

    @FXML
    private Button add;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listview.setItems(consultationList);
        add.setOnAction(this::gotoadd);
        try {
            calendarService = new GoogleCalendarService();
            refreshList();
            syncExistingConsultations(); // Sync existing consultations
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de se connecter à Google Calendar: " + e.getMessage());
        }
        setUpButtons();
    }

    public void refreshList() throws SQLException {
        consultationList.clear();
        consultationList.addAll(service.afficher());
    }

    private void setUpButtons() {
        listview.setCellFactory(param -> new ListCell<Consultation>() {
            @Override
            protected void updateItem(Consultation consultation, boolean empty) {
                super.updateItem(consultation, empty);
                if (empty || consultation == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox container = new VBox(8);
                    container.setStyle("-fx-background-color: #f4f4f4; -fx-padding: 15; -fx-background-radius: 10;");
                    container.setPrefWidth(880);
                    container.setMaxWidth(Double.MAX_VALUE);

                    Label name = new Label("👤 Nom: " + consultation.getNomPatient());
                    name.setStyle("-fx-font-weight: bold; -fx-font-size: 15;");

                    Label date = new Label("📅 Date: " + consultation.getDateConsultation());
                    date.setStyle("-fx-font-size: 14;");

                    Label diag = new Label("🩺 Diagnostic: " + consultation.getDiagnostic());
                    diag.setStyle("-fx-font-size: 14;");

                    Button detailButton = new Button("🔍 Détails");
                    Button modifyButton = new Button("✏️ Modifier");
                    Button deleteButton = new Button("🗑 Supprimer");

                    detailButton.setStyle("-fx-background-color: #5c67f2; -fx-text-fill: white; -fx-background-radius: 5;");
                    modifyButton.setStyle("-fx-background-color: #00b8bb; -fx-text-fill: white; -fx-background-radius: 5;");
                    deleteButton.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white; -fx-background-radius: 5;");

                    detailButton.setOnAction(detailEvent -> showDetailsConsultation(consultation));
                    modifyButton.setOnAction(modifyEvent -> modifyConsultation(consultation));
                    deleteButton.setOnAction(deleteEvent -> {
                        try {
                            deleteConsultation(consultation);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });

                    HBox buttonBox = new HBox(10, detailButton, modifyButton, deleteButton);
                    buttonBox.setAlignment(Pos.CENTER_RIGHT);

                    container.getChildren().addAll(name, date, diag, buttonBox);
                    setGraphic(container);
                }
            }
        });
    }

    private void modifyConsultation(Consultation consultation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/consultation/update.fxml"));
            Parent root = loader.load();

            UpdateConsultation updateController = loader.getController();
            updateController.setConsultation(consultation);

            Stage stage = new Stage();
            stage.setTitle("Modifier Consultation");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteConsultation(Consultation consultation) throws SQLException {
        try {
            // Fetch events from Google Calendar to find the matching event
            LocalDate consultationDate = consultation.getDateConsultation().toLocalDate();
            LocalDateTime startOfPeriod = consultationDate.atTime(LocalTime.MIN);
            LocalDateTime endOfPeriod = consultationDate.atTime(LocalTime.MAX);

            List<Event> events = calendarService.getEvents(startOfPeriod, endOfPeriod);

            // Find the event that matches this consultation
            String expectedSummary = "Consultation - " + consultation.getNomPatient();
            Event eventToDelete = events.stream()
                    .filter(event -> {
                        LocalDateTime eventStart = LocalDateTime.ofInstant(
                                new java.util.Date(event.getStart().getDateTime() != null
                                        ? event.getStart().getDateTime().getValue()
                                        : event.getStart().getDate().getValue()).toInstant(),
                                ZoneId.systemDefault());
                        String eventSummary = event.getSummary() != null ? event.getSummary() : "";
                        return eventSummary.equals(expectedSummary) && eventStart.toLocalDate().equals(consultationDate);
                    })
                    .findFirst()
                    .orElse(null);

            // Delete the event from Google Calendar if found
            if (eventToDelete != null) {
                calendarService.deleteEvent(eventToDelete.getId());
            }

            // Delete the consultation from the database
            service.supprimer(consultation.getId());
            refreshList();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la suppression de l'événement Google Calendar: " + e.getMessage());
        }
    }

    @FXML
    public void gotoadd(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/consultation/addConsultation.fxml"));
            Parent root = loader.load();

            Stage newStage = new Stage();
            newStage.setTitle("Ajouter Consultation");
            newStage.setScene(new Scene(root));
            newStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void showDetailsConsultation(Consultation consultation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/consultation/showDetailsConsultation.fxml"));
            Parent root = loader.load();

            showDetailsConsultation controller = loader.getController();
            controller.setConsultation(consultation);

            Stage stage = new Stage();
            stage.setTitle("Détails Consultation");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void loadCalendar(ActionEvent event2) {
        try {
            // Fetch events for the next 7 days
            LocalDate today = LocalDate.now();
            LocalDateTime startOfPeriod = today.atTime(LocalTime.MIN);
            LocalDateTime endOfPeriod = today.plusDays(7).atTime(LocalTime.MAX);

            List<Event> events = calendarService.getEvents(startOfPeriod, endOfPeriod);

            // Create a new window to display the calendar
            Stage calendarStage = new Stage();
            calendarStage.setTitle("Calendrier des 7 prochains jours");

            VBox mainBox = new VBox(10);
            mainBox.setStyle("-fx-padding: 20; -fx-background-color: #f9f9f9;");

            Label titleLabel = new Label("Calendrier des 7 prochains jours");
            titleLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #00b8bb;");

            // Create a grid for the 7 days
            GridPane calendarGrid = new GridPane();
            calendarGrid.setHgap(10);
            calendarGrid.setVgap(10);
            calendarGrid.setAlignment(Pos.CENTER);

            // Add day headers
            DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEE d MMM");
            for (int i = 0; i < 7; i++) {
                LocalDate day = today.plusDays(i);
                Label dayLabel = new Label(day.format(dayFormatter));
                dayLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
                calendarGrid.add(dayLabel, i, 0);
            }

            // Add event cells
            for (int i = 0; i < 7; i++) {
                LocalDate day = today.plusDays(i);
                LocalDateTime dayStart = day.atTime(LocalTime.MIN);
                LocalDateTime dayEnd = day.atTime(LocalTime.MAX);

                // Filter events for this day
                List<Event> dayEvents = events.stream()
                        .filter(event -> {
                            LocalDateTime eventStart = LocalDateTime.ofInstant(
                                    new java.util.Date(event.getStart().getDateTime() != null
                                            ? event.getStart().getDateTime().getValue()
                                            : event.getStart().getDate().getValue()).toInstant(),
                                    ZoneId.systemDefault());
                            return !eventStart.isBefore(dayStart) && !eventStart.isAfter(dayEnd);
                        })
                        .collect(Collectors.toList());

                // Aggregate events by patient name to avoid duplicates
                Map<String, Event> uniqueEvents = new HashMap<>();
                for (Event evt : dayEvents) {
                    String summary = evt.getSummary() != null ? evt.getSummary() : "Sans titre";
                    uniqueEvents.putIfAbsent(summary, evt); // Keep only the first occurrence of each patient
                }

                VBox dayBox = new VBox(5);
                dayBox.setPrefSize(120, 150); // Increased height for better readability
                dayBox.setStyle(uniqueEvents.isEmpty()
                        ? "-fx-background-color: #d4edda; -fx-background-radius: 10; -fx-border-radius: 10;" // Green for no events
                        : "-fx-background-color: #f8d7da; -fx-background-radius: 10; -fx-border-radius: 10;"); // Red for events

                if (!uniqueEvents.isEmpty()) {
                    for (Event evt : uniqueEvents.values()) {
                        String summary = evt.getSummary() != null ? evt.getSummary() : "Sans titre";
                        Label eventLabel = new Label(summary);
                        eventLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #333;");
                        eventLabel.setWrapText(true); // Wrap text to fit within the box

                        // Create delete button with corbeille icon
                        Button deleteButton = new Button("🗑");
                        deleteButton.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white; -fx-background-radius: 5; -fx-font-size: 10;");
                        deleteButton.setOnAction(e -> {
                            try {
                                deleteEventAndConsultation(evt, calendarStage);
                                // Refresh the calendar by reopening it
                                calendarStage.close();
                                loadCalendar(new ActionEvent(event2.getSource(), event2.getTarget()));
                            } catch (SQLException | IOException ex) {
                                showAlert("Erreur", "Erreur lors de la suppression: " + ex.getMessage());
                            }
                        });

                        HBox eventBox = new HBox(5);
                        eventBox.getChildren().addAll(eventLabel, deleteButton);
                        dayBox.getChildren().add(eventBox);
                    }
                } else {
                    Label noEventLabel = new Label("Aucun événement");
                    noEventLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #666;");
                    dayBox.getChildren().add(noEventLabel);
                }

                calendarGrid.add(dayBox, i, 1);
            }

            mainBox.getChildren().addAll(titleLabel, calendarGrid);

            Scene scene = new Scene(mainBox, 900, 350); // Increased window height for better layout
            calendarStage.setScene(scene);
            calendarStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les événements: " + e.getMessage());
        }
    }

    private void deleteEventAndConsultation(Event event, Stage calendarStage) throws SQLException, IOException {
        // Step 1: Delete the event from Google Calendar
        calendarService.deleteEvent(event.getId());

        // Step 2: Find and delete the corresponding consultation from the database
        String eventSummary = event.getSummary() != null ? event.getSummary() : "Sans titre";
        if (!eventSummary.startsWith("Consultation - ")) {
            showAlert("Erreur", "Événement non associé à une consultation.");
            return;
        }
        String patientName = eventSummary.substring("Consultation - ".length());

        LocalDateTime eventStart = LocalDateTime.ofInstant(
                new java.util.Date(event.getStart().getDateTime() != null
                        ? event.getStart().getDateTime().getValue()
                        : event.getStart().getDate().getValue()).toInstant(),
                ZoneId.systemDefault());
        LocalDate eventDate = eventStart.toLocalDate();

        // Find the consultation in the database
        List<Consultation> consultations = service.afficher();
        Consultation consultationToDelete = consultations.stream()
                .filter(c -> c.getNomPatient().equals(patientName) && c.getDateConsultation().toLocalDate().equals(eventDate))
                .findFirst()
                .orElse(null);

        if (consultationToDelete != null) {
            service.supprimer(consultationToDelete.getId());
            refreshList(); // Refresh the list in IndexConsultation
        } else {
            showAlert("Avertissement", "Consultation correspondante non trouvée dans la base de données.");
        }
    }

    private void syncExistingConsultations() {
        try {
            // Fetch events for the next 30 days to cover most consultations
            LocalDate today = LocalDate.now();
            LocalDateTime startOfPeriod = today.atTime(LocalTime.MIN);
            LocalDateTime endOfPeriod = today.plusDays(30).atTime(LocalTime.MAX);
            List<Event> existingEvents = calendarService.getEvents(startOfPeriod, endOfPeriod);

            List<Consultation> consultations = service.afficher();
            for (Consultation consultation : consultations) {
                // Convert consultation date to LocalDateTime for comparison
                LocalDate consultationDate = consultation.getDateConsultation().toLocalDate();
                LocalDateTime consultationStart = consultationDate.atTime(LocalTime.MIN);

                // Check if an event already exists for this consultation
                String expectedSummary = "Consultation - " + consultation.getNomPatient();
                boolean eventExists = existingEvents.stream().anyMatch(event -> {
                    LocalDateTime eventStart = LocalDateTime.ofInstant(
                            new java.util.Date(event.getStart().getDateTime() != null
                                    ? event.getStart().getDateTime().getValue()
                                    : event.getStart().getDate().getValue()).toInstant(),
                            ZoneId.systemDefault());
                    String eventSummary = event.getSummary() != null ? event.getSummary() : "";
                    return eventSummary.equals(expectedSummary) && eventStart.toLocalDate().equals(consultationDate);
                });

                // Create event only if it doesn't already exist
                if (!eventExists) {
                    calendarService.createEvent(consultation);
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la synchronisation des consultations existantes: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}