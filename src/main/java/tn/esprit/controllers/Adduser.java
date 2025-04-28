package tn.esprit.controllers;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
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
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class Adduser implements Initializable {

    @FXML
    private TextField captchaUser;

    @FXML
    private PasswordField confirmationUser;

    @FXML
    private TextField emailUser;

    @FXML
    private PasswordField motdepasseUser;

    @FXML
    private TextField nomUser;

    @FXML
    private TextField prenomUser;

    @FXML
    private ComboBox<String> roleUser;

    @FXML
    private TextField telephoneUser;

    @FXML
    private Label validationConfirmation;

    @FXML
    private Label validationEmail;

    @FXML
    private Label validationMotdepasse;

    @FXML
    private Label validationNom;

    @FXML
    private Label validationPrenom;

    @FXML
    private Label validationTelephone;

    @FXML
    private Label validationRole;

    @FXML
    private ImageView captchaImage;

    @FXML
    private Label validationCaptcha;

    private String captchaText;

    private String imagePath = "default.jpg";

    private ServiceUtilisateur ps = new ServiceUtilisateur();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        roleUser.setItems(FXCollections.observableArrayList("patient", "medecin"));
        generateCaptcha();
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

            // Ajouter du bruit :points aléatoires
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

    @FXML
    private void refreshCaptcha(ActionEvent event) {
        generateCaptcha();
        captchaUser.clear();
        validationCaptcha.setText("");
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
            if (user.getRole().equals("medecin")) {
                // Modification : Envoyer un e-mail au médecin pour indiquer que son compte est désactivé
                sendDeactivationEmail(user);
                showAlert("Succès", "Compte médecin créé avec succès, mais actuellement désactivé. Un e-mail a été envoyé pour vous informer. Veuillez patienter jusqu'à ce qu'un administrateur procède à son activation.");
            } else {
                showAlert("Succès", "Votre compte a été créé avec succès !");
            }
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


    // Nouvelle méthode : Envoyer un e-mail de désactivation au médecin
    private void sendDeactivationEmail(Utilisateur utilisateur) {
        // Paramètres du serveur SMTP (identiques à ceux de ListeUser)
        String host = "smtp.gmail.com";
        String port = "587";
        String mailFrom = "boussalem18faouzi@gmail.com"; // Votre adresse e-mail
        String password = "hxhkiarhhbiytexv"; // Votre mot de passe d'application Gmail

        // Adresse e-mail du médecin
        String mailTo = utilisateur.getEmail();

        // Configurer les propriétés du serveur SMTP
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);

        // Créer une session avec authentification
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mailFrom, password);
            }
        });

        try {
            // Créer un message MIME
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(mailFrom));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(mailTo));
            message.setSubject("Votre compte médecin a été créé mais est désactivé");

            // Corps du message HTML
            String htmlContent = "<!DOCTYPE html>" +
                    "<html lang='fr'>" +
                    "<head>" +
                    "  <meta charset='UTF-8'>" +
                    "  <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                    "  <style>" +
                    "    body { margin: 0; padding: 20px; background-color: #f2f2f2; font-family: Arial, sans-serif; }" +
                    "    .container { background-color: #ffffff; margin: 0 auto; padding: 30px 20px; border-radius: 12px; max-width: 600px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); }" +
                    "    .header { background-color: #003366; color: white; text-align: center; padding: 30px 20px; border-top-left-radius: 12px; border-top-right-radius: 12px; }" +
                    "    .header img { max-width: 80px; margin-bottom: 10px; }" +
                    "    .header h1 { margin: 10px 0 0 0; font-size: 24px; }" +
                    "    .content { padding: 20px; text-align: left; color: #333333; font-size: 16px; line-height: 1.6; }" +
                    "    .button { display: inline-block; background-color: #007acc; color: white; padding: 12px 24px; margin: 20px 0; text-decoration: none; font-weight: bold; border-radius: 6px; }" +
                    "    .footer { font-size: 13px; color: #777777; text-align: center; margin-top: 30px; }" +
                    "    .separator { border-top: 2px solid #007acc; margin: 30px 0; }" +
                    "    a { color: #007acc; text-decoration: none; }" +
                    "  </style>" +
                    "</head>" +
                    "<body>" +
                    "  <div class='container'>" +
                    "    <div class='header'>" +
                    "      <h1>Merci pour votre inscription</h1>" +
                    "    </div>" +
                    "    <div class='content'>" +
                    "      <p>Bonjour <strong>" + utilisateur.getPrenom() + " " + utilisateur.getNom() + "</strong>,</p>" +
                    "      <p>Votre compte médecin a été <strong>créé avec succès</strong> mais est actuellement <strong>désactivé</strong>.</p>" +
                    "      <div style='text-align:center;'>" +
                    "        <a href='#' class='button'>Statut : Désactivé</a>" +
                    "      </div>" +
                    "      <p>Veuillez patienter jusqu'à ce qu'un administrateur active votre compte.</p>" +
                    "      <div class='separator'></div>" +
                    "      <p>Pour toute question, contactez-nous à : <a href='mailto:admin@BrightMind.com'>admin@BrightMind.com</a></p>" +
                    "    </div>" +
                    "    <div class='footer'>" +
                    "      <p>© 2025 Bright Mind. Tous droits réservés.</p>" +
                    "    </div>" +
                    "  </div>" +
                    "</body>" +
                    "</html>";
            // Spécifier que le contenu est en HTML
            message.setContent(htmlContent, "text/html; charset=utf-8");

            // Envoyer le message
            Transport.send(message);
            System.out.println("E-mail de désactivation envoyé à : " + mailTo);

        } catch (MessagingException e) {
            System.err.println("Erreur lors de l'envoi de l'e-mail : " + e.getMessage());
            showAlert("Erreur", "Échec de l'envoi de l'e-mail de désactivation : " + e.getMessage());
        }
    }


    private Utilisateur createUserFromForm() {
        List<String> roles = new ArrayList<>();
        roles.add(roleUser.getValue()); // Ajouter le rôle sélectionné

        boolean isActive = !roleUser.getValue().equals("medecin");

        return new Utilisateur(
                nomUser.getText(),
                prenomUser.getText(),
                emailUser.getText(),
                "Non Rempli",
                "Non Rempli",
                telephoneUser.getText(),
                roleUser.getValue(),
                "Non Rempli",
                "Non Rempli",
                "Non Rempli",
                motdepasseUser.getText(), // Le hachage est géré dans setMotdepasse de Utilisateur
                "1234",
                captchaUser.getText(),
                "default.jpg",
                "activation123",
                isActive,
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
        isValid &= validateFieldCombo(roleUser, validationRole, "Rôle requis");
        isValid &= validateField(telephoneUser, validationTelephone, "Téléphone requis");
        validateField(captchaUser, null, "");
        if (!captchaUser.getText().equals(captchaText)) {
            validationCaptcha.setText("CAPTCHA incorrect");
            isValid = false;
        } else {
            validationCaptcha.setText("");
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
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean isValidEmail(String email) {
        return Pattern.matches("^[\\w.-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", email);
    }

    private boolean isValidPhoneNumber(String phone) {
        return Pattern.matches("^\\d{8}$", phone);
    }

    @FXML
    void login(ActionEvent event) {
        try {
            // Chargement du fichier login.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Utilisateur/login.fxml"));
            Parent root = loader.load();

            // Récupérer la scène actuelle et changer son contenu
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}