package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import tn.esprit.entities.Diagnostic;
import tn.esprit.entities.Utilisateur;
import tn.esprit.services.diagnosticService;
import tn.esprit.services.ServiceUtilisateur;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class AddDiagnostic implements Initializable {

    @FXML
    private AnchorPane contentArea;

    @FXML
    private DatePicker date_diagnostic;

    @FXML
    private TextField description;

    @FXML
    private TextField name;

    @FXML
    private ComboBox<String> medecinName;

    @FXML
    private ComboBox<String> patientName;

    @FXML
    private Label nameError;

    @FXML
    private Label descriptionError;

    @FXML
    private Label dateError;

    @FXML
    private Label patientError;

    @FXML
    private Label medecinError;

    private Map<String, Integer> patientMap = new HashMap<>();
    private Map<String, Integer> medecinMap = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();

        try {
            List<Utilisateur> patients = serviceUtilisateur.getPatients();
            for (Utilisateur p : patients) {
                String fullName = p.getNom() + " " + p.getPrenom();
                patientName.getItems().add(fullName);
                patientMap.put(fullName, p.getId());
            }

            List<Utilisateur> medecins = serviceUtilisateur.getMedecins();
            for (Utilisateur m : medecins) {
                String fullName = m.getNom() + " " + m.getPrenom();
                medecinName.getItems().add(fullName);
                medecinMap.put(fullName, m.getId());
            }

        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des utilisateurs : " + e.getMessage());
        }
    }

    @FXML
    void addButton(ActionEvent event) {
        if (validateInputs()) {
            try {
                Diagnostic diagnostic = new Diagnostic();
                diagnostic.setName(name.getText());
                diagnostic.setDescription(description.getText());
                diagnostic.setDate_diagnostic(Date.valueOf(date_diagnostic.getValue()));
                diagnostic.setPatient_id(patientMap.get(patientName.getValue()));
                diagnostic.setMedecin_id(medecinMap.get(medecinName.getValue()));

                diagnosticService service = new diagnosticService();
                service.ajouter(diagnostic);

                // ✅ Alerte de confirmation
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText(null);
                alert.setContentText("Le diagnostic a été ajouté avec succès !");

                // ✅ Attendre confirmation utilisateur puis aller à la liste
                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        goToList(event);
                    }
                });

            } catch (Exception e) {
                System.err.println("Erreur lors de l'ajout du diagnostic : " + e.getMessage());
            }
        }
    }


    @FXML
    void retourButton(ActionEvent event) {
        // ✅ Demande de confirmation avant retour
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Annuler l'ajout ?");
        alert.setContentText("Êtes-vous sûr de vouloir quitter sans enregistrer ?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            goToList(event);
        }
    }

    private void goToList(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Diagnostic/ListeDiagnostic.fxml"));
            Parent root = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean validateInputs() {
        boolean isValid = true;

        nameError.setText("");
        descriptionError.setText("");
        dateError.setText("");
        patientError.setText("");
        medecinError.setText("");

        if (name.getText().isEmpty()) {
            isValid = false;
            nameError.setText("Nom du diagnostic requis.");
        }

        if (description.getText().isEmpty()) {
            isValid = false;
            descriptionError.setText("Description requise.");
        } else if (description.getText().length() < 16) {
            isValid = false;
            descriptionError.setText("Minimum 16 caractères requis.");
        }

        if (date_diagnostic.getValue() == null) {
            isValid = false;
            dateError.setText("Date requise.");
        } else if (date_diagnostic.getValue().isAfter(LocalDate.now())) {
            isValid = false;
            dateError.setText("La date ne peut pas être dans le futur.");
        }

        if (patientName.getValue() == null || !patientMap.containsKey(patientName.getValue())) {
            isValid = false;
            patientError.setText("Veuillez choisir un patient.");
        }

        if (medecinName.getValue() == null || !medecinMap.containsKey(medecinName.getValue())) {
            isValid = false;
            medecinError.setText("Veuillez choisir un médecin.");
        }

        return isValid;
    }
}
