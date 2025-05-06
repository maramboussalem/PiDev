package tn.esprit.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import tn.esprit.entities.Message;
import tn.esprit.entities.Reclamation;
import tn.esprit.services.ConversationService;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ConversationController {

    @FXML
    private Label reclamationDetails;

    @FXML
    private Label clockLabel;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox messageContainer;

    @FXML
    private TextField messageField;

    @FXML
    private Button sendButton;

    private Reclamation reclamation;
    private boolean isAdmin;
    private ConversationService conversationService;
    private int conversationId;

    private final SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final SimpleDateFormat clockFormat = new SimpleDateFormat("HH:mm:ss");

    public ConversationController() {
        this.conversationService = new ConversationService();
    }

    public void setReclamation(Reclamation reclamation, boolean isAdmin) {
        this.reclamation = reclamation;
        this.isAdmin = isAdmin;
        reclamationDetails.setText("Reclamation: " + reclamation.getSujet() + " - Response: " + reclamation.getReponse());
        this.conversationId = reclamation.getId();
        loadMessages();
        startClock();
    }

    private void startClock() {
        Timeline clockTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            String currentTime = clockFormat.format(new Date());
            clockLabel.setText("Current Time: " + currentTime);
        }));
        clockTimeline.setCycleCount(Timeline.INDEFINITE);
        clockTimeline.play();
    }

    private void loadMessages() {
        try {
            List<Message> messages = conversationService.getMessagesByConversationId(conversationId);
            messageContainer.getChildren().clear();
            for (Message message : messages) {
                addMessageToUI(message);
            }
            // Scroll to the bottom after loading messages
            scrollPane.setVvalue(1.0);
        } catch (SQLException e) {
            e.printStackTrace();
            Text errorText = new Text("Error loading messages: " + e.getMessage());
            errorText.setStyle("-fx-fill: red;");
            messageContainer.getChildren().add(errorText);
        }
    }

    private void addMessageToUI(Message message) {
        String formattedTimestamp = timestampFormat.format(message.getTimestamp());
        boolean isSenderAdmin = message.getSender().equals("Admin");
        String senderPrefix = isSenderAdmin ? "[Admin] " : "[Patient] ";

        // Create message content
        Text messageText = new Text(senderPrefix + message.getMessage() + " [" + formattedTimestamp + "]");
        messageText.setStyle("-fx-font-size: 14;");

        // Style the message bubble
        HBox messageBox = new HBox();
        messageBox.setPadding(new Insets(8));
        messageBox.setStyle(
                "-fx-background-color: " + (isSenderAdmin ? "#00b8bb" : "#e0e0e0") + ";" +
                        "-fx-background-radius: 10;" +
                        "-fx-max-width: 500;" +
                        "-fx-padding: 10 15;"
        );
        messageBox.getChildren().add(messageText);

        // Align message based on sender
        if (isSenderAdmin) {
            messageBox.setAlignment(Pos.CENTER_RIGHT);
            messageText.setStyle(messageText.getStyle() + "-fx-fill: white;");
        } else {
            messageBox.setAlignment(Pos.CENTER_LEFT);
            messageText.setStyle(messageText.getStyle() + "-fx-fill: black;");
        }

        // Add message to container
        messageContainer.getChildren().add(messageBox);

        // Scroll to the bottom after adding a message
        scrollPane.setVvalue(1.0);
    }

    @FXML
    private void sendMessage() {
        String messageText = messageField.getText().trim();
        if (!messageText.isEmpty()) {
            String sender = isAdmin ? "Admin" : "Patient";

            // Create and save the message
            Message message = new Message();
            message.setConversationId(conversationId);
            message.setSender(sender);
            message.setMessage(messageText);
            try {
                conversationService.saveMessage(message);
                loadMessages(); // Reload messages to get the actual timestamp
            } catch (SQLException e) {
                e.printStackTrace();
                Text errorText = new Text("Error saving message: " + e.getMessage());
                errorText.setStyle("-fx-fill: red;");
                messageContainer.getChildren().add(errorText);
            }

            messageField.clear();
        }
    }
}