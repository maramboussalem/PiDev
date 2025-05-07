package tn.esprit.controllers.Compagne;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.esprit.entities.Post;
import tn.esprit.services.ServicePost;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.List;

public class UpdatePost {
    //update
    @FXML
    private TextArea contenu;

    @FXML
    private ImageView imageView;

    @FXML
    private TextField titre;

    private Runnable onPostUpdated;
    private String uploadedImagePath = "";

    private Post currentPost;
    @FXML
    private Button uploadButton;
    @FXML
    private void initialize() {
        uploadButton.setOnAction(e -> {
            uploadedImagePath = handleImageUpload(); // save the uploaded image path
        });
    }
    public void setPost(Post post) {
        this.currentPost = post;

        // Pre-fill the UI with post data
        titre.setText(post.getTitle());
        contenu.setText(post.getContent());

        // Load and set the image
        if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
            Image image = new Image("file:" + post.getImageUrl(), true);
            imageView.setImage(image);
        } else {
            imageView.setImage(new Image("file:images/placeholder.jpg"));
        }
    }

    private String handleImageUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisissez une image");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(uploadButton.getScene().getWindow());

        if (selectedFile != null) {
            try {

                File destFolder = new File("images");
                if (!destFolder.exists()) destFolder.mkdirs();


                File destFile = new File(destFolder, selectedFile.getName());
                Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);


                Image image = new Image(destFile.toURI().toString());
                imageView.setImage(image);

                // Return the relative path to save in the database
                return "images/" + selectedFile.getName();
            } catch (IOException ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Image Upload Failed", "Could not upload the selected image.");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "No Image Selected", "Please select an image to upload.");
        }

        return "";
    }
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void modifierPost() {
        if (currentPost == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun post sélectionné pour la modification.");
            return;
        }

        String newTitre = titre.getText().trim();
        String newContenu = contenu.getText().trim();

        if (newTitre.isEmpty() || newContenu.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champs manquants", "Veuillez remplir tous les champs.");
            return;
        }

        // Update post fields
        currentPost.setTitle(newTitre);
        currentPost.setContent(newContenu);

        if (!uploadedImagePath.isEmpty()) {
            currentPost.setImageUrl(uploadedImagePath);
        }

        try {
            ServicePost servicePost = new ServicePost(); // Adjust if you're using dependency injection
            servicePost.modifier(currentPost);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Le post a été modifié avec succès.");
            //  Call the callback if it's set
            if (onPostUpdated != null) {
                onPostUpdated.run();
            }

            // Optionally, close the current window after update
            ((Stage) titre.getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite lors de la modification.");
        }
    }
    public void setOnPostUpdated(Runnable onPostUpdated) {
        this.onPostUpdated = onPostUpdated;
    }
}
