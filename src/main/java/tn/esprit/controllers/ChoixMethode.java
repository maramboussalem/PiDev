package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import tn.esprit.entities.Utilisateur;
import tn.esprit.services.ServiceUtilisateur;
import tn.esprit.utils.TwilioSMS;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

public class ChoixMethode {

    @FXML private Label errorLabel;

    private String email;
    private final ServiceUtilisateur service = new ServiceUtilisateur();
    private static final Logger LOGGER = Logger.getLogger(ChoixMethode.class.getName());

    public void setEmail(String email) {
        this.email = email;
    }

    @FXML
    void email(ActionEvent event) {
        try {
            Utilisateur user = service.getUserByEmail(email);
            if (user == null) {
                errorLabel.setText("Utilisateur non trouvé pour cet email.");
                return;
            }
            String code = TwilioSMS.generateVerificationCode();
            user.setVerification_code(code);
            service.modifier(user);

            LOGGER.info("Envoi de l'email à : " + user.getEmail() + " avec le code : " + code);
            sendVerificationEmail(user, code);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Utilisateur/VerificationCode.fxml"));
            Parent root = loader.load();

            VerificationCode controller = loader.getController();
            controller.setEmail(email);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (MessagingException e) {
            errorLabel.setText("Erreur lors de l'envoi de l'email.");
            LOGGER.severe("Erreur d'envoi d'email : " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            errorLabel.setText("Une erreur s'est produite.");
            LOGGER.severe("Erreur générale : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void sms(ActionEvent event) {
        try {
            java.net.URL fxmlLocation = getClass().getResource("/Utilisateur/EnvoiCodeVérification.fxml");
            if (fxmlLocation == null) {
                errorLabel.setText("Erreur : Fichier FXML EnvoiCodeVerification.fxml introuvable.");
                LOGGER.severe("Fichier FXML /Utilisateur/EnvoiCodeVerification.fxml introuvable.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            EnvoiCodeVérification controller = loader.getController();
            controller.setEmail(email);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            errorLabel.setText("Erreur lors du chargement de la page.");
            LOGGER.severe("Erreur de chargement FXML : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendVerificationEmail(Utilisateur utilisateur, String code) throws MessagingException {
        String host = "smtp.gmail.com";
        String port = "587";
        String mailFrom = "boussalem18faouzi@gmail.com";
        String password = "hxhkiarhhbiytexv";

        String mailTo = utilisateur.getEmail();

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mailFrom, password);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(mailFrom));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mailTo));
        message.setSubject("Code de vérification pour réinitialisation du mot de passe");

        String htmlContent = "<!DOCTYPE html>" +
                "<html lang='fr'>" +
                "<head>" +
                "  <meta charset='UTF-8'>" +
                "  <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "  <style>" +
                "    body { margin: 0; padding: 20px; background-color: #f2f2f2; font-family: Arial, sans-serif; }" +
                "    .container { background-color: #ffffff; margin: 0 auto; padding: 30px 20px; border-radius: 12px; max-width: 600px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); }" +
                "    .header { background-color: #003366; color: white; text-align: center; padding: 30px 20px; border-top-left-radius: 12px; border-top-right-radius: 12px; }" +
                "    .header h1 { margin: 0; font-size: 24px; }" +
                "    .content { padding: 20px; text-align: left; color: #333333; font-size: 16px; line-height: 1.6; }" +
                "    .button { display: inline-block; background-color: #007acc; color: white; padding: 12px 24px; margin: 20px 0; text-decoration: none; font-weight: bold; border-radius: 6px; font-size: 18px; }" +
                "    .footer { font-size: 13px; color: #777777; text-align: center; margin-top: 30px; }" +
                "    .separator { border-top: 2px solid #007acc; margin: 30px 0; }" +
                "    a { color: #007acc; text-decoration: none; }" +
                "  </style>" +
                "</head>" +
                "<body>" +
                "  <div class='container'>" +
                "    <div class='header'>" +
                "      <h1>Vérification du compte</h1>" +
                "    </div>" +
                "    <div class='content'>" +
                "      <p>Bonjour <strong>" + utilisateur.getPrenom() + " " + utilisateur.getNom() + "</strong>,</p>" +
                "      <p>Voici votre code de vérification pour réinitialiser votre mot de passe :</p>" +
                "      <div style='text-align:center;'>" +
                "        <div class='button'>" + code + "</div>" +
                "      </div>" +
                "      <p>Si vous n'avez pas demandé cette vérification, veuillez ignorer cet e-mail.</p>" +
                "      <div class='separator'></div>" +
                "      <p>Pour toute question, contactez-nous à : <a href='mailto:admin@BrightMind.com'>admin@BrightMind.com</a></p>" +
                "    </div>" +
                "    <div class='footer'>" +
                "      <p>© 2025 Bright Mind. Tous droits réservés.</p>" +
                "    </div>" +
                "  </div>" +
                "</body>" +
                "</html>";

        message.setContent(htmlContent, "text/html; charset=UTF-8");

        LOGGER.info("Tentative d'envoi de l'email à : " + mailTo);
        Transport.send(message);
        LOGGER.info("Email envoyé avec succès à : " + mailTo);
    }

    @FXML
    void login(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Utilisateur/login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}