package tn.esprit.controllers.ParametresVitauxM;

import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.json.JSONObject;
import tn.esprit.entities.ParametresVitaux;
import tn.esprit.services.AIClient;
import tn.esprit.services.ParametresVitauxService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AddParametresVitauxM implements Initializable {

    @FXML
    private AnchorPane contentArea;

    @FXML
    private DatePicker created_at;

    @FXML
    private Label dateError;

    @FXML
    private TextField ecg, fc, fr, gad, gsc, name, spo2, tad, tas;

    @FXML
    private Label ecgError, fcError, frError, gadError, gscError, nameError, spo2Error, tadError, tasError;

    @FXML
    private ImageView image;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadImage();
    }

    private void loadImage() {
        String imagePath = "src/main/resources/images/addParametres.gif";
        File file = new File(imagePath);
        if (file.exists()) {
            Image img = new Image(file.toURI().toString());
            image.setImage(img);
        } else {
            System.out.println("Fichier image introuvable : " + imagePath);
            image.setImage(new Image("/images/default.png")); // Assurez-vous d’avoir une image par défaut
        }
    }

    @FXML
    void add_parametresVitaux(ActionEvent event) {
        if (validateInputs()) {
            try {
                // Créer l'objet ParametresVitaux
                ParametresVitaux pv = new ParametresVitaux();
                pv.setName(name.getText());
                pv.setFc(Integer.parseInt(fc.getText()));
                pv.setFr(Integer.parseInt(fr.getText()));
                pv.setEcg(ecg.getText());
                pv.setTas(Integer.parseInt(tas.getText()));
                pv.setTad(Integer.parseInt(tad.getText()));
                pv.setSpo2(Integer.parseInt(spo2.getText()));
                pv.setGsc(Integer.parseInt(gsc.getText()));
                pv.setGad(Integer.parseInt(gad.getText()));
                pv.setCreated_at(Timestamp.valueOf(created_at.getValue().atStartOfDay()));

                // Ajouter les paramètres vitaux au service
                ParametresVitauxService service = new ParametresVitauxService();
                service.ajouter(pv);

                // Create the input for the AI
                String input = String.format(
                        "Vital signs: Pulse Rate: %s BPM, Respiratory Rate: %s BPM, ECG: %s, SpO2: %s%%, Diastolic BP: %s mmHg, Systolic BP: %s mmHg, Glasgow Coma Scale: %s, Glasgow Assessment of Disability: %s",
                        fc.getText(), fr.getText(), ecg.getText(), spo2.getText(), tad.getText(), tas.getText(), gsc.getText(), gad.getText()
                );
                String context = "Patient name: " + name.getText();
                String task = "diagnose";
                String outputFormat = "json";

                // Generate a structured prompt
                String prompt = AIClient.generatePrompt(task, input, context, outputFormat);

                // Get the diagnostic from the AI
                String diagnostic = AIClient.getDiagnostic(prompt);

                // Print the raw diagnostic string to debug
                System.out.println("Raw diagnostic: " + diagnostic);

                // Parse the JSON response
                String pathologie = "Pathologie inconnue";
                String diagnosticMessage = "Diagnostic inconnu";
                try {
                    JSONObject jsonResponse = new JSONObject(diagnostic);
                    pathologie = jsonResponse.optString("pathologie", "Pathologie inconnue");
                    diagnosticMessage = jsonResponse.optString("diagnostic", "Diagnostic inconnu");
                } catch (Exception e) {
                    System.err.println("Error parsing JSON response: " + e.getMessage());
                }

                // Print the result after parsing
                System.out.println("Pathologie: " + pathologie);
                System.out.println("Diagnostic: " + diagnosticMessage);

                // Créer une alerte avec un TextArea
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Résultat du Diagnostic");
                alert.setHeaderText("Analyse du diagnostic par l'IA");

                TextArea textArea = new TextArea(
                        "Pathologie: \n" + (pathologie.isEmpty() ? "Non spécifiée" : pathologie) +
                                "\n\nDiagnostic: \n" + (diagnosticMessage.isEmpty() ? "Aucun diagnostic disponible" : diagnosticMessage)
                );
                textArea.setWrapText(true);
                textArea.setEditable(false);
                textArea.setPrefHeight(400);
                textArea.setPrefWidth(600);

                alert.getDialogPane().setContent(textArea);

                // Afficher l'alerte
                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        historiqueButton(event);
                    }
                });

            } catch (Exception e) {
                System.err.println("❌ Erreur lors de l'ajout des paramètres vitaux : " + e.getMessage());
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Erreur");
                errorAlert.setHeaderText("Une erreur est survenue lors de l'ajout des paramètres.");
                errorAlert.setContentText(e.getMessage());
                errorAlert.showAndWait();
            }
        }
    }

    @FXML
    void closeButton(ActionEvent event) {
        Button button = (Button) event.getSource();

        // Apply DropShadow effect
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web("#4f9efc"));
        shadow.setRadius(10);
        shadow.setSpread(0.5);
        button.setEffect(shadow);

        // Soft Scale Animation (smaller effect)
        ScaleTransition scale = new ScaleTransition(Duration.millis(100), button);
        scale.setFromX(1.0);
        scale.setFromY(1.0);
        scale.setToX(0.97);  // Just a slight shrink
        scale.setToY(0.97);
        scale.setAutoReverse(true);
        scale.setCycleCount(2);

        scale.setOnFinished(e -> {
            // Navigate to home.fxml
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/home.fxml"));
                Parent homeView = loader.load();

                Scene scene = button.getScene();
                Stage stage = (Stage) scene.getWindow();
                stage.setScene(new Scene(homeView));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        scale.play();
    }

    @FXML
    void historiqueButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ParametresVitauxM/IndexParametresVitauxM.fxml"));
            Parent root = loader.load();

            // ⚠️ Important : vérifier s'il a déjà un parent
            if (root.getParent() != null) {
                ((AnchorPane) root.getParent()).getChildren().remove(root);
            }

            contentArea.getChildren().clear();
            contentArea.getChildren().add(root);

        } catch (IOException e) {
            System.err.println("❌ Erreur lors du chargement de la page d'historique : " + e.getMessage());
        }
    }

    private boolean validateInputs() {
        boolean isValid = true;

        // Reset error messages
        nameError.setText("");
        fcError.setText("");
        frError.setText("");
        ecgError.setText("");
        tasError.setText("");
        tadError.setText("");
        spo2Error.setText("");
        gscError.setText("");
        gadError.setText("");
        dateError.setText("");

        // Name validation
        if (name.getText().isEmpty()) {
            isValid = false;
            nameError.setText("Nom requis.");
        }

        // FC validation (must be between 30 and 120)
        if (!isInteger(fc.getText())) {
            isValid = false;
            fcError.setText("FC invalide.");
        } else {
            int fcValue = Integer.parseInt(fc.getText());
            if (fcValue < 30 || fcValue > 120) {
                isValid = false;
                fcError.setText("FC doit être entre 30 et 120.");
            }
        }

        // FR validation (must be between 0 and 20)
        if (!isInteger(fr.getText())) {
            isValid = false;
            frError.setText("FR invalide.");
        } else {
            int frValue = Integer.parseInt(fr.getText());
            if (frValue < 0 || frValue > 20) {
                isValid = false;
                frError.setText("FR doit être entre 0 et 20.");
            }
        }

        // SpO2 validation (must be between 0 and 100)
        if (!isInteger(spo2.getText())) {
            isValid = false;
            spo2Error.setText("SpO2 invalide.");
        } else {
            int spo2Value = Integer.parseInt(spo2.getText());
            if (spo2Value < 0 || spo2Value > 100) {
                isValid = false;
                spo2Error.setText("SpO2 doit être entre 0 et 100.");
            }
        }

        // TAD validation (must be between 30 and 150)
        if (!isInteger(tad.getText())) {
            isValid = false;
            tadError.setText("TAD invalide.");
        } else {
            int tadValue = Integer.parseInt(tad.getText());
            if (tadValue < 30 || tadValue > 150) {
                isValid = false;
                tadError.setText("TAD doit être entre 30 et 150.");
            }
        }

        // TAS validation (must be between 50 and 200)
        if (!isInteger(tas.getText())) {
            isValid = false;
            tasError.setText("TAS invalide.");
        } else {
            int tasValue = Integer.parseInt(tas.getText());
            if (tasValue < 50 || tasValue > 200) {
                isValid = false;
                tasError.setText("TAS doit être entre 50 et 200.");
            }
        }

        // GSC validation (can be any integer but within a reasonable range, like 0-15)
        if (!isInteger(gsc.getText())) {
            isValid = false;
            gscError.setText("GSC invalide.");
        } else {
            int gscValue = Integer.parseInt(gsc.getText());
            if (gscValue < 0 || gscValue > 15) {  // Assuming GSC is in the range 0 to 15
                isValid = false;
                gscError.setText("GSC doit être entre 0 et 15.");
            }
        }

// GAD validation (can be any integer, or impose range if needed)
        if (!isInteger(gad.getText())) {
            isValid = false;
            gadError.setText("GAD invalide.");
        } else {
            int gadValue = Integer.parseInt(gad.getText());
            // You can define a reasonable range for GAD, for example, 0 to 30 (just an example)
            if (gadValue < 0 || gadValue > 30) {  // Assuming GAD should be between 0 and 30
                isValid = false;
                gadError.setText("GAD doit être entre 0 et 30.");
            }
        }


        // ECG validation (should be a valid ECG value, customize range as needed)
        if (!isInteger(ecg.getText())) {
            isValid = false;
            ecgError.setText("ECG invalide.");
        }

        // Created_at validation (can't be in the future)
        if (created_at.getValue() == null) {
            isValid = false;
            dateError.setText("Date requise.");
        } else {
            LocalDate today = LocalDate.now();
            if (created_at.getValue().isAfter(today)) {
                isValid = false;
                dateError.setText("La date ne peut pas être dans le futur.");
            }
        }

        return isValid;
    }

    private boolean isInteger(String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
