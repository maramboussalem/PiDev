package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.entities.Consultation;
import tn.esprit.entities.Utilisateur;
import tn.esprit.services.ConsultationService;
import tn.esprit.services.ServiceUtilisateur;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.util.*;

public class AddConsultation implements Initializable {

    @FXML
    private DatePicker date_consultation;

    @FXML
    private TextArea diagnostic;

    @FXML
    private Button goToListButton;

    @FXML
    private TextField nom_patient;

    @FXML
    private Button submit;

    @FXML
    private ComboBox<String> patientName;

    @FXML
    private Label nomPatientError;

    @FXML
    private Label diagnosticError;

    @FXML
    private Label dateConsultationError;

    @FXML
    private Label userError;

    private Map<String, Integer> patientNameToIdMap = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();

        try {
            List<Utilisateur> patients = serviceUtilisateur.getPatients();
            for (Utilisateur patient : patients) {
                String fullName = patient.getNom() + " " + patient.getPrenom();
                patientName.getItems().add(fullName);
                patientNameToIdMap.put(fullName, patient.getId());
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des patients : " + e.getMessage());
        }
    }

    @FXML
    void addConsultation(ActionEvent event) {
        if (validateInputs()) {
            try {
                String selectedName = patientName.getValue();
                int userId = patientNameToIdMap.get(selectedName);

                Consultation consultation = new Consultation();
                consultation.setNomPatient(nom_patient.getText());
                consultation.setDiagnostic(diagnostic.getText());
                consultation.setDateConsultation(Date.valueOf(date_consultation.getValue()));
                consultation.setUserId(userId);

                ConsultationService service = new ConsultationService();
                service.ajouter(consultation);

                System.out.println("Consultation ajoutée avec succès !");
                goToList(event);
            } catch (Exception e) {
                System.err.println("Erreur lors de l'ajout de la consultation : " + e.getMessage());
            }
        }
    }

    @FXML
    void goToList(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Consultation/indexConsultation.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Liste des Consultations");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la page : " + e.getMessage());
        }
    }

    private boolean validateInputs() {
        boolean isValid = true;

        nomPatientError.setText("");
        diagnosticError.setText("");
        dateConsultationError.setText("");
        userError.setText("");

        if (nom_patient.getText().isEmpty()) {
            isValid = false;
            nomPatientError.setText("Patient's name is required.");
        }

        if (diagnostic.getText().isEmpty()) {
            isValid = false;
            diagnosticError.setText("Diagnostic is required.");
        }

        if (date_consultation.getValue() == null) {
            isValid = false;
            dateConsultationError.setText("Consultation date is required.");
        }

        if (patientName.getValue() == null || !patientNameToIdMap.containsKey(patientName.getValue())) {
            isValid = false;
            userError.setText("Please select a valid patient.");
        }

        return isValid;
    }
}
