package tn.esprit.controllers.Compagne;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import tn.esprit.entities.Post;

public class PostDetails {
    @FXML
    private Label titleLabel;
    @FXML
    private ImageView imageView;
    @FXML
    private Label contentLabel;

    public void setPostDetails(Post post) {
            titleLabel.setText(post.getTitle());
            contentLabel.setText(post.getContent());

            // Set image (if available)
            try {
                if (post.getImageUrl() != null && !post.getImageUrl().isBlank()) {
                    Image img = new Image("file:" + post.getImageUrl(), true);
                    imageView.setImage(img);
                } else {
                    imageView.setImage(new Image("file:images/placeholder.jpg"));
                }
            } catch (Exception e) {
                System.out.println("⚠️ Failed to load image for post " + post.getId());
                imageView.setImage(new Image("file:images/placeholder.jpg"));
            }
        }

        // Close the details window
        @FXML
        private void closePostDetails() {
            Stage stage = (Stage) titleLabel.getScene().getWindow();
            stage.close();
        }
    }


