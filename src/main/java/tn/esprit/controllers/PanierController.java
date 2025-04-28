package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import tn.esprit.entities.Medicament;
import tn.esprit.utils.EmailSender;
import tn.esprit.utils.PDFGenerator;
import tn.esprit.utils.SessionPanier;

import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class PanierController implements Initializable {
    @FXML private TableView<PanierItem> panierTable;
    @FXML private Label totalLabel;

    private final SessionPanier panier = SessionPanier.getInstance();

    public void continuerAchats(ActionEvent event) {

    }

    public static class PanierItem {
        private final String nom;
        private final double prix;
        private final int quantite;
        private final double total;

        public PanierItem(String nom, double prix, int quantite) {
            this.nom = nom;
            this.prix = prix;
            this.quantite = quantite;
            this.total = prix * quantite;
        }

        // Getters
        public String getNom() { return nom; }
        public double getPrix() { return prix; }
        public int getQuantite() { return quantite; }
        public double getTotal() { return total; }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configurer les colonnes du TableView
        panierTable.getColumns().forEach(column -> {
            if (column.getText().equals("Médicament")) {
                column.setCellValueFactory(new PropertyValueFactory<>("nom"));
            } else if (column.getText().equals("Prix unitaire")) {
                column.setCellValueFactory(new PropertyValueFactory<>("prix"));
            } else if (column.getText().equals("Quantité")) {
                column.setCellValueFactory(new PropertyValueFactory<>("quantite"));
            } else if (column.getText().equals("Total")) {
                column.setCellValueFactory(new PropertyValueFactory<>("total"));
            }
        });

        actualiserPanier();
    }

    private void actualiserPanier() {
        panierTable.getItems().clear();

        for (Map.Entry<Medicament, Integer> entry : panier.getItems().entrySet()) {
            Medicament medicament = entry.getKey();
            int quantite = entry.getValue();

            panierTable.getItems().add(new PanierItem(
                    medicament.getNom(),
                    medicament.getPrix(),
                    quantite
            ));
        }

        totalLabel.setText(String.format("Total: %.2f DT", panier.getTotal()));
    }

    @FXML
    private void viderPanier() {
        panier.viderPanier();
        actualiserPanier();
    }

    @FXML
    private void validerCommande() {
         TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Validation de commande");
        dialog.setHeaderText("Confirmer votre email pour recevoir la facture");
        dialog.setContentText("Email:" );

        dialog.showAndWait().ifPresent(email -> {
            try {
                // Générer la facture PDF
                File facturePDF = PDFGenerator.generateInvoice(panier, "Client", email);

                // Envoyer l'email avec la facture
                boolean emailSent = EmailSender.sendInvoiceEmail(
                        email,
                        "Client",
                        panier.getTotal(),
                        facturePDF
                );

                if (emailSent) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Commande validée");
                    alert.setHeaderText(null);
                    alert.setContentText("Votre commande a été validée avec succès !\nUne facture a été envoyée à " + email);
                    alert.showAndWait();
                } else {
                    throw new Exception("Échec de l'envoi de l'email");
                }

                panier.viderPanier();
                actualiserPanier();

            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Erreur lors de la validation de la commande");
                alert.setContentText("Une erreur est survenue : " + e.getMessage());
                alert.showAndWait();
                e.printStackTrace();
            }
        });
    }
}