package tn.esprit.controllers;

import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.entities.Utilisateur;
import tn.esprit.services.ServiceUtilisateur;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.util.Random;

public class updateuser implements Initializable {

    @FXML
    private TextField adresseUserM;

    @FXML
    private TextField antecedentsUserM;

    @FXML
    private TextField captchaUserM;

    @FXML
    private PasswordField confirmationUserM;

    @FXML
    private TextField emailUserM;

    @FXML
    private TextField hopitaleUserM;

    @FXML
    private PasswordField motdepasseUserM;

    @FXML
    private TextField nomUserM;

    @FXML
    private TextField prenomUserM;

    @FXML
    private ComboBox<String> roleUserM;

    @FXML
    private ComboBox<String> sexeUserM;

    @FXML
    private TextField specialiteUserM;

    @FXML
    private TextField telephoneUserM;

    @FXML
    private Label validationAdresseM;

    @FXML
    private Label validationAntecedents;

    @FXML
    private Label validationConfirmationM;

    @FXML
    private Label validationEmailM;

    @FXML
    private Label validationHopital;

    @FXML
    private Label validationMotdepasseM;

    @FXML
    private Label validationNomM;

    @FXML
    private Label validationPrenomM;

    @FXML
    private Label validationSpecialite;

    @FXML
    private Label validationTelephoneM;

    @FXML
    private Label validationImageM;

    @FXML
    private ImageView captchaImageM;

    @FXML
    private Label validationCaptchaM;

    private String captchaText;

    private Utilisateur utilisateur;
    private ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();
    private DetailsUser detailsUserController;
    private String imagePath;

    public void setDetailsUserController(DetailsUser detailsUserController) {
        this.detailsUserController = detailsUserController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        roleUserM.getItems().addAll("patient", "medecin");
        sexeUserM.getItems().addAll("Homme", "Femme");

        roleUserM.valueProperty().addListener((observable, oldValue, newValue) -> {
            mettreAJourChampsRole(newValue);
        });

        if (utilisateur != null && utilisateur.getImg_url() != null) {
            imagePath = utilisateur.getImg_url();
        }

        if (utilisateur != null && utilisateur.getRole() != null) {
            mettreAJourChampsRole(utilisateur.getRole());
        }
        generateCaptcha();
    }

    // Méthode pour passer l'utilisateur dans ce contrôleur
    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
        this.imagePath = utilisateur.getImg_url();
        if (utilisateur.getMotdepasse() == null) {
            System.err.println("Warning: The utilisateur object's motdepasse is null!");
        }
        remplirChamps();
    }

    private void remplirChamps() {
        if (utilisateur != null) {
            nomUserM.setText(utilisateur.getNom());
            prenomUserM.setText(utilisateur.getPrenom());
            emailUserM.setText(utilisateur.getEmail());
            adresseUserM.setText(utilisateur.getAdresse());
            telephoneUserM.setText(utilisateur.getTelephone());
            roleUserM.setValue(utilisateur.getRole());
            sexeUserM.setValue(utilisateur.getSexe());
            antecedentsUserM.setText(utilisateur.getAntecedents_medicaux());
            specialiteUserM.setText(utilisateur.getSpecialite());
            hopitaleUserM.setText(utilisateur.getHopital());
            // Gestion de l'image : vérifier si imagePath est null
            if (imagePath == null) {
                validationImageM.setText("Aucune image sélectionnée");
            } else {
                validationImageM.setText(imagePath.equals("default.png") ? "Aucune image sélectionnée" : "Image actuelle : " + imagePath);
            }
        }
    }

    private void generateCaptcha() {
        try {
            // Générer un texte aléatoire
            captchaText = generateRandomText(6);

            // Créer une image avec le texte
            BufferedImage bufferedImage = new BufferedImage(150, 50, BufferedImage.TYPE_INT_RGB);
            java.awt.Graphics2D g2d = bufferedImage.createGraphics();

            // Fond blanc
            g2d.setColor(java.awt.Color.WHITE);
            g2d.fillRect(0, 0, 150, 50);

            // Ajouter du bruit
            Random random = new Random();
            for (int i = 0; i < 50; i++) {
                int x = random.nextInt(150);
                int y = random.nextInt(50);
                g2d.setColor(new java.awt.Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
                g2d.fillRect(x, y, 1, 1);
            }

            // Dessiner le texte
            g2d.setColor(java.awt.Color.BLACK);
            g2d.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 20));
            g2d.drawString(captchaText, 20, 35);

            // Ajouter des lignes de distorsion
            g2d.setColor(java.awt.Color.GRAY);
            for (int i = 0; i < 3; i++) {
                int x1 = random.nextInt(150);
                int y1 = random.nextInt(50);
                int x2 = random.nextInt(150);
                int y2 = random.nextInt(50);
                g2d.drawLine(x1, y1, x2, y2);
            }

            g2d.dispose();

            // Convertir en Image JavaFX
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            Image fxImage = new Image(bais);
            captchaImageM.setImage(fxImage);

        } catch (Exception e) {
            e.printStackTrace();
            validationCaptchaM.setText("Erreur lors de la génération du CAPTCHA");
        }
    }

    private String generateRandomText(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    @FXML
    private void refreshCaptchaM(ActionEvent event) {
        generateCaptcha();
        captchaUserM.clear();
        validationCaptchaM.setText("");
    }

    private void mettreAJourChampsRole(String role) {
        if (role != null) {
            switch (role) {
                case "patient":
                    antecedentsUserM.setDisable(false);
                    antecedentsUserM.setText("");
                    specialiteUserM.setDisable(true);
                    specialiteUserM.setText("");
                    hopitaleUserM.setDisable(true);
                    hopitaleUserM.setText("");
                    break;

                case "medecin":
                    antecedentsUserM.setDisable(true);
                    antecedentsUserM.setText("");
                    specialiteUserM.setDisable(false);
                    hopitaleUserM.setDisable(false);
                    break;

                default:
                    antecedentsUserM.setDisable(true);
                    antecedentsUserM.setText("");
                    specialiteUserM.setDisable(true);
                    specialiteUserM.setText("");
                    hopitaleUserM.setDisable(true);
                    hopitaleUserM.setText("");
                    break;
            }
        }
    }

    @FXML
    public void imageUserM(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image de profil");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(nomUserM.getScene().getWindow());

        if (selectedFile != null) {
            try {
                String destinationDir = "src/main/resources/images/profiles/";
                File dir = new File(destinationDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                String fileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                File destinationFile = new File(destinationDir + fileName);

                Files.copy(selectedFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                imagePath = fileName; // Mettre à jour imagePath uniquement si une nouvelle image est sélectionnée
                validationImageM.setText("Image sélectionnée : " + fileName);
                System.out.println("Image mise à jour : " + imagePath);
            } catch (IOException e) {
                validationImageM.setText("Erreur lors du chargement de l'image");
                System.err.println("Erreur lors de la copie de l'image : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    // Méthode pour mettre à jour l'utilisateur
    @FXML
    void MettreAjourUser(ActionEvent event) {
        if (!validerChamps()) {
            return;
        }
        utilisateur.setNom(nomUserM.getText());
        utilisateur.setPrenom(prenomUserM.getText());
        utilisateur.setEmail(emailUserM.getText());
        utilisateur.setAdresse(adresseUserM.getText());
        utilisateur.setTelephone(telephoneUserM.getText());
        utilisateur.setRole(roleUserM.getValue());
        utilisateur.setSexe(sexeUserM.getValue());
        utilisateur.setAntecedents_medicaux(antecedentsUserM.getText());
        utilisateur.setSpecialite(specialiteUserM.getText());
        utilisateur.setHopital(hopitaleUserM.getText());
        // Gestion de l'image : utiliser imagePath si modifié, sinon conserver l'image actuelle
        if (imagePath != null) {
            utilisateur.setImg_url(imagePath);
        }
        // Mettre à jour la liste des roles
        List<String> rolesList = new ArrayList<>();
        rolesList.add(roleUserM.getValue());
        utilisateur.setRoles(rolesList);

        if (!motdepasseUserM.getText().isEmpty()) {
            if (!motdepasseUserM.getText().equals(confirmationUserM.getText())) {
                validationConfirmationM.setText("Les mots de passe ne correspondent pas.");
                return;
            }
            utilisateur.setMotdepasse(hashPassword(motdepasseUserM.getText()));
        } else {
            utilisateur.setMotdepasse(utilisateur.getMotdepasse());
        }
        try {
            serviceUtilisateur.modifier(utilisateur);
            showAlert("Succès", "Les informations de votre compte ont été modifiées avec succès.");

            // Actualiser les détails dans DetailsUser si le contrôleur est disponible
            if (detailsUserController != null) {
                detailsUserController.refreshDetails();
            }
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de la mise à jour de l'utilisateur : " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean validerChamps() {
        boolean isValid = true;
        if (nomUserM.getText().isEmpty()) {
            validationNomM.setText("Le nom est requis.");
            isValid = false;
        } else {
            validationNomM.setText("");
        }
        if (prenomUserM.getText().isEmpty()) {
            validationPrenomM.setText("Le prénom est requis.");
            isValid = false;
        } else {
            validationPrenomM.setText("");
        }
        if (emailUserM.getText().isEmpty()) {
            validationEmailM.setText("L'email est requis.");
            isValid = false;
        } else if (!emailUserM.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            validationEmailM.setText("Email invalide.");
            isValid = false;
        } else {
            validationEmailM.setText("");
        }
        if (!motdepasseUserM.getText().isEmpty()) {  // On vérifie uniquement si l'utilisateur a saisi un nouveau mot de passe
            String password = motdepasseUserM.getText();
            StringBuilder errorMessage = new StringBuilder();
            if (password.length() < 8) {
                errorMessage.append("Le mot de passe doit contenir au moins 8 caractères.\n");
                isValid = false;
            }
            if (!password.matches(".*[a-z].*")) {
                errorMessage.append("Le mot de passe doit contenir au moins une lettre minuscule.\n");
                isValid = false;
            }
            if (!password.matches(".*[A-Z].*")) {
                errorMessage.append("Le mot de passe doit contenir au moins une lettre majuscule.\n");
                isValid = false;
            }
            if (!password.matches(".*\\d.*")) {
                errorMessage.append("Le mot de passe doit contenir au moins un chiffre.\n");
                isValid = false;
            }
            if (!password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
                errorMessage.append("Le mot de passe doit contenir au moins un caractère spécial .\n");
                isValid = false;
            }
            // Affichage du message d'erreur combiné
            if (errorMessage.length() > 0) {
                validationMotdepasseM.setText(errorMessage.toString().trim());
            } else {
                validationMotdepasseM.setText("");
            }
        } else {
            validationMotdepasseM.setText(""); // Pas de message si le champ est vide (on conserve l'ancien mot de passe)
        }

        if (adresseUserM.getText().isEmpty()) {
            validationAdresseM.setText("L'adresse est requise.");
            isValid = false;
        } else {
            validationAdresseM.setText("");
        }
        if (telephoneUserM.getText().isEmpty()) {
            validationTelephoneM.setText("Le numéro de téléphone est requis.");
            isValid = false;
        } else {
            validationTelephoneM.setText("");
        }
        if (roleUserM.getValue() != null && roleUserM.getValue().equals("patient") && antecedentsUserM.getText().isEmpty()) {
            validationAntecedents.setText("Les antécédents médicaux sont requis pour un patient.");
            isValid = false;
        } else {
            validationAntecedents.setText("");
        }
        if (roleUserM.getValue() != null && roleUserM.getValue().equals("medecin") && specialiteUserM.getText().isEmpty()) {
            validationSpecialite.setText("La spécialité est requise pour un médecin.");
            isValid = false;
        } else {
            validationSpecialite.setText("");
        }
        if (roleUserM.getValue() != null && roleUserM.getValue().equals("medecin") && hopitaleUserM.getText().isEmpty()) {
            validationHopital.setText("L'hôpital est requis pour un médecin.");
            isValid = false;
        } else {
            validationHopital.setText("");
        }

        if (!captchaUserM.getText().equals(captchaText)) {
            validationCaptchaM.setText("CAPTCHA incorrect");
            isValid = false;
        } else {
            validationCaptchaM.setText("");
        }
        return isValid;
    }

    private String hashPassword(String password) {
        return org.mindrot.jbcrypt.BCrypt.hashpw(password, org.mindrot.jbcrypt.BCrypt.gensalt());
    }
}
