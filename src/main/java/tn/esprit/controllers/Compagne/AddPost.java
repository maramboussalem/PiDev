package tn.esprit.controllers.Compagne;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
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

public class AddPost {
    @FXML
    private TextArea contenu;

    @FXML
    private ImageView imageView;

    @FXML
    private TextField titre;

    @FXML
    private Button uploadButton;
    @FXML
    private Label validationContenu;

    @FXML
    private Label validationImage;

    @FXML
    private Label validationTitre;

    private Runnable onPostAdded;
    private String uploadedImagePath = "";
    @FXML
    private void initialize() {
        uploadButton.setOnAction(e -> {
            uploadedImagePath = handleImageUpload(); // save the uploaded image path
        });
    }

    private String handleImageUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose an Image");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(uploadButton.getScene().getWindow());

        if (selectedFile != null) {
            try {
                // Create /images folder if it doesn't exist
                File destFolder = new File("images");
                if (!destFolder.exists()) destFolder.mkdirs();

                // Copy the file into the /images folder
                File destFile = new File(destFolder, selectedFile.getName());
                Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                // Display the image in the ImageView
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
    private void clearValidationLabels() {
        validationTitre.setText("");
        validationContenu.setText("");
        validationImage.setText("");
    }
    public void addPost(ActionEvent actionEvent) {
        ServicePost servicePost = new ServicePost();
        clearValidationLabels();
        /*if (titre.getText().isEmpty() || contenu.getText().isEmpty() || uploadedImagePath.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Missing Information");
            alert.setContentText("Please fill in all fields and upload an image.");
            alert.showAndWait();
            return;
        }*/
        boolean isValid = true;

        // Validate title
        if (titre.getText().trim().isEmpty()) {
            validationTitre.setText("Title is required.");
            isValid = false;
        }

        // Validate content
        if (contenu.getText().trim().isEmpty()) {
            validationContenu.setText("Content is required.");
            isValid = false;
        }

        // Validate image
        if (uploadedImagePath.isEmpty()) {
            validationImage.setText("Please upload an image.");
            isValid = false;
        }

        if (!isValid) return;

        Post post = new Post(titre.getText(), contenu.getText(), uploadedImagePath);
        try {
            servicePost.ajouter(post);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setContentText("Post added successfully!");
            alert.showAndWait();

            // Optional: clear fields
            titre.clear();
            contenu.clear();
            imageView.setImage(null);
            uploadedImagePath = "";
            // ✅ Call the callback
            if (onPostAdded != null) {
                onPostAdded.run();
            }

// ✅ Optional: Close the window after success
            ((Stage)((Node)actionEvent.getSource()).getScene().getWindow()).close();

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setContentText("Error while saving the post.");
            alert.showAndWait();
        }


    }
    public void setOnPostAdded(Runnable onPostAdded) {
        this.onPostAdded = onPostAdded;
    }
}