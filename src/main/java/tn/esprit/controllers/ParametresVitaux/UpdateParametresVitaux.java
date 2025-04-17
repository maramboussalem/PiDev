package tn.esprit.controllers.ParametresVitaux;

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
import tn.esprit.entities.ParametresVitaux;
import tn.esprit.services.ParametresVitauxService;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ResourceBundle;

public class UpdateParametresVitaux implements Initializable {


    @FXML
    private AnchorPane contentArea;

    @FXML
    private DatePicker created_at;

    @FXML
    private Label dateError;

    @FXML
    private TextField ecg;

    @FXML
    private Label ecgError;

    @FXML
    private TextField fc;

    @FXML
    private Label fcError;

    @FXML
    private TextField fr;

    @FXML
    private Label frError;

    @FXML
    private TextField gad;

    @FXML
    private Label gadError;

    @FXML
    private TextField gsc;

    @FXML
    private Label gscError;

    @FXML
    private ImageView image;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadImage();
    }

    private void loadImage() {
        URL imageUrl = getClass().getResource("/images/updateParametres.jpg");
        if (imageUrl != null) {
            image.setImage(new Image(imageUrl.toExternalForm()));
        } else {
            System.out.println("Fichier image introuvable dans le classpath !");
            URL defaultImg = getClass().getResource("/images/default.png");
            if (defaultImg != null) {
                image.setImage(new Image(defaultImg.toExternalForm()));
                System.out.println("Image par défaut chargée avec succès");
            }
        }

    }

    @FXML
    private TextField name;

    @FXML
    private Label nameError;

    @FXML
    private TextField spo2;

    @FXML
    private Label spo2Error;

    @FXML
    private TextField tad;

    @FXML
    private Label tadError;

    @FXML
    private TextField tas;

    @FXML
    private Label tasError;

    private ParametresVitaux selectedParametres;
    private final ParametresVitauxService pvService = new ParametresVitauxService();

    // Called from Index or Liste screen to initialize values
    public void initialize(ParametresVitaux pv) {
        this.selectedParametres = pv;

        name.setText(pv.getName());
        fc.setText(String.valueOf(pv.getFc()));
        fr.setText(String.valueOf(pv.getFr()));
        tas.setText(String.valueOf(pv.getTas()));
        tad.setText(String.valueOf(pv.getTad()));
        gad.setText(String.valueOf(pv.getGad()));
        ecg.setText(String.valueOf(pv.getEcg()));
        spo2.setText(String.valueOf(pv.getSpo2()));
        gsc.setText(String.valueOf(pv.getGsc()));
        created_at.setValue(pv.getCreated_at().toLocalDateTime().toLocalDate());

    }

    @FXML
    void saveButton(ActionEvent event) {
        if (validateForm()) {
            try {
                selectedParametres.setName(name.getText());
                selectedParametres.setFc((int) Double.parseDouble(fc.getText()));
                selectedParametres.setFr((int) Double.parseDouble(fr.getText()));
                selectedParametres.setTas((int) Double.parseDouble(tas.getText()));
                selectedParametres.setTad((int) Double.parseDouble(tad.getText()));
                selectedParametres.setGad((float) Double.parseDouble(gad.getText()));
                selectedParametres.setEcg(ecg.getText());
                selectedParametres.setSpo2((int) Double.parseDouble(spo2.getText()));
                selectedParametres.setGsc((int) Double.parseDouble(gsc.getText()));
                selectedParametres.setCreated_at(Timestamp.valueOf(created_at.getValue().atStartOfDay()));


                pvService.modifier(selectedParametres);

                System.out.println("Paramètres vitaux mis à jour avec succès !");
            } catch (SQLException e) {
                System.err.println("Erreur lors de la mise à jour : " + e.getMessage());
            }
        }
    }

    @FXML
    void historiqueButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ParametresVitaux/IndexParametresVitaux.fxml"));
            Parent root = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void closeButton(ActionEvent event) {
        Button button = (Button) event.getSource();

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web("#4f9efc"));
        shadow.setRadius(10);
        shadow.setSpread(0.5);
        button.setEffect(shadow);

        ScaleTransition scale = new ScaleTransition(Duration.millis(100), button);
        scale.setFromX(1.0);
        scale.setFromY(1.0);
        scale.setToX(0.97);
        scale.setToY(0.97);
        scale.setAutoReverse(true);
        scale.setCycleCount(2);

        scale.setOnFinished(e -> {
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

    private boolean validateForm() {
        boolean isValid = true;

        if (name.getText().isEmpty()) {
            nameError.setText("Nom requis");
            isValid = false;
        } else {
            nameError.setText("");
        }

        if (created_at.getValue() == null) {
            dateError.setText("Date requise");
            isValid = false;
        } else {
            dateError.setText("");
        }

        isValid &= validateNumericField(fc, fcError, "FC invalide");
        isValid &= validateNumericField(fr, frError, "FR invalide");
        isValid &= validateNumericField(tas, tasError, "TAS invalide");
        isValid &= validateNumericField(tad, tadError, "TAD invalide");
        isValid &= validateNumericField(gad, gadError, "GAD invalide");
        isValid &= validateNumericField(ecg, ecgError, "ECG invalide");
        isValid &= validateNumericField(spo2, spo2Error, "SpO2 invalide");
        isValid &= validateNumericField(gsc, gscError, "GSC invalide");

        return isValid;
    }

    private boolean validateNumericField(TextField field, Label errorLabel, String errorMessage) {
        try {
            Double.parseDouble(field.getText());
            errorLabel.setText("");
            return true;
        } catch (NumberFormatException e) {
            errorLabel.setText(errorMessage);
            return false;
        }
    }

}
