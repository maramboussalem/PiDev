package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import tn.esprit.entities.Diagnostic;
import tn.esprit.services.diagnosticService;

import java.sql.Date;
import java.sql.SQLException;

public class UpdateDiagnostic {

    @FXML
    private Label dateErrorM;

    @FXML
    private DatePicker date_diagnosticM;

    @FXML
    private Label descriptionErrorM;

    @FXML
    private TextField descriptionM;

    @FXML
    private Label medecinErrorM;

    @FXML
    private ComboBox<Integer> medecinNameM;

    @FXML
    private Label nameErrorM;

    @FXML
    private TextField nameM;

    @FXML
    private Label patientErrorM;

    @FXML
    private ComboBox<Integer> patientNameM;

    private diagnosticService diagnosticService = new diagnosticService();
    private Diagnostic selectedDiagnostic;

    // Appelée depuis ListeDiagnostic
    public void initialize(Diagnostic diagnostic) {
        this.selectedDiagnostic = diagnostic;

        nameM.setText(diagnostic.getName());
        descriptionM.setText(diagnostic.getDescription());
        date_diagnosticM.setValue(diagnostic.getDate_diagnostic().toLocalDate());

        // Simplicité : on remplit manuellement juste l’ID ici
        medecinNameM.getItems().add(diagnostic.getMedecin_id());
        patientNameM.getItems().add(diagnostic.getPatient_id());

        medecinNameM.setValue(diagnostic.getMedecin_id());
        patientNameM.setValue(diagnostic.getPatient_id());
    }

    @FXML
    void update_diagnosticM(ActionEvent event) {
        if (validateForm()) {
            try {
                selectedDiagnostic.setName(nameM.getText());
                selectedDiagnostic.setDescription(descriptionM.getText());
                selectedDiagnostic.setDate_diagnostic(Date.valueOf(date_diagnosticM.getValue()));
                selectedDiagnostic.setMedecin_id(medecinNameM.getValue());
                selectedDiagnostic.setPatient_id(patientNameM.getValue());

                diagnosticService.modifier(selectedDiagnostic);

                System.out.println("Diagnostic updated successfully!");
            } catch (SQLException e) {
                System.err.println("Error updating diagnostic: " + e.getMessage());
            }
        } else {
            System.err.println("Form validation failed");
        }
    }

    private boolean validateForm() {
        boolean isValid = true;

        if (nameM.getText().isEmpty()) {
            nameErrorM.setText("Name is required.");
            isValid = false;
        } else {
            nameErrorM.setText("");
        }

        if (descriptionM.getText().isEmpty()) {
            descriptionErrorM.setText("Description is required.");
            isValid = false;
        } else {
            descriptionErrorM.setText("");
        }

        if (date_diagnosticM.getValue() == null) {
            dateErrorM.setText("Date is required.");
            isValid = false;
        } else {
            dateErrorM.setText("");
        }

        if (medecinNameM.getValue() == null) {
            medecinErrorM.setText("Please select a doctor.");
            isValid = false;
        } else {
            medecinErrorM.setText("");
        }

        if (patientNameM.getValue() == null) {
            patientErrorM.setText("Please select a patient.");
            isValid = false;
        } else {
            patientErrorM.setText("");
        }

        return isValid;
    }
}
