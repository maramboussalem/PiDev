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
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tn.esprit.entities.Fournisseur;
import tn.esprit.services.FournisseurService;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class IndexFournisseur implements Initializable {

    @FXML private Button addBtn;
    @FXML private FlowPane cardsContainer;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> sortComboBox;

    private final FournisseurService service = new FournisseurService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeComboBox();
        setupEventHandlers();
        refreshCards();
    }

    private void initializeComboBox() {
        sortComboBox.setItems(FXCollections.observableArrayList(
                "Default",
                "Name (A-Z)",
                "Name (Z-A)",
                "Email (A-Z)",
                "Email (Z-A)"
        ));
        sortComboBox.getSelectionModel().selectFirst();
    }
    private void openShowForm(Fournisseur supplier) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fournisseur/ShowFournisseur.fxml"));
            Parent root = loader.load();

            ShowFournisseurController controller = loader.getController();
            controller.setFournisseur(supplier);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setTitle("Détails du fournisseur");
            stage.showAndWait();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir les détails: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    private void setupEventHandlers() {
        addBtn.setOnAction(this::openAddForm);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> refreshCards());
        sortComboBox.valueProperty().addListener((obs, oldVal, newVal) -> refreshCards());
    }

    public void refreshCards() {
        cardsContainer.getChildren().clear();
        List<Fournisseur> suppliers = service.afficher();

        // Apply search filter
        String searchText = searchField.getText().toLowerCase();
        if (!searchText.isEmpty()) {
            suppliers = suppliers.stream()
                    .filter(s -> s.getNom_fournisseur().toLowerCase().contains(searchText))
                    .toList();
        }

        // Apply sorting
        String sort = sortComboBox.getValue();
        if (sort != null && !sort.equals("Default")) {
            switch (sort) {
                case "Name (A-Z)":
                    suppliers.sort((s1, s2) -> s1.getNom_fournisseur().compareToIgnoreCase(s2.getNom_fournisseur()));
                    break;
                case "Name (Z-A)":
                    suppliers.sort((s1, s2) -> s2.getNom_fournisseur().compareToIgnoreCase(s1.getNom_fournisseur()));
                    break;
                case "Email (A-Z)":
                    suppliers.sort((s1, s2) -> s1.getEmail().compareToIgnoreCase(s2.getEmail()));
                    break;
                case "Email (Z-A)":
                    suppliers.sort((s1, s2) -> s2.getEmail().compareToIgnoreCase(s1.getEmail()));
                    break;
            }
        }

        // Create cards
        for (Fournisseur supplier : suppliers) {
            cardsContainer.getChildren().add(createSupplierCard(supplier));
        }
    }

    private VBox createSupplierCard(Fournisseur supplier) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");

        // Supplier Icon
        ImageView imageView = new ImageView();
        try {
            InputStream imageStream = getClass().getResourceAsStream("/images/supplier.png");
            if (imageStream != null) {
                imageView.setImage(new Image(imageStream));
            }
        } catch (Exception e) {
            System.err.println("Error loading supplier icon: " + e.getMessage());
        }
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);
        imageView.setPreserveRatio(true);

        // Name
        Label nameLabel = new Label(supplier.getNom_fournisseur());
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Details
        Label emailLabel = new Label("Email: " + supplier.getEmail());
        Label addressLabel = new Label("Address: " + supplier.getAdresse());
        Label phoneLabel = new Label("Phone: " + supplier.getTelephone());
        emailLabel.setStyle("-fx-text-fill: black;");
        addressLabel.setStyle("-fx-text-fill: black;");
        phoneLabel.setStyle("-fx-text-fill: black;");

        // Buttons
        HBox buttonBox = new HBox(10);
        Button editBtn = new Button("Edit");
        editBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        editBtn.setOnAction(e -> openEditForm(supplier));
        // Ajouter un bouton "Voir"
        Button viewBtn = new Button("View");
        viewBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        viewBtn.setOnAction(e -> openShowForm(supplier));
        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        deleteBtn.setOnAction(e -> deleteSupplier(supplier));

        buttonBox.getChildren().addAll( viewBtn,editBtn, deleteBtn);
        card.getChildren().addAll(imageView, nameLabel, emailLabel, addressLabel, phoneLabel, buttonBox);
        return card;
    }

    private void openAddForm(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fournisseur/AddFournisseur.fxml"));
            Parent root = loader.load();

            AddFournisseurController controller = loader.getController();
            controller.setRefreshCallback(this::refreshCards);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setTitle("Add New Supplier");
            stage.showAndWait();
        } catch (IOException e) {
            showAlert("Error", "Failed to open form: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void openEditForm(Fournisseur supplier) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fournisseur/EditFournisseur.fxml"));
            Parent root = loader.load();

            EditFournisseurController controller = loader.getController();
            controller.setSupplierData(supplier);
            controller.setRefreshCallback(this::refreshCards);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setTitle("Edit Supplier");
            stage.showAndWait();
        } catch (IOException e) {
            showAlert("Error", "Failed to open edit form: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void deleteSupplier(Fournisseur supplier) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete " + supplier.getNom_fournisseur() + "?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                service.supprimer(supplier.getId());
                refreshCards();
                showAlert("Success", "Supplier deleted successfully", Alert.AlertType.INFORMATION);
            }
        });
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}