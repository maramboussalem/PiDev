package tn.esprit.controllers.CompagnePublic;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import tn.esprit.entities.Post;
import tn.esprit.test.MyListener;

import java.io.InputStream;

public class ItemController {
    @FXML private Label title;
    @FXML private Label content;
    @FXML private ImageView img;

    private Post post;
    private MyListener myListener;

    public void setData(Post post, MyListener listener) {
        this.post = post;
        this.myListener = listener;

        title.setText(post.getTitle());
        content.setText(post.getContent());

        try {
            Image image;
            if (post.getImageUrl() != null && !post.getImageUrl().isBlank()) {
                // Use absolute or relative file path
                image = new Image("file:" + post.getImageUrl(), true);
            } else {
                // Fallback to a placeholder image inside the resources
                image = new Image(getClass().getResource("/images/placeholder.jpg").toExternalForm());
            }
            img.setImage(image);
        } catch (Exception e) {
            System.out.println("⚠️ Failed to load image for post " + post.getId() + ": " + e.getMessage());
            Image fallback = new Image(getClass().getResource("/images/placeholder.jpg").toExternalForm());
            img.setImage(fallback);
        }
        InputStream imageStream = getClass().getResourceAsStream(post.getImageUrl());
        /*if (imageStream != null) {
            img.setImage(new Image(imageStream));
        }*/

        // Optional: set click listener
        img.setOnMouseClicked(e -> myListener.onClickListener(post));
    }
}
