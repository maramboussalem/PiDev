package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import javafx.scene.Node;
import tn.esprit.utils.MyDataBase;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ModifierMotdepasse {

    @FXML
    private PasswordField VNmotdepasse;

    @FXML
    private Label errCNMotdepasse;

    @FXML
    private Label errNMotdepasse;

    @FXML
    private PasswordField nMotdepasse;

    private final Connection connection = MyDataBase.getInstance().getMyConnection();

    private String emailUtilisateur; // Reçu depuis le contrôleur précédent

    public void setEmail(String email) {
        this.emailUtilisateur = email;
    }

    @FXML
    void changerLEMotDePasse(ActionEvent event) {
        String nouveauMDP = nMotdepasse.getText();
        String confirmationMDP = VNmotdepasse.getText();

        errNMotdepasse.setText("");
        errCNMotdepasse.setText("");

        if (!nouveauMDP.equals(confirmationMDP)) {
            errCNMotdepasse.setText("Les mots de passe ne correspondent pas.");
            return;
        }

        if (nouveauMDP.length() < 6) {
            errNMotdepasse.setText("Le mot de passe doit contenir au moins 6 caractères.");
            return;
        }

        try {
            String motdepasseCrypte = BCrypt.hashpw(nouveauMDP, BCrypt.gensalt());

            String sql = "UPDATE utilisateur SET motdepasse = ? WHERE email = ?";
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setString(1, motdepasseCrypte);
            pst.setString(2, emailUtilisateur);
            int rows = pst.executeUpdate();

            if (rows > 0) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Utilisateur/login.fxml"));
                Scene loginScene = new Scene(loader.load());

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(loginScene);
                stage.setTitle("Connexion");
                stage.show();
            } else {
                errCNMotdepasse.setText("Erreur lors de la mise à jour.");
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            errCNMotdepasse.setText("Erreur : " + e.getMessage());
        }
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
