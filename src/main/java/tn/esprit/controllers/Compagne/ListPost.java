package tn.esprit.controllers.Compagne;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import tn.esprit.entities.Post;
import tn.esprit.services.ServicePost;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ListPost {

    @FXML
    private VBox pnItems;

    private HBox createPostHBox(Post post) {
        HBox hbox = new HBox(20);
        hbox.setPadding(new Insets(15));
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setStyle("-fx-background-color: #00b8bb; -fx-background-radius: 12; -fx-border-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 8, 0, 0, 4);");

        // Image
        ImageView imageView = new ImageView();
        imageView.setFitWidth(60);
        imageView.setFitHeight(60);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setStyle("-fx-effect: dropshadow(gaussian, black, 4, 0.5, 0, 2);");

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

        // Text Info
        VBox textInfo = new VBox(5);
        textInfo.setStyle("-fx-text-fill: white;");
        Label titleLabel = new Label(post.getTitle());
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label contentLabel = new Label(post.getContent().length() > 80 ? post.getContent().substring(0, 80) + "..." : post.getContent());
        contentLabel.setStyle("-fx-text-fill: white;");
        Label metaLabel = new Label(post.getPublishedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "   👁 " + post.getViews());
        metaLabel.setStyle("-fx-font-size: 12px; -fx-text-fill:white;");
        textInfo.getChildren().addAll(titleLabel, contentLabel, metaLabel);

        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Buttons
        Button editButton = new Button("✏️");
        editButton.setStyle("-fx-background-color: #ffc107; -fx-text-fill: black; -fx-font-weight: bold; -fx-background-radius: 8;");
        editButton.setOnAction(e -> handleEdit(post));

        Button deleteButton = new Button("🗑️");
        deleteButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
        deleteButton.setOnAction(e -> handleDelete(post));

        Button viewDetailsButton = new Button("View Details");
        viewDetailsButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
        viewDetailsButton.setOnAction(e -> handleViewDetails(post));

        HBox buttonBox = new HBox(10, editButton, deleteButton, viewDetailsButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        hbox.getChildren().addAll(imageView, textInfo, spacer, buttonBox);
        return hbox;
    }

    private void handleViewDetails(Post post) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Compagne/PostDetails.fxml"));
            Parent root = loader.load();

            // Access the controller and set the post details
            PostDetails controller = loader.getController();
            controller.setPostDetails(post);

            // Open the new window
            Stage stage = new Stage();
            stage.setTitle("Post Details");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEdit(Post post) {
        System.out.println("Editing post: " + post.getId());
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Compagne/UpdatePost.fxml"));
            Parent root = loader.load();
            UpdatePost controller = loader.getController();
            controller.setPost(post);
            controller.setOnPostUpdated(() -> {
                try {
                    refreshPostList();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
            Stage stage = new Stage();
            stage.setTitle("Modifier un Post");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void handleDelete(Post post) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Deletion");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("Are you sure you want to delete this post?");

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Delete from DB using your service class
                    ServicePost servicePost = new ServicePost();
                    servicePost.supprimer(post.getId()); // Make sure postService is initialized

                    // Optionally, refresh the UI list
                    refreshPostList();

                    showAlert(Alert.AlertType.INFORMATION, "Success", "Post deleted successfully.");
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete post: " + e.getMessage());
                }
            }
        });
    }
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
    private void refreshPostList() throws SQLException {
        ServicePost servicePost = new ServicePost();
        List<Post> posts = servicePost.afficher(); // Re-fetch from DB
        pnItems.getChildren().clear(); // VBox or ListView
        for (Post post : posts) {
            Node postCard = createPostHBox(post);
            pnItems.getChildren().add(postCard);
        }
    }



    public void displayPosts() {
        try {
            ServicePost postService = new ServicePost();
            List<Post> posts = postService.afficher();

            pnItems.getChildren().clear(); // Clear previous items
            for (Post post : posts) {
                HBox postBox = createPostHBox(post);
                pnItems.getChildren().add(postBox);
                VBox.setMargin(postBox, new Insets(10, 0, 10, 0)); // spacing between cards
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void ajouterPostView(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Compagne/AddPost.fxml"));
            Parent root = loader.load();

            // Get the controller of AddPost.fxml
            AddPost addPostController = loader.getController();

            // Set the callback to refresh the post list
            addPostController.setOnPostAdded(() -> {
                try {
                    refreshPostList(); // ⬅️ This is already defined and works
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

            // Open the new window
            Stage stage = new Stage();
            stage.setTitle("Ajouter un Post");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void initialize() {
        displayPosts();
    }
}
