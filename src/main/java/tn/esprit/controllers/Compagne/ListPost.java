package tn.esprit.controllers.Compagne;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import tn.esprit.entities.Post;
import tn.esprit.services.ServicePost;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ListPost {
    //list
    @FXML
    private VBox pnItems;
    @FXML
    private TextField searchField;
    private List<Post> allPosts;



    private HBox createPostHBox(Post post) {
        HBox hbox = new HBox(25);
        hbox.setPadding(new Insets(20));
        hbox.setSpacing(20);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setMinHeight(160);
        hbox.setPrefHeight(180);
        hbox.setMaxHeight(200);

        //hbox.setPadding(new Insets(20));
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setStyle("-fx-background-color: #ffffff; "
                + "-fx-background-radius: 15px; "
                + "-fx-border-radius: 15px; "
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0.3, 0, 4);");


        ImageView imageView = new ImageView();
        imageView.setFitWidth(200);
        imageView.setFitHeight(250);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setStyle("-fx-effect: dropshadow(gaussian, white, 4, 0.5, 0, 0);");



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


        VBox textInfo = new VBox(8);
        Label titleLabel = new Label(post.getTitle());
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

        Label metaLabel = new Label(post.getPublishedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "   👁 " + post.getViews());
        metaLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #777;");

        Label contentLabel = new Label(post.getContent().length() > 80 ? post.getContent().substring(0, 80) + "..." : post.getContent());
        contentLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #555;");
        contentLabel.setWrapText(true);
        contentLabel.setMaxWidth(300);

        textInfo.getChildren().addAll(titleLabel, metaLabel, contentLabel);


        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);


        Button editButton = new Button("✏️");
        styleButton(editButton, "#ffc107", "black");

        Button deleteButton = new Button("🗑️");
        styleButton(deleteButton, "#dc3545", "white");

        Button viewDetailsButton = new Button("Voir détails");
        styleButton(viewDetailsButton, "#28a745", "white");

        editButton.setOnAction(e -> handleEdit(post));
        deleteButton.setOnAction(e -> handleDelete(post));
        viewDetailsButton.setOnAction(e -> handleViewDetails(post));

        HBox buttonBox = new HBox(10, editButton, deleteButton, viewDetailsButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        hbox.getChildren().addAll(imageView, textInfo, spacer, buttonBox);
        return hbox;
    }


    private void styleButton(Button button, String bgColor, String textColor) {
        button.setStyle("-fx-background-color: " + bgColor + ";"
                + "-fx-text-fill: " + textColor + ";"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 8;"
                + "-fx-cursor: hand;");
    }


    private void handleViewDetails(Post post) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Compagne/PostDetails.fxml"));
            Parent root = loader.load();

            PostDetails controller = loader.getController();
            controller.setPostDetails(post);

            Stage stage = new Stage();
            stage.setTitle("Détails Post");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEdit(Post post) {
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
        confirmationAlert.setTitle("Confirmer la suppression");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("Êtes-vous sûr de vouloir supprimer ce message ?");

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {

                    ServicePost servicePost = new ServicePost();
                    servicePost.supprimer(post.getId());


                    refreshPostList();

                    showAlert(Alert.AlertType.INFORMATION, "Success", "Message supprimé avec succès.");
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
        List<Post> posts = servicePost.afficher();
        pnItems.getChildren().clear();
        for (Post post : posts) {
            Node postCard = createPostHBox(post);
            pnItems.getChildren().add(postCard);
        }
    }



    public void displayPosts(List<Post> posts) {
        pnItems.getChildren().clear();
        for (Post post : posts) {
            HBox postBox = createPostHBox(post);
            pnItems.getChildren().add(postBox);
            VBox.setMargin(postBox, new Insets(10, 0, 10, 0)); // spacing between cards
        }
    }

    private void filterPosts(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            displayPosts(allPosts);
        } else {
            List<Post> filteredPosts = allPosts.stream()
                    .filter(post -> post.getTitle().toLowerCase().contains(keyword.toLowerCase()))
                    .toList();

            displayPosts(filteredPosts);
        }
    }


    @FXML
    public void ajouterPostView(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Compagne/AddPost.fxml"));
            Parent root = loader.load();


            AddPost addPostController = loader.getController();


            addPostController.setOnPostAdded(() -> {
                try {
                    refreshPostList();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });


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
        ServicePost postService = new ServicePost();
        try {
            allPosts = postService.afficher();
            displayPosts(allPosts);
        } catch (SQLException e) {
            e.printStackTrace();
        }


        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterPosts(newValue);
        });
    }

}
