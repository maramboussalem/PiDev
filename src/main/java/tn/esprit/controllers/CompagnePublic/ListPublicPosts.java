package tn.esprit.controllers.CompagnePublic;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import tn.esprit.entities.Post;
import tn.esprit.services.ServicePost;
import tn.esprit.test.MyListener;
import javafx.stage.Stage;
import tn.esprit.utils.LanguageUtil;
import tn.esprit.utils.NewsletterService;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ListPublicPosts implements Initializable {
    //merge posts
    @FXML private VBox chosenFruitCard;
    @FXML private Label fruitNameLable;
    @FXML private Label content;
    @FXML private ImageView Img;
    @FXML private ScrollPane scroll;
    @FXML private GridPane grid;
    @FXML
    private TextField emailField;

    @FXML
    private Button subscribeButton;
    @FXML
    private Label languageLabel;

    @FXML
    private Label englishLabel;

    @FXML
    private Label frenchLabel;
    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private Button interactButton;

    private List<Post> posts = new ArrayList<>();
    private MyListener myListener;
    private Post currentPost;


    private List<Post> getData() throws SQLException {

        ServicePost postService = new ServicePost();
        List<Post> posts = postService.afficher();
        return posts;
    }

    private void setChosenPost(Post post) {
        this.currentPost = post;
        fruitNameLable.setText(post.getTitle());
        content.setText(post.getContent());
        /*InputStream imgStream = getClass().getResourceAsStream(post.getImageUrl());
        if (imgStream != null) Img.setImage(new Image(imgStream));*/
        try {
            Image image;
            if (post.getImageUrl() != null && !post.getImageUrl().isBlank()) {
                // Use absolute or relative file path
                image = new Image("file:" + post.getImageUrl(), true);
            } else {
                // Fallback to a placeholder image inside the resources
                image = new Image(getClass().getResource("/images/placeholder.jpg").toExternalForm());
            }
            Img.setImage(image);
        } catch (Exception e) {
            System.out.println("⚠️ Failed to load image for post " + post.getId() + ": " + e.getMessage());
            Image fallback = new Image(getClass().getResource("/images/placeholder.jpg").toExternalForm());
            Img.setImage(fallback);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Bind text properties to translations
        searchField.promptTextProperty().bind(Bindings.createStringBinding(
                () -> LanguageUtil.getMessage("search.prompt"), LanguageUtil.languageTriggerProperty()));
        searchButton.textProperty().bind(Bindings.createStringBinding(
                () -> LanguageUtil.getMessage("search.button"), LanguageUtil.languageTriggerProperty()));

        interactButton.textProperty().bind(Bindings.createStringBinding(
                () -> LanguageUtil.getMessage("interact.button"), LanguageUtil.languageTriggerProperty()));
        languageLabel.textProperty().bind(Bindings.createStringBinding(
                () -> LanguageUtil.getMessage("language.label"), LanguageUtil.languageTriggerProperty()));
        englishLabel.textProperty().bind(Bindings.createStringBinding(
                () -> LanguageUtil.getMessage("language.english"), LanguageUtil.languageTriggerProperty()));
        frenchLabel.textProperty().bind(Bindings.createStringBinding(
                () -> LanguageUtil.getMessage("language.french"), LanguageUtil.languageTriggerProperty()));
        emailField.promptTextProperty().bind(Bindings.createStringBinding(
                () -> LanguageUtil.getMessage("newsletter.prompt"), LanguageUtil.languageTriggerProperty()));
        subscribeButton.textProperty().bind(Bindings.createStringBinding(
                () -> LanguageUtil.getMessage("subscribe.button"), LanguageUtil.languageTriggerProperty()));

        // Set up language switching
        englishLabel.setOnMouseClicked(event -> {
            LanguageUtil.setLocale("en");
            updateLanguageSelection();
        });

        frenchLabel.setOnMouseClicked(event -> {
            LanguageUtil.setLocale("fr");
            updateLanguageSelection();
        });

        // Initialize language selection UI
        updateLanguageSelection();
        subscribeButton.setOnAction(event -> handleSubscribe());
        try {
            posts.addAll(getData());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (!posts.isEmpty()) {
            setChosenPost(posts.get(0));
            myListener = post -> setChosenPost(post);
        }

        int column = 0;
        int row = 1;
        try {
            for (Post post : posts) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/CompagnePublic/item.fxml"));
                AnchorPane pane = loader.load();
                ItemController controller = loader.getController();
                controller.setData(post, myListener);

                if (column == 3) {
                    column = 0;
                    row++;
                }
                grid.add(pane, column++, row);
                GridPane.setMargin(pane, new Insets(10));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void readMore(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/CompagnePublic/post.fxml"));
        Parent root = loader.load();


        PostController controller = loader.getController();
        controller.setPost(currentPost);

        Stage stage = new Stage();
        stage.setTitle("Post");
        stage.setScene(new Scene(root));
        stage.show();
    }
    private void handleSubscribe() {
        String email = emailField.getText().trim();

        if (email.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please enter your email address.");
            return;
        }

        String result = NewsletterService.subscribe(email);

        switch (result) {
            case "SUCCESS":
                showAlert(Alert.AlertType.INFORMATION, "Success", "You have been successfully subscribed to the newsletter!");
                emailField.clear();
                break;
            case "ALREADY_SUBSCRIBED":
                showAlert(Alert.AlertType.WARNING, "Already Subscribed", "This email is already subscribed.");
                break;
            default:
                showAlert(Alert.AlertType.ERROR, "Subscription Failed", "Error occurred: " + result);
                break;
        }
    }
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void updateLanguageSelection() {
        String currentLang = LanguageUtil.getCurrentLanguage();
        englishLabel.setUnderline(currentLang.equals("en"));
        frenchLabel.setUnderline(currentLang.equals("fr"));
    }
}

