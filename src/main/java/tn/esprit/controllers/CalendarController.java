package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import tn.esprit.entities.Rdv;
import tn.esprit.services.RdvService;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class CalendarController {

    @FXML
    private TextField monthSearchField;

    @FXML
    private Button searchButton;

    @FXML
    private VBox calendarContainer;

    private RdvService rdvService;
    private ObservableList<Rdv> rdvList;
    private final String[] monthsFr = {"Janvier", "Février", "Mars", "Avril", "Mai", "Juin", "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"};
    private final String[] daysFr = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"};
    private final String currentYear = String.valueOf(LocalDateTime.now().getYear());
    private Map<Integer, String> serviceNameMap;

    @FXML
    public void initialize() {
        rdvService = new RdvService();
        rdvList = FXCollections.observableArrayList();

        // Fetch the service name mapping
        try {
            serviceNameMap = rdvService.getServiceNameMap();
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors de la récupération des noms de services: " + e.getMessage());
            alert.showAndWait();
            return;
        }

        // Fetch all RDVs from the database
        try {
            rdvList.addAll(rdvService.afficher());
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors de la récupération des rendez-vous: " + e.getMessage());
            alert.showAndWait();
        }

        // Generate calendar for each month
        for (int monthIndex = 0; monthIndex < 12; monthIndex++) {
            calendarContainer.getChildren().add(createMonthCalendar(monthIndex));
        }
    }

    private VBox createMonthCalendar(int monthIndex) {
        // Month container
        VBox monthBox = new VBox(20);
        monthBox.setId("month-" + monthIndex);
        monthBox.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #e0e7ff; -fx-border-width: 1; -fx-padding: 40; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 30, 0, 0, 6);");
        monthBox.setOnMouseEntered(e -> monthBox.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #e0e7ff; -fx-border-width: 1; -fx-padding: 40; -fx-effect: dropshadow(gaussian, rgba(0,123,255,0.15), 40, 0, 0, 12); -fx-translate-y: -8;"));
        monthBox.setOnMouseExited(e -> monthBox.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #e0e7ff; -fx-border-width: 1; -fx-padding: 40; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 30, 0, 0, 6);"));

        // Month header
        Label monthHeader = new Label("📅 " + monthsFr[monthIndex] + " " + currentYear);
        monthHeader.setStyle("-fx-font-size: 28; -fx-font-weight: bold; -fx-text-fill: linear-gradient(to right, #3498db, #8e44ad); -fx-alignment: center;");
        monthBox.getChildren().add(monthHeader);

        // Table for the month
        TableView<ObservableList<VBox>> table = new TableView<>();
        table.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 15; -fx-border-radius: 15; -fx-border-color: #e0e7ff; -fx-border-width: 1;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Define columns (days of the week)
        String[] colors = {"#3498db", "#2980b9", "#1f618d", "#5499c7", "#5dade2", "#7bb7e6", "#a9cbe7"};
        for (int dayIndex = 0; dayIndex < 7; dayIndex++) {
            TableColumn<ObservableList<VBox>, VBox> column = new TableColumn<>(daysFr[dayIndex]);
            column.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: " + colors[dayIndex] + "; -fx-alignment: center;");
            column.setMinWidth(150); // Ensure columns are wide enough to display content
            final int index = dayIndex;

            // Set a custom cell factory to render the VBox
            column.setCellFactory(col -> new TableCell<ObservableList<VBox>, VBox>() {
                @Override
                protected void updateItem(VBox item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                    } else {
                        setGraphic(item);
                    }
                }
            });

            // Set the cell value factory to get the VBox at the correct index
            column.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().get(index)));
            column.setSortable(false);
            table.getColumns().add(column);
        }

        // Populate table with RDVs for each day
        ObservableList<ObservableList<VBox>> rowData = FXCollections.observableArrayList();
        ObservableList<VBox> row = FXCollections.observableArrayList();
        for (int dayIndex = 0; dayIndex < 7; dayIndex++) {
            // Filter RDVs for this month and day of the week
            int finalDayIndex = dayIndex;
            List<Rdv> rdvsForDay = rdvList.filtered(rdv ->
                    rdv.getDateRdv().getMonthValue() - 1 == monthIndex &&
                            rdv.getDateRdv().getDayOfWeek().getValue() - 1 == finalDayIndex
            );

            // Create a VBox to hold the RDVs for this day
            VBox dayBox = new VBox(10);
            dayBox.setMinHeight(200);
            dayBox.setStyle("-fx-background-color: " + (rdvsForDay.isEmpty() ? "rgba(182,228,245,0.74)" : "#ffcccc") + "; -fx-border-color: #dfe6e9; -fx-border-width: 2; -fx-background-radius: 15; -fx-border-radius: 15; -fx-padding: 10;");
            dayBox.setOnMouseEntered(e -> dayBox.setStyle("-fx-background-color: " + (rdvsForDay.isEmpty() ? "rgba(182,228,245,0.74)" : "#ffcccc") + "; -fx-border-color: #dfe6e9; -fx-border-width: 2; -fx-background-radius: 15; -fx-border-radius: 15; -fx-padding: 10; -fx-scale-x: 1.03; -fx-scale-y: 1.03; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 25, 0, 0, 10);"));
            dayBox.setOnMouseExited(e -> dayBox.setStyle("-fx-background-color: " + (rdvsForDay.isEmpty() ? "rgba(182,228,245,0.74)" : "#ffcccc") + "; -fx-border-color: #dfe6e9; -fx-border-width: 2; -fx-background-radius: 15; -fx-border-radius: 15; -fx-padding: 10;"));

            if (rdvsForDay.isEmpty()) {
                Label noRdvLabel = new Label("Aucun rendez-vous prévu");
                noRdvLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #7f8c8d; -fx-padding: 20;");
                dayBox.getChildren().add(noRdvLabel);
            } else {
                for (Rdv rdv : rdvsForDay) {
                    HBox rdvBox = new HBox(10);
                    rdvBox.setPadding(new Insets(15, 10, 15, 10));
                    rdvBox.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;");
                    rdvBox.setOnMouseEntered(e -> rdvBox.setStyle("-fx-background-color: #e8f4ff; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0; -fx-translate-x: 5;"));
                    rdvBox.setOnMouseExited(e -> rdvBox.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;"));

                    // Look up the service name using serviceNameId
                    String serviceName = rdv.getServiceNameId() != null ?
                            serviceNameMap.getOrDefault(rdv.getServiceNameId(), "Service inconnu") :
                            "Non défini";
                    Label rdvLabel = new Label("⏰ " + rdv.getDateRdv().format(DateTimeFormatter.ofPattern("dd/MM HH:mm")) + " - Service " + serviceName);
                    rdvLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
                    rdvLabel.setMaxWidth(Double.MAX_VALUE); // Allow the label to grow as needed
                    rdvLabel.setWrapText(true); // Wrap text if it's too long
                    // Add a tooltip to show the full text on hover
                    Tooltip tooltip = new Tooltip(rdvLabel.getText());
                    rdvLabel.setTooltip(tooltip);
                    HBox.setHgrow(rdvLabel, javafx.scene.layout.Priority.ALWAYS); // Ensure the label takes available space

                    Button deleteButton = new Button("🗑️");
                    deleteButton.setStyle("-fx-background-color: #ff4d4f; -fx-text-fill: white; -fx-background-radius: 5;");
                    HBox.setHgrow(deleteButton, javafx.scene.layout.Priority.NEVER); // Prevent the button from taking extra space
                    deleteButton.setOnAction(e -> deleteRdv(rdv.getId()));
                    rdvBox.getChildren().addAll(rdvLabel, deleteButton);
                    dayBox.getChildren().add(rdvBox);
                }
            }

            row.add(dayBox);
        }
        rowData.add(row);
        table.setItems(rowData);

        monthBox.getChildren().add(table);
        return monthBox;
    }

    private void deleteRdv(int rdvId) {
        try {
            rdvService.supprimer(rdvId);
            rdvList.removeIf(rdv -> rdv.getId() == rdvId);
            refreshCalendar();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Rendez-vous supprimé avec succès !");
            alert.showAndWait();
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors de la suppression du rendez-vous: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void refreshCalendar() {
        calendarContainer.getChildren().clear();
        for (int monthIndex = 0; monthIndex < 12; monthIndex++) {
            calendarContainer.getChildren().add(createMonthCalendar(monthIndex));
        }
    }

    @FXML
    private void searchMonth() {
        String input = monthSearchField.getText().toLowerCase();
        for (int monthIndex = 0; monthIndex < 12; monthIndex++) {
            VBox monthBox = (VBox) calendarContainer.lookup("#month-" + monthIndex);
            String monthName = monthsFr[monthIndex].toLowerCase();
            if (monthName.contains(input)) {
                monthBox.setVisible(true);
                monthBox.setManaged(true);
            } else {
                monthBox.setVisible(false);
                monthBox.setManaged(false);
            }
        }
    }
}