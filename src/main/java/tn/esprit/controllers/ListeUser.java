package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import tn.esprit.services.ServiceUtilisateur;
import tn.esprit.entities.Utilisateur;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.VBox;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class ListeUser implements Initializable {

    @FXML
    private TextField searchField;

    @FXML
    private ListView<Utilisateur> listeUser;

    @FXML
    private ComboBox<String> sortComboBox;

    @FXML
    private Label adresse;

    @FXML
    private Label email;

    @FXML
    private ImageView image;

    @FXML
    private Label nom;

    @FXML
    private Label prenom;

    @FXML
    private Label role;

    @FXML
    private Label sexe;

    @FXML
    private Label tel;

    @FXML
    private Button btnToggleStatus; // Le bouton global
    @FXML
    private Label statutLabel; // Le label qui affiche le statut de l'utilisateur



    private Utilisateur utilisateurSelectionne;

    private ServiceUtilisateur serviceUtilisateur;

    public ListeUser() {
        serviceUtilisateur = new ServiceUtilisateur();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        afficherUtilisateurs();

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrerUtilisateurs(newValue);
        });

        sortComboBox.getItems().addAll("Nom", "Prénom", "Email");
        sortComboBox.setOnAction(event -> trierUtilisateurs(sortComboBox.getValue()));

    }
    @FXML
    private void btnToggleStatus(ActionEvent event) {
        Utilisateur selectedUser = listeUser.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            try {
                boolean newStatus = !selectedUser.isIs_active(); // Inverser le statut
                serviceUtilisateur.updateStatus(selectedUser.getId(), newStatus); // Mettre à jour dans la base de données
                selectedUser.setIs_active(newStatus); // Mettre à jour localement l'objet sélectionné

                // Si le compte est désactivé, envoyer un e-mail
                if (!newStatus) {
                    sendDeactivationEmail(selectedUser);
                }

                // Rafraîchir l'affichage des utilisateurs
                afficherUtilisateurs();

                // Mettre à jour l'affichage du bouton et du label
                updateStatusLabel();
            } catch (SQLException e) {
                System.err.println("Erreur lors de la mise à jour du statut : " + e.getMessage());
            }
        }
    }
    private void filtrerUtilisateurs(String critere) {
        try {
            List<Utilisateur> utilisateurs = serviceUtilisateur.afficher();
            ObservableList<Utilisateur> utilisateursFiltres = FXCollections.observableArrayList();

            for (Utilisateur utilisateur : utilisateurs) {
                if (utilisateur.getNom().toLowerCase().contains(critere.toLowerCase()) ||
                        utilisateur.getPrenom().toLowerCase().contains(critere.toLowerCase()) ||
                        utilisateur.getEmail().toLowerCase().contains(critere.toLowerCase())  ||
                        utilisateur.getRole().toLowerCase().contains(critere.toLowerCase())) {
                    utilisateursFiltres.add(utilisateur);
                }
            }
            listeUser.setItems(utilisateursFiltres);
        } catch (SQLException e) {
            System.err.println("Erreur lors du filtrage des utilisateurs : " + e.getMessage());
        }
    }

    private void trierUtilisateurs(String critere) {
        try {
            List<Utilisateur> utilisateurs = serviceUtilisateur.afficher();
            ObservableList<Utilisateur> utilisateursObservable = FXCollections.observableArrayList(utilisateurs);

            utilisateursObservable.sort((u1, u2) -> {
                switch (critere) {
                    case "Nom":
                        return u1.getNom().compareToIgnoreCase(u2.getNom());
                    case "Prénom":
                        return u1.getPrenom().compareToIgnoreCase(u2.getPrenom());
                    case "Email":
                        return u1.getEmail().compareToIgnoreCase(u2.getEmail());
                    default:
                        return 0;
                }
            });
            listeUser.setItems(utilisateursObservable);
        } catch (SQLException e) {
            System.err.println("Erreur lors du tri des utilisateurs : " + e.getMessage());
        }
    }

    private void afficherUtilisateurs() {
        try {
            List<Utilisateur> utilisateurs = serviceUtilisateur.afficher();
            ObservableList<Utilisateur> utilisateursObservable = FXCollections.observableArrayList(utilisateurs);
            listeUser.setItems(utilisateursObservable);

            listeUser.setCellFactory(param -> new ListCell<Utilisateur>() {
                @Override
                protected void updateItem(Utilisateur utilisateur, boolean empty) {
                    super.updateItem(utilisateur, empty);
                    if (utilisateur != null && !empty) {
                        // Image de profil
                        ImageView imageView = new ImageView();
                        String cheminImage = utilisateur.getImg_url();
                        URL imageUrl = getClass().getResource("/images/profiles/" + cheminImage);

                        if (imageUrl != null) {
                            imageView.setImage(new Image(imageUrl.toExternalForm()));
                        } else {
                            imageView.setImage(new Image("file:src/main/resources/images/default.jpg"));
                        }
                        imageView.setFitHeight(120); // Réduire la taille pour un look plus compact
                        imageView.setFitWidth(120);
                        imageView.setPreserveRatio(true);
                        imageView.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 2, 2);"); // Ombre

                        // Indicateur de rôle (icône ou texte stylé)
                        Label roleIndicator = new Label(utilisateur.getRole());
                        roleIndicator.setStyle(
                                "-fx-font-size: 12px; " +
                                        "-fx-padding: 2 8 2 8; " +
                                        "-fx-background-radius: 12; " +
                                        (utilisateur.getRole().equalsIgnoreCase("medecin") ?
                                                "-fx-background-color: #ff8800; -fx-text-fill: white;" :
                                                "-fx-background-color: #2196F3; -fx-text-fill: white;")
                        );

                        // Statut avec couleur
                        Label statutLabel = new Label(utilisateur.isIs_active() ? "Actif" : "Désactivé");
                        statutLabel.setStyle(
                                "-fx-font-size: 12px; " +
                                        "-fx-padding: 2 8 2 8; " +
                                        "-fx-background-radius: 12; " +
                                        (utilisateur.isIs_active() ?
                                                "-fx-background-color: #28a745; -fx-text-fill: white;" :
                                                "-fx-background-color: #dc3545; -fx-text-fill: white;")
                        );

                        // Inside listeUser.setCellFactory
                        Label nomLabel = new Label("Nom: " + utilisateur.getNom());
                        nomLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                        Label prenomLabel = new Label("Prénom: " + utilisateur.getPrenom());
                        Label emailLabel = new Label("Email: " + utilisateur.getEmail());
                        Label telephoneLabel = new Label("Téléphone: " + utilisateur.getTelephone());
                        Label adresseLabel = new Label("Adresse: " + utilisateur.getAdresse());
                        Label sexeLabel = new Label("Sexe: " + utilisateur.getSexe());
                        HBox roleBox = new HBox(5, new Label("Rôle: "), roleIndicator);
                        HBox statutBox = new HBox(5, new Label("Statut: "), statutLabel);

                        VBox infoBox = new VBox(
                                nomLabel,
                                prenomLabel,
                                emailLabel,
                                telephoneLabel,
                                adresseLabel,
                                sexeLabel,
                                roleBox,
                                statutBox
                        );
                        infoBox.setSpacing(4);
                        infoBox.setStyle("-fx-padding: 5;");
                        infoBox.setSpacing(4);
                        infoBox.setStyle("-fx-padding: 5;");

                        // Tooltip pour les informations longues
                        Tooltip tooltip = new Tooltip(
                                "Nom: " + utilisateur.getNom() + "\n" +
                                        "Prénom: " + utilisateur.getPrenom() + "\n" +
                                        "Email: " + utilisateur.getEmail() + "\n" +
                                        "Adresse: " + utilisateur.getAdresse()
                        );
                        setTooltip(tooltip);

                        // Action de sélection
                        setOnMouseClicked(event -> {
                            utilisateurSelectionne = utilisateur;
                            updateStatusLabel();
                        });

                        // Conteneur principal
                        HBox hbox = new HBox(15, imageView, infoBox);
                        hbox.setStyle(
                                "-fx-padding: 10; " +
                                        "-fx-background-color: #ffffff; " +
                                        "-fx-background-radius: 10; " +
                                        "-fx-border-color: #e0e0e0; " +
                                        "-fx-border-radius: 10; " +
                                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
                        );
                        hbox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

                        setGraphic(hbox);
                    } else {
                        setGraphic(null);
                    }
                }
            });

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'affichage des utilisateurs : " + e.getMessage());
        }
    }
    private void updateStatusLabel() {
        if (utilisateurSelectionne != null) {
            statutLabel.setText(utilisateurSelectionne.isIs_active() ? "Statut: Actif" : "Statut: Désactivé");
            btnToggleStatus.setText(utilisateurSelectionne.isIs_active() ? "Désactiver" : "Activer");
            btnToggleStatus.setStyle(
                    "-fx-background-color: " + (utilisateurSelectionne.isIs_active() ? "#dc3545" : "#28a745") + "; " +
                            "-fx-text-fill: white; " +
                            "-fx-font-weight: bold; " +
                            "-fx-padding: 8 16; " +
                            "-fx-background-radius: 5;"
            );

            // Effet de survol
            btnToggleStatus.setOnMouseEntered(e ->
                    btnToggleStatus.setStyle(
                            "-fx-background-color: " + (utilisateurSelectionne.isIs_active() ? "#c82333" : "#218838") + "; " +
                                    "-fx-text-fill: white; " +
                                    "-fx-font-weight: bold; " +
                                    "-fx-padding: 8 16; " +
                                    "-fx-background-radius: 5;"
                    )
            );
            btnToggleStatus.setOnMouseExited(e ->
                    btnToggleStatus.setStyle(
                            "-fx-background-color: " + (utilisateurSelectionne.isIs_active() ? "#dc3545" : "#28a745") + "; " +
                                    "-fx-text-fill: white; " +
                                    "-fx-font-weight: bold; " +
                                    "-fx-padding: 8 16; " +
                                    "-fx-background-radius: 5;"
                    )
            );
        }
    }
    @FXML
    void btnStats(ActionEvent event) {
        try {
            List<Utilisateur> utilisateurs = serviceUtilisateur.afficher();

            // Compter les rôles
            long patients = utilisateurs.stream().filter(u -> "patient".equalsIgnoreCase(u.getRole())).count();
            long medecins = utilisateurs.stream().filter(u -> "medecin".equalsIgnoreCase(u.getRole())).count();
            long autres = utilisateurs.stream().filter(u -> !"patient".equalsIgnoreCase(u.getRole()) && !"medecin".equalsIgnoreCase(u.getRole())).count();

            // Créer les données pour le diagramme en secteurs
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                    new PieChart.Data("Patients", patients),
                    new PieChart.Data("Médecins", medecins),
                    new PieChart.Data("Admins", autres)
            );

            // Créer le diagramme en secteurs
            PieChart pieChart = new PieChart(pieChartData);
            pieChart.setTitle("Répartition des utilisateurs par rôle");
            pieChart.setLabelsVisible(true); // Afficher les étiquettes
            pieChart.setLegendVisible(true); // Afficher la légende

            // Créer une nouvelle scène pour afficher le diagramme
            VBox vbox = new VBox(pieChart);
            vbox.setPadding(new javafx.geometry.Insets(10));
            Scene scene = new Scene(vbox, 600, 400);

            // Ouvrir une nouvelle fenêtre
            Stage stage = new Stage();
            stage.setTitle("Statistiques des utilisateurs");
            stage.setScene(scene);
            stage.show();

        } catch (SQLException e) {
            System.err.println("Erreur lors du calcul des statistiques : " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur lors du chargement des statistiques");
            alert.setContentText("Une erreur s'est produite : " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void sendDeactivationEmail(Utilisateur utilisateur) {
        // Paramètres du serveur SMTP (ici pour Gmail)
        String host = "smtp.gmail.com";
        String port = "587";
        String mailFrom = "boussalem18faouzi@gmail.com"; // Remplacez par votre adresse e-mail
        String password = "hxhkiarhhbiytexv"; // Remplacez par un mot de passe d'application Gmail

        // Adresse e-mail de l'utilisateur désactivé
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
            message.setSubject("Votre compte a été désactivé");

            // Corps HTML stylisé
            String htmlContent = "<!DOCTYPE html>" +
                    "<html lang='fr'>" +
                    "<head>" +
                    "  <meta charset='UTF-8'>" +
                    "  <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                    "  <style>" +
                    "    body { margin: 0; padding: 20px; background-color: #f2f2f2; font-family: Arial, sans-serif; }" +
                    "    .container { background-color: #ffffff; margin: 0 auto; padding: 30px 20px; border-radius: 12px; max-width: 600px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); }" +
                    "    .header { background-color: #cc0000; color: white; text-align: center; padding: 30px 20px; border-top-left-radius: 12px; border-top-right-radius: 12px; }" +
                    "    .header h1 { margin: 0; font-size: 24px; }" +
                    "    .content { padding: 20px; text-align: left; color: #333333; font-size: 16px; line-height: 1.6; }" +
                    "    .button { display: inline-block; background-color: #cc0000; color: white; padding: 12px 24px; margin: 20px 0; text-decoration: none; font-weight: bold; border-radius: 6px; font-size: 18px; }" +
                    "    .footer { font-size: 13px; color: #777777; text-align: center; margin-top: 30px; }" +
                    "    .separator { border-top: 2px solid #cc0000; margin: 30px 0; }" +
                    "    a { color: #cc0000; text-decoration: none; }" +
                    "  </style>" +
                    "</head>" +
                    "<body>" +
                    "  <div class='container'>" +
                    "    <div class='header'>" +
                    "      <h1>Compte désactivé</h1>" +
                    "    </div>" +
                    "    <div class='content'>" +
                    "      <p>Bonjour <strong>" + utilisateur.getPrenom() + " " + utilisateur.getNom() + "</strong>,</p>" +
                    "      <p>Nous vous informons que votre compte a été <strong>désactivé</strong>.</p>" +
                    "      <p>Si vous pensez qu'il s'agit d'une erreur ou souhaitez réactiver votre compte, veuillez contacter notre administrateur :</p>" +
                    "      <div style='text-align:center;'>" +
                    "        <a href='mailto:admin@BrightMind.com' class='button'>Contacter l'administrateur</a>" +
                    "      </div>" +
                    "      <div class='separator'></div>" +
                    "      <p>Merci pour votre compréhension.<br>L'équipe de gestion.</p>" +
                    "    </div>" +
                    "    <div class='footer'>" +
                    "      <p>© 2025 Bright Mind. Tous droits réservés.</p>" +
                    "    </div>" +
                    "  </div>" +
                    "</body>" +
                    "</html>";

            message.setContent(htmlContent, "text/html; charset=UTF-8");

            // Envoyer le message
            Transport.send(message);
            System.out.println("E-mail de désactivation envoyé à : " + mailTo);

        } catch (MessagingException e) {
            System.err.println("Erreur lors de l'envoi de l'e-mail : " + e.getMessage());
        }
    }

    @FXML
    private void repondreDemande() {
        if (utilisateurSelectionne == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucun utilisateur sélectionné");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un utilisateur dans la liste.");
            alert.showAndWait();
            return;
        }

        // Créer une boîte de dialogue pour choisir l'action
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Approuver", "Approuver", "Rejeter");
        dialog.setTitle("Répondre à une demande");
        dialog.setHeaderText("Répondre à la demande de réactivation pour " + utilisateurSelectionne.getPrenom() + " " + utilisateurSelectionne.getNom());
        dialog.setContentText("Choisissez une action :");

        dialog.showAndWait().ifPresent(action -> {
            try {
                if (action.equals("Approuver")) {
                    // Réactiver le compte
                    serviceUtilisateur.updateStatus(utilisateurSelectionne.getId(), true);
                    utilisateurSelectionne.setIs_active(true);
                    sendReactivationEmail(utilisateurSelectionne);
                } else {
                    // Envoyer un e-mail de refus
                    sendRejectionEmail(utilisateurSelectionne);
                }
                // Rafraîchir l'affichage
                afficherUtilisateurs();
                updateStatusLabel();
            } catch (SQLException e) {
                System.err.println("Erreur lors de la mise à jour du statut : " + e.getMessage());
            }
        });
    }

    private Properties loadConfig() {
        Properties props = new Properties();
        props.setProperty("smtp.host", "smtp.gmail.com");
        props.setProperty("smtp.port", "587");
        props.setProperty("smtp.username", "boussalem18faouzi@gmail.com");
        props.setProperty("smtp.password", "hxhkiarhhbiytexv");
        props.setProperty("admin.email", "admin@votre-domaine.com");
        return props;
    }

    private void sendReactivationEmail(Utilisateur utilisateur) {
        Properties config = loadConfig();
        String host = config.getProperty("smtp.host", "smtp.gmail.com");
        String port = config.getProperty("smtp.port", "587");
        String mailFrom = config.getProperty("smtp.username", "boussalem18faouzi@gmail.com");
        String password = config.getProperty("smtp.password", "hxhkiarhhbiytexv");
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

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(mailFrom));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(mailTo));
            message.setSubject("Votre compte a été réactivé");


            String htmlContent = "<!DOCTYPE html>" +
                    "<html lang='fr'>" +
                    "<head>" +
                    "  <meta charset='UTF-8'>" +
                    "  <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                    "  <style>" +
                    "    body { margin: 0; padding: 20px; background-color: #f2f2f2; font-family: Arial, sans-serif; }" +
                    "    .container { background-color: #ffffff; margin: 0 auto; padding: 30px 20px; border-radius: 12px; max-width: 600px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); }" +
                    "    .header { background-color: #28a745; color: white; text-align: center; padding: 30px 20px; border-top-left-radius: 12px; border-top-right-radius: 12px; }" +
                    "    .header h1 { margin: 0; font-size: 24px; }" +
                    "    .content { padding: 20px; text-align: left; color: #333333; font-size: 16px; line-height: 1.6; }" +
                    "    .footer { font-size: 13px; color: #777777; text-align: center; margin-top: 30px; }" +
                    "    .separator { border-top: 2px solid #28a745; margin: 30px 0; }" +
                    "    a { color: #28a745; text-decoration: none; }" +
                    "  </style>" +
                    "</head>" +
                    "<body>" +
                    "  <div class='container'>" +
                    "    <div class='header'>" +
                    "      <h1>Compte réactivé</h1>" +
                    "    </div>" +
                    "    <div class='content'>" +
                    "      <p>Bonjour <strong>" + utilisateur.getPrenom() + " " + utilisateur.getNom() + "</strong>,</p>" +
                    "      <p>Nous sommes heureux de vous informer que votre compte a été <strong>réactivé</strong>.</p>" +
                    "      <p>Vous pouvez maintenant vous reconnecter et profiter de nos services.</p>" +
                    "      <div class='separator'></div>" +
                    "      <p>Merci pour votre confiance.<br>L'équipe de gestion.</p>" +
                    "    </div>" +
                    "    <div class='footer'>" +
                    "      <p>© 2025 Bright Mind. Tous droits réservés.</p>" +
                    "    </div>" +
                    "  </div>" +
                    "</body>" +
                    "</html>";

            message.setContent(htmlContent, "text/html; charset=UTF-8");

            Transport.send(message);
            System.out.println("E-mail de réactivation envoyé à : " + mailTo);

        } catch (MessagingException e) {
            System.err.println("Erreur lors de l'envoi de l'e-mail : " + e.getMessage());
        }
    }

    private void sendRejectionEmail(Utilisateur utilisateur) {
        Properties config = loadConfig();
        String host = config.getProperty("smtp.host", "smtp.gmail.com");
        String port = config.getProperty("smtp.port", "587");
        String mailFrom = config.getProperty("smtp.username", "boussalem18faouzi@gmail.com");
        String password = config.getProperty("smtp.password", "hxhkiarhhbiytexv");
        String adminEmail = config.getProperty("admin.email", "admin@votre-domaine.com");
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

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(mailFrom));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(mailTo));
            message.setSubject("Votre demande de réactivation a été rejetée");

            String htmlContent = "<!DOCTYPE html>" +
                    "<html lang='fr'>" +
                    "<head>" +
                    "  <meta charset='UTF-8'>" +
                    "  <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                    "  <style>" +
                    "    body { margin: 0; padding: 20px; background-color: #f2f2f2; font-family: Arial, sans-serif; }" +
                    "    .container { background-color: #ffffff; margin: 0 auto; padding: 30px 20px; border-radius: 12px; max-width: 600px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); }" +
                    "    .header { background-color: #cc0000; color: white; text-align: center; padding: 30px 20px; border-top-left-radius: 12px; border-top-right-radius: 12px; }" +
                    "    .header h1 { margin: 0; font-size: 24px; }" +
                    "    .content { padding: 20px; text-align: left; color: #333333; font-size: 16px; line-height: 1.6; }" +
                    "    .button { display: inline-block; background-color: #cc0000; color: white; padding: 12px 24px; margin: 20px 0; text-decoration: none; font-weight: bold; border-radius: 6px; font-size: 18px; }" +
                    "    .footer { font-size: 13px; color: #777777; text-align: center; margin-top: 30px; }" +
                    "    .separator { border-top: 2px solid #cc0000; margin: 30px 0; }" +
                    "    a { color: #cc0000; text-decoration: none; }" +
                    "  </style>" +
                    "</head>" +
                    "<body>" +
                    "  <div class='container'>" +
                    "    <div class='header'>" +
                    "      <h1>Demande rejetée</h1>" +
                    "    </div>" +
                    "    <div class='content'>" +
                    "      <p>Bonjour <strong>" + utilisateur.getPrenom() + " " + utilisateur.getNom() + "</strong>,</p>" +
                    "      <p>Nous vous informons que votre <strong>demande de réactivation</strong> a été <strong>rejetée</strong>.</p>" +
                    "      <p>Pour plus d'informations, veuillez contacter notre administrateur :</p>" +
                    "      <div style='text-align:center;'>" +
                    "        <a href='mailto:" + adminEmail + "' class='button'>Contacter l'administrateur</a>" +
                    "      </div>" +
                    "      <div class='separator'></div>" +
                    "      <p>Merci pour votre compréhension.<br>L'équipe de gestion.</p>" +
                    "    </div>" +
                    "    <div class='footer'>" +
                    "      <p>© 2025 Bright Mind. Tous droits réservés.</p>" +
                    "    </div>" +
                    "  </div>" +
                    "</body>" +
                    "</html>";

            message.setContent(htmlContent, "text/html; charset=UTF-8");
            Transport.send(message);
            System.out.println("E-mail de rejet envoyé à : " + mailTo);

        } catch (MessagingException e) {
            System.err.println("Erreur lors de l'envoi de l'e-mail : " + e.getMessage());
        }
    }
}
