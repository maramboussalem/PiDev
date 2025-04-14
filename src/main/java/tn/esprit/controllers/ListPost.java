package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import tn.esprit.entities.Post;
import tn.esprit.services.ServicePost;

import java.sql.SQLException;
import java.util.List;

public class ListPost {
    @FXML
    private VBox pnItems;
    private HBox createPostHBox(Post post) {
        HBox hbox = new HBox(50);
        hbox.setStyle("-fx-background-color: #fff; -fx-padding: 10; -fx-border-color: #ccc; -fx-border-radius: 5;");

        Label idLabel = new Label(String.valueOf(post.getId()));
        Label titleLabel = new Label(post.getTitle());
        Label contentLabel = new Label(post.getContent());
        Label dateLabel = new Label(post.getPublishedAt().toString());
        Label viewsLabel = new Label(String.valueOf(post.getViews()));

        ImageView imageView = new ImageView();
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);
        try {
            Image img = new Image(post.getImageUrl(), true);
            imageView.setImage(img);
        } catch (Exception e) {
            System.out.println("Failed to load image for post " + post.getId());
        }

        hbox.getChildren().addAll(idLabel, titleLabel, contentLabel, dateLabel, viewsLabel, imageView);
        return hbox;
    }

    public void displayPosts() {
        try {
            ServicePost postService = new ServicePost(); // Assuming you have this class
            List<Post> posts = postService.afficher(); // Fetch posts from DB

            for (Post post : posts) {
                System.out.println(post);
                HBox postBox = createPostHBox(post);
                pnItems.getChildren().add(postBox);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void initialize() {
        displayPosts();
    }
}
