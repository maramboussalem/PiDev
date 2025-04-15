package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage; // Import Stage class
import tn.esprit.entities.Consultation;
import tn.esprit.entities.Utilisateur; // Assuming Utilisateur is the user class
import tn.esprit.services.ServiceUtilisateur; // Assuming you have a service to fetch the user

public class showDetailsConsultation {

    @FXML
    private Label nomLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private Label diagnosticLabel;

    // User-related labels
    @FXML
    private Label firstNameLabel;
    @FXML
    private Label lastNameLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label sexeLabel;
    @FXML
    private Label adresseLabel;
    @FXML
    private Label telephoneLabel;

    private ServiceUtilisateur serviceUtilisateur; // Corrected the service name

    // Constructor
    public showDetailsConsultation() {
        serviceUtilisateur = new ServiceUtilisateur(); // Instantiate the service class
    }

    public void setConsultation(Consultation consultation) {
        try {
            // Fetch the Utilisateur object using the userId from the consultation
            Utilisateur utilisateur = serviceUtilisateur.getUserById(consultation.getUserId()); // Assuming getUserById fetches by userId

            if (utilisateur != null) {
                // Set the consultation details
                nomLabel.setText("Nom: " + consultation.getNomPatient());
                dateLabel.setText("Date: " + consultation.getDateConsultation());
                diagnosticLabel.setText("Diagnostic: " + consultation.getDiagnostic());

                // Set the user details
               // firstNameLabel.setText("Prénom: " + utilisateur.getPrenom()); // Corrected getter for first name
              //  lastNameLabel.setText("Nom: " + utilisateur.getNom()); // Corrected getter for last name
                emailLabel.setText("Email: " + utilisateur.getEmail());
                sexeLabel.setText("Sexe: " + utilisateur.getSexe());
                adresseLabel.setText("Adresse: " + utilisateur.getAdresse());
                telephoneLabel.setText("Téléphone: " + utilisateur.getTelephone());
            } else {
                // Handle the case where the user is not found
                System.out.println("Utilisateur non trouvé.");
            }
        } catch (Exception e) {
            e.printStackTrace(); // Handle any exceptions gracefully
        }
    }

    // Close the window
    @FXML
    private void closeWindow() {
        Stage stage = (Stage) nomLabel.getScene().getWindow();
        stage.close(); // Close the current window
    }
}
