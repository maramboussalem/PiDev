package tn.esprit.controllers;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.entities.Utilisateur;
import tn.esprit.services.ServiceUtilisateur;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import javafx.stage.FileChooser;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;


import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.util.Random;

public class Adduser implements Initializable {

    @FXML
    private TextField adresseUser;

    @FXML
    private TextField antecedentsUser;

    @FXML
    private TextField captchaUser;

    @FXML
    private PasswordField confirmationUser;

    @FXML
    private TextField emailUser;

    @FXML
    private TextField hopitaleUser;

    @FXML
    private PasswordField motdepasseUser;

    @FXML
    private TextField nomUser;

    @FXML
    private TextField prenomUser;

    @FXML
    private ComboBox<String> roleUser;

    @FXML
    private ComboBox<String> sexeUser;

    @FXML
    private TextField specialiteUser;

    @FXML
    private TextField telephoneUser;

    @FXML
    private Label validationAdresse;

    @FXML
    private Label validationAntecedents;

    @FXML
    private Label validationConfirmation;

    @FXML
    private Label validationEmail;

    @FXML
    private Label validationHopital;

    @FXML
    private Label validationMotdepasse;

    @FXML
    private Label validationNom;

    @FXML
    private Label validationPrenom;

    @FXML
    private Label validationSpecialite;

    @FXML
    private Label validationTelephone;

    @FXML
    private Label validationRole;

    @FXML
    private Label validationSexe;

    @FXML
    private Label validationImage;

    @FXML
    private ImageView captchaImage;

    @FXML
    private Label validationCaptcha;

    private String captchaText;

    private String imagePath = "default.png";

    private ServiceUtilisateur ps = new ServiceUtilisateur();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        sexeUser.setItems(FXCollections.observableArrayList("Femme", "Homme"));
        roleUser.setItems(FXCollections.observableArrayList("patient", "medecin"));
        // Gérer le rôle sélectionné dès l'initialisation
        roleUser.setOnAction(e -> manageRoleFields());
        // Gérer les champs au cas où il y aurait un rôle sélectionné au début
        manageRoleFields();
        generateCaptcha();
    }
    // Méthode pour générer un CAPTCHA
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
            captchaImage.setImage(fxImage);

        } catch (Exception e) {
            e.printStackTrace();
            validationCaptcha.setText("Erreur lors de la génération du CAPTCHA");
        }
    }

    // Générer un texte aléatoire pour le CAPTCHA
    private String generateRandomText(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    // Action pour rafraîchir le CAPTCHA
    @FXML
    private void refreshCaptcha(ActionEvent event) {
        generateCaptcha();
        captchaUser.clear();
        validationCaptcha.setText("");
    }
    private void manageRoleFields() {
        String selectedRole = roleUser.getValue();
        if ("patient".equals(selectedRole)) {
            antecedentsUser.setDisable(false);
            hopitaleUser.setDisable(true);
            specialiteUser.setDisable(true);
            antecedentsUser.setPromptText("Entrez vos antécédents médicaux");
            // Informer l'utilisateur
            validationAntecedents.setText("Champs disponibles pour le patient");
            validationHopital.setText("Champ désactivé pour le patient");
            validationSpecialite.setText("Champ désactivé pour le patient");

        } else if ("medecin".equals(selectedRole)) {
            antecedentsUser.setDisable(true);
            hopitaleUser.setDisable(false);
            specialiteUser.setDisable(false);
            antecedentsUser.setPromptText("Champ non disponible pour un médecin");

            validationAntecedents.setText("Champ désactivé pour le médecin");
            validationHopital.setText("Champs disponibles pour le médecin");
            validationSpecialite.setText("Champs disponibles pour le médecin");

        } else {
            antecedentsUser.setDisable(false);
            hopitaleUser.setDisable(false);
            specialiteUser.setDisable(false);
        }
    }

    @FXML
    void imageUser(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image de profil");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(nomUser.getScene().getWindow());

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
                imagePath = fileName;
                validationImage.setText("Image sélectionnée : " + fileName);
                System.out.println("Image sélectionnée, imagePath mis à jour : " + imagePath);

            } catch (IOException e) {
                validationImage.setText("Erreur lors du chargement de l'image");
                System.err.println("Erreur lors de la copie de l'image : " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Aucune image sélectionnée, imagePath reste : " + imagePath);
        }
    }

    @FXML
    void SinscrireUser(ActionEvent event) {
        if (!validateForm()) return;
        System.out.println("Avant création de l'utilisateur, imagePath = " + imagePath);

        Utilisateur user = createUserFromForm();

        System.out.println("Utilisateur créé : " + user);
        try {
            if (ps.emailExists(user.getEmail())) {
                showAlert("Erreur", "Cet email est déjà utilisé !");
                return;
            }

            ps.ajouter(user);
            showAlert("Succès", "Votre compte a été créé avec succès !");

            // Fermer la fenêtre actuelle
            Stage stage = (Stage) nomUser.getScene().getWindow();
            stage.close();

            // Ouvrir la fenêtre de connexion
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Utilisateur/Login.fxml"));
            Parent root = loader.load();
            Stage loginStage = new Stage();
            loginStage.setScene(new Scene(root));
            loginStage.setTitle("Connexion");
            loginStage.show();

        } catch (IOException e) {
            showAlert("Erreur", "Échec de l'ouverture de la page de connexion !");
            e.printStackTrace();
        } catch (Exception e) {
            showAlert("Erreur", "Échec de l'ajout : " + e.getMessage());
            e.printStackTrace();
        }
    }


    private Utilisateur createUserFromForm() {
        List<String> roles = new ArrayList<>();
        roles.add(roleUser.getValue()); // Ajouter le rôle sélectionné

        return new Utilisateur(
                nomUser.getText(),
                prenomUser.getText(),
                emailUser.getText(),
                sexeUser.getValue(),
                adresseUser.getText(),
                telephoneUser.getText(),
                roleUser.getValue(),
                antecedentsUser.getText(),
                specialiteUser.getText(),
                hopitaleUser.getText(),
                motdepasseUser.getText(), // Le hachage est géré dans setMotdepasse de Utilisateur
                "1234",
                captchaUser.getText(),
                imagePath,
                "activation123",
                true,
                roles
        );
    }

    private boolean validateForm() {
        boolean isValid = true;
        resetValidationLabels();

        isValid &= validateField(nomUser, validationNom, "Nom requis");
        isValid &= validateField(prenomUser, validationPrenom, "Prénom requis");
        isValid &= validateEmail();
        isValid &= validatePhone();
        isValid &= validatePassword();
        isValid &= validateFieldCombo(sexeUser, validationSexe, "Sexe requis");
        isValid &= validateFieldCombo(roleUser, validationRole, "Rôle requis");
        isValid &= validateField(adresseUser, validationAdresse, "Adresse requise");
        isValid &= validateField(telephoneUser, validationTelephone, "Téléphone requis");

        // Champs optionnels (non obligatoires, mais validés si remplis)
        validateField(antecedentsUser, validationAntecedents, "Antécédents invalides");
        validateField(specialiteUser, validationSpecialite, "Spécialité invalide");
        validateField(hopitaleUser, validationHopital, "Hôpital invalide");
        validateField(captchaUser, null, ""); // Pas de validation stricte pour captcha ici

        if (!captchaUser.getText().equals(captchaText)) {
            validationCaptcha.setText("CAPTCHA incorrect");
            isValid = false;
        } else {
            validationCaptcha.setText("");
        }

        if ("default.png".equals(imagePath)) {
            validationImage.setText("Veuillez sélectionner une image");
            isValid = false;
        }
        return isValid;
    }

    private boolean validateField(TextField field, Label errorLabel, String message) {
        if (field.getText().trim().isEmpty() && errorLabel != null) {
            errorLabel.setText(message);
            return false;
        }
        return true;
    }

    private boolean validateFieldCombo(ComboBox<String> combo, Label errorLabel, String message) {
        if (combo.getValue() == null && errorLabel != null) {
            errorLabel.setText(message);
            return false;
        }
        return true;
    }

    private boolean validateEmail() {
        if (!isValidEmail(emailUser.getText())) {
            validationEmail.setText("Email invalide");
            return false;
        }
        return true;
    }

    private boolean validatePhone() {
        if (!isValidPhoneNumber(telephoneUser.getText())) {
            validationTelephone.setText("Numéro invalide (8 chiffres)");
            return false;
        }
        return true;
    }

    private boolean validatePassword() {
        String motdepasse = motdepasseUser.getText();

        // Vérification de la longueur du mot de passe
        if (motdepasse.length() < 8 || motdepasse.length() > 20) {
            validationMotdepasse.setText("Le mot de passe doit avoir entre 8 et 20 caractères.");
            return false;
        }

        // Vérification des critères de sécurité
        if (!motdepasse.matches(".*[a-z].*")) {
            validationMotdepasse.setText("Le mot de passe doit contenir au moins une lettre minuscule.");
            return false;
        }
        if (!motdepasse.matches(".*[A-Z].*")) {
            validationMotdepasse.setText("Le mot de passe doit contenir au moins une lettre majuscule.");
            return false;
        }
        if (!motdepasse.matches(".*[0-9].*")) {
            validationMotdepasse.setText("Le mot de passe doit contenir au moins un chiffre.");
            return false;
        }
        if (!motdepasse.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            validationMotdepasse.setText("Le mot de passe doit contenir au moins un caractère spécial.");
            return false;
        }

        // Vérification de la correspondance des mots de passe
        if (!motdepasse.equals(confirmationUser.getText())) {
            validationConfirmation.setText("Les mots de passe ne correspondent pas.");
            return false;
        }

        return true;
    }


    private void resetValidationLabels() {
        if (validationNom != null) validationNom.setText("");
        if (validationPrenom != null) validationPrenom.setText("");
        if (validationEmail != null) validationEmail.setText("");
        if (validationTelephone != null) validationTelephone.setText("");
        if (validationMotdepasse != null) validationMotdepasse.setText("");
        if (validationConfirmation != null) validationConfirmation.setText("");
        if (validationRole != null) validationRole.setText("");
        if (validationSexe != null) validationSexe.setText("");
        if (validationAdresse != null) validationAdresse.setText("");
        if (validationAntecedents != null) validationAntecedents.setText("");
        if (validationSpecialite != null) validationSpecialite.setText("");
        if (validationHopital != null) validationHopital.setText("");
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearForm() {
        nomUser.clear();
        prenomUser.clear();
        emailUser.clear();
        sexeUser.setValue(null);
        adresseUser.clear();
        telephoneUser.clear();
        roleUser.setValue(null);
        antecedentsUser.clear();
        specialiteUser.clear();
        hopitaleUser.clear();
        motdepasseUser.clear();
        confirmationUser.clear();
        captchaUser.clear();
    }

    private boolean isValidEmail(String email) {
        return Pattern.matches("^[\\w.-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", email);
    }

    private boolean isValidPhoneNumber(String phone) {
        return Pattern.matches("^\\d{8}$", phone);
    }

}