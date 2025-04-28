package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import tn.esprit.entities.Fournisseur;
import tn.esprit.entities.Medicament;
import tn.esprit.services.MedicamentService;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;

public class ShowFournisseurController implements Initializable {

    @FXML private ImageView supplierImage;
    @FXML private Label nameLabel;
    @FXML private Label emailLabel;
    @FXML private Label addressLabel;
    @FXML private Label phoneLabel;
    @FXML private ListView<Medicament> medicamentsListView;
    @FXML private Label medicamentCountLabel;
    @FXML private Button closeButton;

    private Fournisseur fournisseur;
    private final MedicamentService medicamentService = new MedicamentService();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configureMedicamentListView();
        closeButton.setOnAction(event -> closeButton.getScene().getWindow().hide());
    }

    public void setFournisseur(Fournisseur fournisseur) {
        this.fournisseur = fournisseur;
        loadFournisseurDetails();
        loadMedicaments();
    }

    private void configureMedicamentListView() {
        medicamentsListView.setCellFactory(param -> new ListCell<Medicament>() {
            @Override
            protected void updateItem(Medicament medicament, boolean empty) {
                super.updateItem(medicament, empty);

                if (empty || medicament == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox container = new VBox(5);

                    // Name and price
                    HBox namePriceBox = new HBox(10);
                    Label nameLabel = new Label(medicament.getNom());
                    nameLabel.setFont(new Font(14));
                    Label priceLabel = new Label(String.format("%.2f DT", medicament.getPrix()));
                    priceLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
                    namePriceBox.getChildren().addAll(nameLabel, priceLabel);

                    // Description
                    Label descLabel = new Label(medicament.getDescription());
                    descLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12;");
                    descLabel.setWrapText(true);

                    // Quantity and expiry
                    HBox detailsBox = new HBox(20);
                    Label quantityLabel = new Label("Stock: " + medicament.getQuantite());
                    Label typeLabel = new Label("Type: " + medicament.getType());
                    Label expiryLabel = new Label("Expire: " +
                            (medicament.getExpireAt() != null ? dateFormat.format(medicament.getExpireAt()) : "N/A"));

                    detailsBox.getChildren().addAll(quantityLabel, typeLabel, expiryLabel);

                    container.getChildren().addAll(namePriceBox, descLabel, detailsBox);
                    setGraphic(container);
                }
            }
        });
    }

    private void loadFournisseurDetails() {
        try {
            supplierImage.setImage(new Image(getClass().getResourceAsStream("/images/medic/supplier.png")));
        } catch (Exception e) {
            System.err.println("Error loading supplier image: " + e.getMessage());
        }

        nameLabel.setText(fournisseur.getNom_fournisseur());
        emailLabel.setText("Email: " + fournisseur.getEmail());
        addressLabel.setText("Adresse: " + fournisseur.getAdresse());
        phoneLabel.setText("Telephone: " + fournisseur.getTelephone());
    }

    private void loadMedicaments() {

        List<Medicament> medicaments = medicamentService.getMedicamentsByFournisseur(fournisseur.getId());
        medicamentsListView.getItems().setAll(medicaments);
        medicamentCountLabel.setText("Nombre de medicaments: " + medicaments.size());
        System.out.println(medicaments);
        System.out.println(fournisseur.getId());

    }
}