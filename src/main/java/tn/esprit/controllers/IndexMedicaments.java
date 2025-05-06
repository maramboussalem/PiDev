package tn.esprit.controllers;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
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
import tn.esprit.entities.Medicament;
import tn.esprit.services.MedicamentService;
import tn.esprit.utils.ImageUtils;
import tn.esprit.utils.SessionPanier;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class IndexMedicaments implements Initializable {

    @FXML private FlowPane cardsContainer;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> sortComboBox;
    @FXML private ComboBox<String> filterComboBox;
    private final MedicamentService service = new MedicamentService();
    @FXML private Button panierButton;

    private final SessionPanier panier = SessionPanier.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeComboBoxes();
        setupEventHandlers();
        loadMedications();
        updatePanierButton();
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
        searchField.textProperty().addListener((obs, oldVal, newVal) -> loadMedications());
        sortComboBox.valueProperty().addListener((obs, oldVal, newVal) -> loadMedications());
        filterComboBox.valueProperty().addListener((obs, oldVal, newVal) -> loadMedications());
        panierButton.setOnAction(e -> ouvrirPanier());
    }

    private void loadMedications() {
        cardsContainer.getChildren().clear();
        List<Medicament> medications = service.afficher();

        // Apply search filter
        String searchText = searchField.getText().toLowerCase();
        if (!searchText.isEmpty()) {
            medications = medications.stream()
                    .filter(m -> m.getNom().toLowerCase().contains(searchText))
                    .toList();
        }
        String sort = sortComboBox.getValue();
        if (sort != null && !sort.equals("Default")) {
            medications = sortMedications(medications, sort);
        }
 for (Medicament medicament : medications) {
            cardsContainer.getChildren().add(createMedicamentCard(medicament));
        }
    }

    private List<Medicament> sortMedications(List<Medicament> medications, String sortCriteria) {
        switch (sortCriteria) {
            case "Name (A-Z)":
                return medications.stream()
                        .sorted((m1, m2) -> m1.getNom().compareToIgnoreCase(m2.getNom()))
                        .toList();
            case "Name (Z-A)":
                return medications.stream()
                        .sorted((m1, m2) -> m2.getNom().compareToIgnoreCase(m1.getNom()))
                        .toList();
            case "Price (Low-High)":
                return medications.stream()
                        .sorted((m1, m2) -> Double.compare(m1.getPrix(), m2.getPrix()))
                        .toList();
            case "Price (High-Low)":
                return medications.stream()
                        .sorted((m1, m2) -> Double.compare(m2.getPrix(), m1.getPrix()))
                        .toList();
            case "Quantity (Low-High)":
                return medications.stream()
                        .sorted((m1, m2) -> Integer.compare(m1.getQuantite(), m2.getQuantite()))
                        .toList();
            case "Quantity (High-Low)":
                return medications.stream()
                        .sorted((m1, m2) -> Integer.compare(m2.getQuantite(), m1.getQuantite()))
                        .toList();
            case "Expiry (Soon-Later)":
                return medications.stream()
                        .sorted((m1, m2) -> m1.getExpireAt().compareTo(m2.getExpireAt()))
                        .toList();
            case "Expiry (Later-Soon)":
                return medications.stream()
                        .sorted((m1, m2) -> m2.getExpireAt().compareTo(m1.getExpireAt()))
                        .toList();
            default:
                return medications;
        }
    }
    private File generateQRCodeImage(String text) throws Exception {
        String filePath = System.getProperty("java.io.tmpdir") + "/med-qr-" + System.currentTimeMillis() + ".png";
        int width = 300;
        int height = 300;
        String fileType = "png";

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.MARGIN, 2);

        BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height, hints);
        File qrFile = new File(filePath);
        MatrixToImageWriter.writeToPath(bitMatrix, fileType, qrFile.toPath());

        return qrFile;
    }
    private VBox createMedicamentCard(Medicament medicament) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");

        // Image loading with better error handling
        ImageView imageView = loadMedicamentImage(medicament);
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

        Button addToCartBtn = new Button("Ajouter au panier");
        addToCartBtn.setStyle("-fx-background-color: #00b8bb; -fx-text-fill: white;");
        addToCartBtn.setOnAction(e -> {
            panier.ajouterAuPanier(medicament, 1);
            updatePanierButton();
            showAlert("Succès", medicament.getNom() + " a été ajouté au panier", Alert.AlertType.INFORMATION);
        });



        //qr code
        ImageView qrCodeImageView = new ImageView();
        qrCodeImageView.setFitWidth(100);
        qrCodeImageView.setFitHeight(100);
        String qrData = "nom: " + medicament.getNom() + "\n" +
                "type: " + medicament.getType() + "\n" +
                "Prix: " + medicament.getPrix() + "\n"  ;
        //qr code
        File qrCodeFile = null;
        try {
            qrCodeFile = generateQRCodeImage(qrData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Image qrImage = new Image(qrCodeFile.toURI().toString());
        qrCodeImageView.setImage(qrImage);

        // Add hover effect
        qrCodeImageView.setOnMouseEntered(e -> qrCodeImageView.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 10, 0, 0, 0);"));
        qrCodeImageView.setOnMouseExited(e -> qrCodeImageView.setStyle(""));

        Label qrLabel = new Label("Scan for details");
        qrLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

        VBox qrBox = new VBox(5, qrCodeImageView, qrLabel);
        qrBox.setStyle("-fx-alignment: center; -fx-border-color: #eee; -fx-border-width: 1; -fx-border-radius: 3;");




        buttonBox.getChildren().addAll(showBtn, addToCartBtn);
        card.getChildren().addAll(imageView, nameLabel, priceLabel, quantityLabel, typeLabel, expiryLabel, buttonBox ,qrBox);
        return card;
    }

    private ImageView loadMedicamentImage(Medicament medicament) {

        return ImageUtils.loadMedicamentImage(medicament.getImage());

    }

    private void ouvrirPanier() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/panier/PanierView.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Votre Panier");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir le panier", Alert.AlertType.ERROR);
        }
    }

    private void updatePanierButton() {
        int nombreItems = panier.getNombreItems();
        panierButton.setText("Panier (" + nombreItems + ")");
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