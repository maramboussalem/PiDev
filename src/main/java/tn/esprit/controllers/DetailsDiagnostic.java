package tn.esprit.controllers;

import com.google.protobuf.compiler.PluginProtos;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.Node;
import tn.esprit.entities.Diagnostic;
import tn.esprit.entities.Utilisateur;
import tn.esprit.services.PdfGeneratorService;
import tn.esprit.services.diagnosticService;
import tn.esprit.services.ServiceUtilisateur;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class DetailsDiagnostic {

    @FXML
    private AnchorPane contentArea;

    @FXML
    private Button cancelButton;

    @FXML
    private TextField dateLabel;

    @FXML
    private TextField descriptionLabel;

    @FXML
    private TextField id_medcinLabel;

    @FXML
    private ImageView image;

    @FXML
    private TextField ip_patientLabel;

    @FXML
    private ListView<String> listview;

    @FXML
    private TextField nameLabel;

    private Diagnostic diagnostic;

    private final diagnosticService service = new diagnosticService();
    private final ServiceUtilisateur userService = new ServiceUtilisateur();

    // Maps to store the full name of patients and doctors
    private Map<Integer, String> patientNameMap = new HashMap<>();
    private Map<Integer, String> medecinNameMap = new HashMap<>();

    public void initialize(Diagnostic diagnostic) {
        this.diagnostic = diagnostic;

        // Load patient and doctor names from the ServiceUtilisateur
        try {
            List<Utilisateur> patients = userService.getPatients();
            for (Utilisateur p : patients) {
                String fullName = p.getNom() + " " + p.getPrenom();
                patientNameMap.put(p.getId(), fullName);
            }

            List<Utilisateur> medecins = userService.getMedecins();
            for (Utilisateur m : medecins) {
                String fullName = m.getNom() + " " + m.getPrenom();
                medecinNameMap.put(m.getId(), fullName);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des utilisateurs : " + e.getMessage());
        }

        if (diagnostic != null) {
            // Set the diagnostic details
            nameLabel.setText(diagnostic.getName());
            descriptionLabel.setText(diagnostic.getDescription());
            dateLabel.setText(diagnostic.getDate_diagnostic().toString());

            // Get the names of the patient and doctor using their IDs
            String patientName = patientNameMap.getOrDefault(diagnostic.getPatient_id(), "Inconnu");
            String medecinName = medecinNameMap.getOrDefault(diagnostic.getMedecin_id(), "Inconnu");

            // Display the names in the TextFields
            ip_patientLabel.setText(patientName);
            id_medcinLabel.setText(medecinName);

            // Make TextFields non-editable
            dateLabel.setEditable(false);
            descriptionLabel.setEditable(false);
            id_medcinLabel.setEditable(false);
            ip_patientLabel.setEditable(false);
            nameLabel.setEditable(false);
        }
    }

    @FXML
    void addButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Diagnostic/addDiagnostic.fxml"));
            Parent root = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void deleteButton(ActionEvent event) {
        if (diagnostic != null) {
            try {
                service.supprimer(diagnostic.getId());
                System.out.println("Diagnostic supprimé !");
                closeWindow(event);
            } catch (Exception e) {
                System.err.println("Erreur lors de la suppression : " + e.getMessage());
            }
        } else {
            System.out.println("Aucun diagnostic chargé pour suppression.");
        }
    }


    @FXML
    void pdfButton(ActionEvent event) {
        if (diagnostic != null) {
            // 1. Path where the PDF will be saved
            String userHome = System.getProperty("user.home");
            String filePath = userHome + "/Desktop/Diagnostic_" + diagnostic.getId() + ".pdf";

            // 2. Generate the PDF using only the Diagnostic data
            PdfGeneratorService pdfService = new PdfGeneratorService();
            pdfService.generatePdf(diagnostic, filePath); // Only pass Diagnostic here

            // 3. Show confirmation alert with file path (in French)
            Alert alert = new Alert(Alert.AlertType.INFORMATION,
                    "Le PDF a été généré avec succès !\nChemin du fichier : " + filePath,
                    ButtonType.OK);
            alert.setTitle("Génération du PDF");
            alert.setHeaderText("PDF Sauvegardé avec succès");
            alert.showAndWait();

            // 4. Try to open the file automatically
            try {
                File file = new File(filePath); // Correct file handling
                if (file.exists()) {
                    Desktop.getDesktop().open(file); // Open the file in the default PDF viewer
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Impossible d'ouvrir le fichier PDF.");
            }
        } else {
            System.out.println("Aucun diagnostic chargé pour générer le PDF.");
        }
    }





    @FXML
    void closeWindow(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
