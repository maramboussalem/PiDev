package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import tn.esprit.entities.Diagnostic;

public class DetailsDiagnostic {

    @FXML
    private Button cancelButton;

    @FXML
    private Label dateLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Label id_medcinLabel;

    @FXML
    private Label ip_patientLabel;

    @FXML
    private Label nameLabel;

    @FXML
    void closeWindow(ActionEvent event) {

    }

    public void initialize(Diagnostic d) {
    }
}
