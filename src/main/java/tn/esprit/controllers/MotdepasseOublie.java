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
import tn.esprit.services.ServiceUtilisateur;

import java.io.IOException;

public class MotdepasseOublie {

    @FXML
    private TextField emailField;

    @FXML
    private Label errorLabel;

    private final ServiceUtilisateur service = new ServiceUtilisateur();

    @FXML
    void Rechercher(ActionEvent event) {
        String email = emailField.getText();

        if (email.isEmpty()) {
            errorLabel.setText("Veuillez entrer votre adresse e-mail.");
            return;
        }

        try {
            if (!service.emailExists(email)) {
                errorLabel.setText("Aucun compte associé à cet e-mail.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Utilisateur/ChoixMethode.fxml"));
            Parent root = loader.load();

            // Correctly cast to ChoixMethode controller
            ChoixMethode controller = loader.getController();
            controller.setEmail(email);

            // Changement de scène
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root));

        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Erreur lors du traitement de la requête.");
        }
    }

    @FXML
    void login(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Utilisateur/login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}