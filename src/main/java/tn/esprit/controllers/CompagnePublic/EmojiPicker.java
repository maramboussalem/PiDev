package tn.esprit.controllers.CompagnePublic;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.util.List;
import javafx.scene.control.TextArea;
import tn.esprit.utils.EmojiService;

public class EmojiPicker {

    private Stage popupStage;
    private TextField targetTextArea;

    public EmojiPicker(TextField targetTextArea) {
        this.targetTextArea = targetTextArea;
        initialize();
    }

    private void initialize() {
        popupStage = new Stage();
        popupStage.initStyle(StageStyle.UTILITY);
        popupStage.setTitle("Select an Emoji");

        FlowPane emojiPane = new FlowPane();
        emojiPane.setPadding(new Insets(10));
        emojiPane.setHgap(10);
        emojiPane.setVgap(10);

        List<String> emojis = EmojiService.fetchEmojis();
        for (String emojiHtml : emojis) {
            Button emojiButton = new Button(htmlToUnicode(emojiHtml));
            emojiButton.setStyle("-fx-font-size: 24px; -fx-background-color: transparent;");
            emojiButton.setOnAction(e -> {
                targetTextArea.appendText(emojiButton.getText());
                popupStage.close();
            });
            emojiPane.getChildren().add(emojiButton);
        }

        Scene scene = new Scene(emojiPane, 400, 300);
        popupStage.setScene(scene);
    }

    public void show() {
        popupStage.show();
    }

    // Helper to convert HTML code like "&#128512;" into real emoji
    private String htmlToUnicode(String htmlCode) {
        try {
            int codePoint = Integer.parseInt(htmlCode.replaceAll("&#|;", ""));
            return new String(Character.toChars(codePoint));
        } catch (Exception e) {
            e.printStackTrace();
            return "?";
        }
    }
}
