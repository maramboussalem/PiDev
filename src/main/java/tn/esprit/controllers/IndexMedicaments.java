package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tn.esprit.controllers.AddMedicament;
import tn.esprit.controllers.EditMedicament;
import tn.esprit.entities.Medicament;
import tn.esprit.services.MedicamentService;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;

public class IndexMedicaments implements Initializable {

    @FXML private FlowPane cardsContainer;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> sortComboBox;
    @FXML private ComboBox<String> filterComboBox;
    private final MedicamentService service = new MedicamentService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeComboBoxes();
        setupEventHandlers();
        refreshCards();
    }

    private void initializeComboBoxes() {
        sortComboBox.setItems(FXCollections.observableArrayList(
                "Default", "Name (A-Z)", "Name (Z-A)",
                "Price (Low-High)", "Price (High-Low)",
                "Quantity (Low-High)", "Quantity (High-Low)",
                "Expiry (Soon-Later)", "Expiry (Later-Soon)"
        ));
        sortComboBox.getSelectionModel().selectFirst();

        filterComboBox.setItems(FXCollections.observableArrayList(
                "All Medications",
                "Expiring Soon (7 days)",
                "Expiring Soon (30 days)",
                "Low Stock (<10)"
        ));
        filterComboBox.getSelectionModel().selectFirst();
    }

    private void setupEventHandlers() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> refreshCards());
        sortComboBox.valueProperty().addListener((obs, oldVal, newVal) -> refreshCards());
        filterComboBox.valueProperty().addListener((obs, oldVal, newVal) -> refreshCards());
    }

    public void refreshCards() {
        cardsContainer.getChildren().clear();
        List<Medicament> medications = service.afficher();

        // Apply search filter
        String searchText = searchField.getText().toLowerCase();
        if (!searchText.isEmpty()) {
            medications = medications.stream()
                    .filter(m -> m.getNom().toLowerCase().contains(searchText))
                    .toList();
        }

        // Apply additional filters


        // Apply sorting
        String sort = sortComboBox.getValue();
        if (sort != null && !sort.equals("Default")) {
            switch (sort) {
                case "Name (A-Z)":
                    medications.sort((m1, m2) -> m1.getNom().compareToIgnoreCase(m2.getNom()));
                    break;
                case "Name (Z-A)":
                    medications.sort((m1, m2) -> m2.getNom().compareToIgnoreCase(m1.getNom()));
                    break;
                case "Price (Low-High)":
                    medications.sort((m1, m2) -> Double.compare(m1.getPrix(), m2.getPrix()));
                    break;
                case "Price (High-Low)":
                    medications.sort((m1, m2) -> Double.compare(m2.getPrix(), m1.getPrix()));
                    break;
                case "Quantity (Low-High)":
                    medications.sort((m1, m2) -> Integer.compare(m1.getQuantite(), m2.getQuantite()));
                    break;
                case "Quantity (High-Low)":
                    medications.sort((m1, m2) -> Integer.compare(m2.getQuantite(), m1.getQuantite()));
                    break;
                case "Expiry (Soon-Later)":
                    medications.sort((m1, m2) -> m1.getExpireAt().compareTo(m2.getExpireAt()));
                    break;
                case "Expiry (Later-Soon)":
                    medications.sort((m1, m2) -> m2.getExpireAt().compareTo(m1.getExpireAt()));
                    break;
            }
        }

        // Create cards
        for (Medicament medicament : medications) {
            cardsContainer.getChildren().add(createMedicamentCard(medicament));
        }
    }

    private VBox createMedicamentCard(Medicament medicament) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");

        // Image
        ImageView imageView = new ImageView();
        try {
            String imagePath = medicament.getImage();
            if (imagePath == null || imagePath.isEmpty()) {
                imagePath = "default-medicament.png";
            }
            InputStream imageStream = getClass().getResourceAsStream("/images/medicaments/" + imagePath);
            if (imageStream != null) {
                imageView.setImage(new Image(imageStream));
            } else {
                InputStream defaultStream = getClass().getResourceAsStream("/images/default-medicament.png");
                if (defaultStream != null) {
                    imageView.setImage(new Image(defaultStream));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);

        // Name
        Label nameLabel = new Label(medicament.getNom());
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Details
        Label priceLabel = new Label("Price: " + medicament.getPrix() + " DT");
        Label quantityLabel = new Label("Quantity: " + medicament.getQuantite());
        Label typeLabel = new Label("Type: " + medicament.getType());
        Label expiryLabel = new Label("Expires: " + new SimpleDateFormat("dd/MM/yyyy").format(medicament.getExpireAt()));

        // Buttons
        HBox buttonBox = new HBox(10);
        Button showBtn = new Button("Show");
        showBtn.setStyle("-fx-background-color: #606060; -fx-text-fill: white;");
        showBtn.setOnAction(e -> openshowpage(medicament));



        buttonBox.getChildren().addAll(showBtn);
        card.getChildren().addAll(imageView, nameLabel, priceLabel, quantityLabel, typeLabel, expiryLabel, buttonBox);
        return card;
    }


    private void openshowpage(Medicament medicament) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/ShowMedicaments.fxml"));
            Parent root = loader.load();

            ShowMedicamentsController controller = loader.getController();
            controller.setMedicament(medicament);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setTitle("Medication Details");
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open details page: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }


    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}