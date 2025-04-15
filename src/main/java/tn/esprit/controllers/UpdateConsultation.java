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

public class UpdateConsultation implements Initializable {

    @FXML
    private DatePicker date_consultation;

    @FXML
    private TextArea diagnostic;

    @FXML
    private Button goToListButton;

    @FXML
    private ComboBox<String> patientName;

    @FXML
    private Button submit;

    @FXML
    private Label diagnosticError;

    @FXML
    private Label dateConsultationError;

    @FXML
    private Label userError;

    private Map<String, Integer> patientNameToIdMap = new HashMap<>();
    private Consultation consultationToUpdate;

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

    public void setConsultation(Consultation consultation) {
        this.consultationToUpdate = consultation;
        patientName.setValue(consultation.getNomPatient());
        date_consultation.setValue(consultation.getDateConsultation().toLocalDate());
        diagnostic.setText(consultation.getDiagnostic());
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

    @FXML
    void updateConsultation(ActionEvent event) {
        if (validateInputs()) {
            try {
                String selectedName = patientName.getValue();
                int userId = patientNameToIdMap.get(selectedName);

                consultationToUpdate.setNomPatient(selectedName);
                consultationToUpdate.setDiagnostic(diagnostic.getText());
                consultationToUpdate.setDateConsultation(Date.valueOf(date_consultation.getValue()));
                consultationToUpdate.setUserId(userId);

                new ConsultationService().modifier(consultationToUpdate);
                System.out.println("Consultation mise à jour avec succès !");
                goToList(event);
            } catch (Exception e) {
                System.err.println("Erreur lors de la mise à jour : " + e.getMessage());
            }
        }
    }

    private boolean validateInputs() {
        boolean isValid = true;

        diagnosticError.setText("");
        dateConsultationError.setText("");
        userError.setText("");

        if (diagnostic.getText().isEmpty()) {
            diagnosticError.setText("Le diagnostic est requis.");
            isValid = false;
        }

        if (date_consultation.getValue() == null) {
            dateConsultationError.setText("Veuillez sélectionner une date.");
            isValid = false;
        }

        if (patientName.getValue() == null || !patientNameToIdMap.containsKey(patientName.getValue())) {
            userError.setText("Veuillez sélectionner un patient valide.");
            isValid = false;
        }

        return isValid;
    }
}
