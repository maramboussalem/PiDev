package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.entities.Utilisateur;
import tn.esprit.services.ServiceUtilisateur;
import tn.esprit.utils.TwilioSMS;

import java.io.IOException;
import java.sql.SQLException;

public class EnvoiCodeVérification {

    @FXML
    private Label errorLabel;

    @FXML
    private TextField phoneField;

    private String email;
    private final ServiceUtilisateur service = new ServiceUtilisateur();

    public void setEmail(String email) {
        this.email = email;
    }

    @FXML
    void envoyerCode(ActionEvent event) {
        String phone = phoneField.getText();

        try {
            Utilisateur user = service.getUserByEmail(email); // This could throw SQLException
            if (user == null || !user.getTelephone().equals(phone)) {
                errorLabel.setText("Numéro incorrect.");
                return;
            }

            // Générer et envoyer le code
            String code = TwilioSMS.generateVerificationCode();
            user.setVerification_code(code);
            service.modifier(user); // This could throw SQLException

            TwilioSMS.sendSMS(phone, "Votre code de vérification est : " + code);

            // Transition to the next scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Utilisateur/VerificationCode.fxml"));
            Parent root = loader.load();

            VerificationCode controller = loader.getController();
            controller.setEmail(email);

            Stage stage = (Stage) phoneField.getScene().getWindow();
            stage.setScene(new Scene(root));

        } catch (SQLException e) {
            // Handle SQLException (database errors)
            errorLabel.setText("Une erreur de base de données s'est produite. Veuillez réessayer.");
            e.printStackTrace(); // You can log this or show more specific details if necessary
        } catch (Exception e) {
            // Handle general exceptions (e.g., FXMLLoader issues)
            errorLabel.setText("Une erreur est survenue. Veuillez réessayer.");
            e.printStackTrace();
        }
    }

    @FXML
    void login(ActionEvent event) {
        try {
            // Chargement du fichier login.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Utilisateur/login.fxml"));
            Parent root = loader.load();

            // Récupérer la scène actuelle et changer son contenu
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
