package tn.esprit.controllers.CompagnePublic;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tn.esprit.entities.*;
import tn.esprit.services.AudioRecorder;
import tn.esprit.services.ServiceComment;
import tn.esprit.services.ServicePost;
import tn.esprit.services.ServiceReport;
import tn.esprit.utils.SessionManager;
import javafx.scene.media.Media;


import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

public class PostController {
    //post
    @FXML
    private Label title;

    @FXML
    private ImageView imgAngry;

    @FXML
    private ImageView imgCare;

    @FXML
    private ImageView imgHaha;

    @FXML
    private ImageView imgLike;

    @FXML
    private ImageView imgLove;

    @FXML
    private ImageView imgPost;

    @FXML
    private ImageView imgReaction;

    @FXML
    private ImageView imgSad;

    @FXML
    private ImageView imgWow;

    @FXML
    private HBox likeContainer;

    @FXML
    private Label nbComments;

    @FXML
    private Label nbReactions;

    @FXML
    private Label reactionName;

    @FXML
    private HBox reactionsContainer;

    @FXML
    private Button submitCommentButton;

    @FXML
    private TextField commentInput;

    @FXML
    private VBox commentsContainer;

    private long startTime = 0;
    private Reactions currentReaction;
    private Post post;
    private Post currentPost;
    Utilisateur utilisateurConnecte = SessionManager.getInstance().getUtilisateur();
    @FXML
    private Button emojiButton;
    @FXML public Button recordButton;
    @FXML public Button stopButton;
    @FXML public Button playButton;
    private File recordedFile;
    private String recordedAudioPath;
    private AudioRecorder audioRecorder;

    @FXML
    void onLikeContainerMouseReleased(MouseEvent event) {
        if(System.currentTimeMillis() - startTime > 500){
            reactionsContainer.setVisible(true);
        }else {
            if(reactionsContainer.isVisible()){
                reactionsContainer.setVisible(false);
            }
            if(currentReaction == Reactions.NON){
                setReaction(Reactions.LIKE);
            }else{
                setReaction(Reactions.NON);
            }
        }
    }

    @FXML
    void onLikeContainerPressed(MouseEvent event) {
        startTime = System.currentTimeMillis();
    }

    @FXML
    void onReactionImgPressed(MouseEvent me) {
        switch (((ImageView) me.getSource()).getId()){
            case "imgLove":
                setReaction(Reactions.LOVE);
                break;
            case "imgCare":
                setReaction(Reactions.CARE);
                break;
            case "imgHaha":
                setReaction(Reactions.HAHA);
                break;
            case "imgWow":
                setReaction(Reactions.WOW);
                break;
            case "imgSad":
                setReaction(Reactions.SAD);
                break;
            case "imgAngry":
                setReaction(Reactions.ANGRY);
                break;
            default:
                setReaction(Reactions.LIKE);
                break;
        }
        reactionsContainer.setVisible(false);

    }
    public void setReaction(Reactions reaction){
        this.post = currentPost;
        if (currentPost == null) return;
        if (reaction == currentReaction) return;

        if (currentReaction == Reactions.NON && reaction != Reactions.NON) {
            currentPost.setTotalReactions(currentPost.getTotalReactions() + 1);
        } else if (currentReaction != Reactions.NON && reaction == Reactions.NON) {
            int newCount = currentPost.getTotalReactions() - 1;
            currentPost.setTotalReactions(Math.max(0, newCount));
        }

        currentReaction = reaction;
        if (reaction == Reactions.NON) {
            imgReaction.setImage(null);
            reactionName.setText("Like");
            reactionName.setTextFill(Color.BLACK);
        } else {
            Image image = new Image(getClass().getResourceAsStream("/" + reaction.getImgSrc()));
            imgReaction.setImage(image);
            reactionName.setText(reaction.getName());
            reactionName.setTextFill(Color.web(reaction.getColor()));
        }
        nbReactions.setText(String.valueOf(currentPost.getTotalReactions()));
    }
    public void setPost(Post post) {
        this.currentPost = post;
        title.setText(currentPost.getTitle());

        nbComments.setText(String.valueOf(currentPost.getComments().size()));
        try {
            Image image;
            if (post.getImageUrl() != null && !post.getImageUrl().isBlank()) {

                image = new Image("file:" + currentPost.getImageUrl(), true);
            } else {

                image = new Image(getClass().getResource("/images/placeholder.jpg").toExternalForm());
            }
            imgPost.setImage(image);
            displayComments();
        } catch (Exception e) {
            System.out.println("Failed to load image for post " + post.getId() + ": " + e.getMessage());
            Image fallback = new Image(getClass().getResource("/images/placeholder.jpg").toExternalForm());
            imgPost.setImage(fallback);
        }

    }

    @FXML
    public void onSubmitComment() throws SQLException {
        ServiceComment serviceComment = new ServiceComment();
        String commentText = commentInput.getText().trim();

        // Check if the comment is empty (text and audio)
        if (commentText.isEmpty() && recordedAudioPath == null) {
            // Show an alert to the user if no content is provided
            // Example: AlertHelper.showError("Please provide a comment or record an audio.");
            return;
        }

        // Create a new Comment object
        Comment newComment = new Comment(commentText, utilisateurConnecte, currentPost);

        // If an audio comment exists, set its path
        if (recordedAudioPath != null) {
            newComment.setAudioPath(recordedAudioPath);  // assuming setAudioPath() is implemented in Comment
        }

        // Clear the input field and reset the audio path after submission
        commentInput.clear();
        //recordedAudioPath = null;  // Reset the audio path after submitting

        // Try to add the comment and refresh the comment list
        try {
            serviceComment.ajouter(newComment);
            refreshCommentList();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception (e.g., show an alert)
        }
    }


    public void displayComments() {
        try {
            ServiceComment commentService = new ServiceComment();
            List<Comment> comments = commentService.afficher();

            commentsContainer.getChildren().clear();

            for (Comment comment : comments) {
                if (comment.getPost().getId() == currentPost.getId()) {
                    HBox commentBox = new HBox(10);
                    commentBox.setPadding(new Insets(10));
                    commentBox.setAlignment(Pos.TOP_LEFT);

                    // ===== USER IMAGE =====
                    ImageView userImageView;
                    if (comment.getUser().getImg_url() != null && !comment.getUser().getImg_url().isBlank()) {
                        Image userImage = new Image(getClass().getResourceAsStream("/images/profiles/" + comment.getUser().getImg_url()));
                        userImageView = new ImageView(userImage);
                    } else {
                        Image defaultImage = new Image(getClass().getResourceAsStream("/images/profiles/default_user.jpg"));
                        userImageView = new ImageView(defaultImage);
                    }
                    userImageView.setFitWidth(60);
                    userImageView.setFitHeight(60);
                    userImageView.setPreserveRatio(false);
                    userImageView.setSmooth(true);
                    Circle clip = new Circle(30, 30, 30);
                    userImageView.setClip(clip);

                    VBox userInfoCommentBox = new VBox(2);
                    userInfoCommentBox.setAlignment(Pos.CENTER_LEFT);

                    Label usernameLabel = new Label(comment.getUser().getPrenom() + " " + comment.getUser().getNom());
                    usernameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

                    Label dateLabel = new Label(comment.getCommentDate().toString());
                    dateLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");

                    Label commentContentLabel = new Label(comment.getContent());
                    commentContentLabel.setWrapText(true);
                    commentContentLabel.setMaxWidth(400);

                    userInfoCommentBox.getChildren().addAll(usernameLabel, dateLabel, commentContentLabel);
                    // ===== AUDIO PLAYBACK =====

                    System.out.println(comment);
                    if (comment.getContent().isEmpty()){
                        System.out.println("Empty comment");
                        Button playButton = new Button("▶️ Play Audio");
                        playButton.setOnAction(e -> {
                            try {
                                Media media = new Media(new File(comment.getAudioPath()).toURI().toString());
                                MediaPlayer mediaPlayer = new MediaPlayer(media);
                                mediaPlayer.play();
                            } catch (Exception ex) {
                                System.err.println("Error playing audio: " + ex.getMessage());
                            }
                        });
                        userInfoCommentBox.getChildren().add(playButton);
                    }

                    // ===== ACTIONS =====
                    VBox actionsBox = new VBox(5);
                    actionsBox.setAlignment(Pos.TOP_RIGHT);
                    ImageView reportIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/report.png"))); // small report icon
                    reportIcon.setFitWidth(20);
                    reportIcon.setFitHeight(20);
                    reportIcon.setOnMouseClicked(e -> onReportComment(comment));

                    // Check if the logged-in user is the owner of the comment
                    if (comment.getUser().getId() == utilisateurConnecte.getId()) {
                        ImageView editIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/edit.png")));
                        editIcon.setFitWidth(20);
                        editIcon.setFitHeight(20);
                        editIcon.setOnMouseClicked(e -> onEditComment(comment));

                        ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/supprimer.png")));
                        deleteIcon.setFitWidth(20);
                        deleteIcon.setFitHeight(20);
                        deleteIcon.setOnMouseClicked(e -> {
                            try {
                                onDeleteComment(comment);
                            } catch (SQLException ex) {
                                throw new RuntimeException(ex);
                            }
                        });

                        actionsBox.getChildren().addAll(editIcon, deleteIcon);
                    }


                    commentBox.getChildren().addAll(userImageView, userInfoCommentBox, actionsBox,reportIcon);
                    commentsContainer.getChildren().add(commentBox);
                }
            }

            // Update number of comments
            nbComments.setText(String.valueOf(commentsContainer.getChildren().size()));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }





    private void onEditComment(Comment comment) {
        // Create a popup window (Stage)
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Editer Commentaire");


        TextArea editTextArea = new TextArea(comment.getContent());
        editTextArea.setWrapText(true);
        editTextArea.setPrefWidth(300);
        editTextArea.setPrefHeight(150);

        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            String newContent = editTextArea.getText().trim();
            if (!newContent.isEmpty()) {
                try {
                    // Update the comment object
                    comment.setContent(newContent);
                    comment.setCommentDate(LocalDateTime.now());

                    // Update in database
                    ServiceComment serviceComment = new ServiceComment();
                    serviceComment.modifier(comment);


                    displayComments();

                    popupStage.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Le commentaire ne peut pas être vide !", ButtonType.OK);
                alert.showAndWait();
            }
        });

        VBox layout = new VBox(10, editTextArea, saveButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }


    private void onDeleteComment(Comment comment) throws SQLException {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmer la suppression");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("Message supprimé avec succès.");

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {

                    ServiceComment serviceComment = new ServiceComment();
                    serviceComment.supprimer(comment.getId());


                    //refreshPostList();

                    showAlert(Alert.AlertType.INFORMATION, "Success", "Post supprimé avec succès.");
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete post: " + e.getMessage());
                }
            }
        });

        refreshCommentList();
    }
    private void refreshCommentList() throws SQLException {
        ServiceComment serviceComment = new ServiceComment();
        List<Comment> comments = serviceComment.afficher();

        commentsContainer.getChildren().clear();

        for (Comment comment : comments) {
            if (comment.getPost().getId() == currentPost.getId()) {
                HBox commentBox = new HBox(10);
                commentBox.setPadding(new Insets(10));
                commentBox.setAlignment(Pos.TOP_LEFT);


                ImageView userImageView;
                if (comment.getUser().getImg_url() != null && !comment.getUser().getImg_url().isBlank()) {
                    Image userImage = new Image(getClass().getResourceAsStream("/images/profiles/" + comment.getUser().getImg_url()));
                    userImageView = new ImageView(userImage);
                } else {
                    Image defaultImage = new Image(getClass().getResourceAsStream("/images/profiles/default_user.jpg"));
                    userImageView = new ImageView(defaultImage);
                }
                userImageView.setFitWidth(60);
                userImageView.setFitHeight(60);
                userImageView.setPreserveRatio(false);
                userImageView.setSmooth(true);
                Circle clip = new Circle(30, 30, 30);
                userImageView.setClip(clip);

                VBox userInfoCommentBox = new VBox(2);
                userInfoCommentBox.setAlignment(Pos.CENTER_LEFT);

                Label usernameLabel = new Label(comment.getUser().getPrenom() + " " + comment.getUser().getNom());
                usernameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

                Label dateLabel = new Label(comment.getCommentDate().toString());
                dateLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");

                Label commentContentLabel = new Label(comment.getContent());
                commentContentLabel.setWrapText(true);
                commentContentLabel.setMaxWidth(400);

                userInfoCommentBox.getChildren().addAll(usernameLabel, dateLabel, commentContentLabel);
                // ===== AUDIO PLAYBACK =====
                if (comment.getContent().isEmpty()){
                    System.out.println("Empty comment");
                    Button playButton = new Button("▶️ Play Audio");
                    playButton.setOnAction(e -> {
                        try {
                            Media media = new Media(new File(recordedAudioPath).toURI().toString());
                            MediaPlayer mediaPlayer = new MediaPlayer(media);
                            mediaPlayer.play();
                        } catch (Exception ex) {
                            System.err.println("Error playing audio: " + ex.getMessage());
                        }
                    });
                    userInfoCommentBox.getChildren().add(playButton);
                }

                VBox actionsBox = new VBox(5);
                actionsBox.setAlignment(Pos.TOP_RIGHT);
                ImageView reportIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/report.png"))); // small report icon
                reportIcon.setFitWidth(20);
                reportIcon.setFitHeight(20);
                reportIcon.setOnMouseClicked(e -> onReportComment(comment));


                if (comment.getUser().getId() == utilisateurConnecte.getId()) {
                    ImageView editIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/edit.png")));
                    editIcon.setFitWidth(20);
                    editIcon.setFitHeight(20);
                    editIcon.setOnMouseClicked(e -> onEditComment(comment));

                    ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/supprimer.png")));
                    deleteIcon.setFitWidth(20);
                    deleteIcon.setFitHeight(20);
                    deleteIcon.setOnMouseClicked(e -> {
                        try {
                            onDeleteComment(comment);
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                    });

                    actionsBox.getChildren().addAll(editIcon, deleteIcon, reportIcon);
                }


                commentBox.getChildren().addAll(userImageView, userInfoCommentBox, actionsBox);
                commentsContainer.getChildren().add(commentBox);
            }
        }

        nbComments.setText(String.valueOf(commentsContainer.getChildren().size()));
    }
    private void onReportComment(Comment comment) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("signaler commentaire");
        dialog.setHeaderText("Pourquoi signalez-vous ce commentaire ?");
        dialog.setContentText("Reason:");

        dialog.showAndWait().ifPresent(reason -> {
            try {
                Report report = new Report();
                report.setCommentId(comment.getId());
                report.setUserId(utilisateurConnecte.getId());
                report.setReportReason(reason);
                report.setReportDate(LocalDateTime.now());

                ServiceReport serviceReport = new ServiceReport();
                serviceReport.addReport(report);
                int reportCount = serviceReport.countReportsForComment(comment.getId());
                if (reportCount >= 2) {
                    serviceReport.deleteReportsByCommentId(comment.getId());
                    ServiceComment serviceComment = new ServiceComment();
                    serviceComment.supprimer(comment.getId());
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Commentaire supprimé");
                    alert.setHeaderText(null);
                    alert.setContentText("Le commentaire a été supprimé en raison de plusieurs rapports");
                    alert.showAndWait();

                    // Refresh comments list
                    displayComments();
                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Signalée");
                    alert.setHeaderText(null);
                    alert.setContentText("Merci ! Votre signalement a été envoyé.");
                    alert.showAndWait();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }




    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleEmojiButtonClick() {
        EmojiPicker emojiPicker = new EmojiPicker(commentInput);
        emojiPicker.show();
    }

    @FXML
    public void startRecording() {
        String filename = UUID.randomUUID().toString(); // generate unique filename
        audioRecorder.startRecording(filename);
    }
    @FXML
    public void stopRecording() {
        audioRecorder.stopRecording();
        recordedFile = audioRecorder.getAudioFile();
        recordedAudioPath = recordedFile.getAbsolutePath();
    }
    @FXML
    public void playAudio() {
        if (recordedFile != null) {
            Media media = new Media(recordedFile.toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            mediaPlayer.play();
        }
    }



    @FXML
    public void initialize() {
        new File("voice_comments").mkdirs();
        audioRecorder = new AudioRecorder();
    }


}
