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

import java.io.IOException;

public class VerificationCode {

    @FXML
    private TextField codeVerification;

    @FXML
    private Label errorLabel;

    private String email;
    private final ServiceUtilisateur service = new ServiceUtilisateur();

    public void setEmail(String email) {
        this.email = email;
    }

    @FXML
    void verifierCode(ActionEvent event) {
        String codeSaisi = codeVerification.getText();

        if (codeSaisi == null || codeSaisi.trim().isEmpty()) {
            errorLabel.setText("Veuillez entrer un code de vérification.");
            return;
        }
        try {
            // Récupération de l'utilisateur à partir de l'email
            Utilisateur user = service.getUserByEmail(email);
            if (user == null) {
                errorLabel.setText("Utilisateur non trouvé pour cet email.");
                return;
            }
            String codeBase = user.getVerification_code();

            System.out.println("Code en base : '" + codeBase + "'");
            System.out.println("Code saisi   : '" + codeSaisi + "'");

            if (codeBase != null && codeBase.trim().equalsIgnoreCase(codeSaisi.trim())) {
                // Code correct → interface modification mot de passe
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Utilisateur/ModifierMotdepasse.fxml"));
                Parent root = loader.load();

                ModifierMotdepasse controller = loader.getController();
                controller.setEmail(email);

                Stage stage = (Stage) codeVerification.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } else {
                errorLabel.setText("Code de vérification incorrect.");
            }

        } catch (Exception e) {
            errorLabel.setText("Une erreur s'est produite.");
            e.printStackTrace();
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