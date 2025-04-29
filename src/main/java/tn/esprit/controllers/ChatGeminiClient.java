package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ChatGeminiClient {

    @FXML
    private TextArea inputArea;

    @FXML
    private TextArea outputArea;

    @FXML
    private Button sendButton;

    private final String API_KEY = "AIzaSyBn5EK6DvEJiu2R8N2oCg1C8VaoXE8iEoQ"; // ⚠️ Remplacer avec ta vraie clé API
    private final String API_URL =  "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=AIzaSyBn5EK6DvEJiu2R8N2oCg1C8VaoXE8iEoQ" ;

    @FXML
    private void handleSendAction() {
        String question = inputArea.getText();
        if (question.isEmpty()) {
            outputArea.setText("Please enter your question first.");
            return;
        }
        String response = askGemini(question);
        outputArea.setText(response);
    }

    private String askGemini(String question) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost postRequest = new HttpPost(API_URL);

            postRequest.addHeader("Content-Type", "application/json");

            // Construire correctement le body JSON attendu
            JSONObject part = new JSONObject();
            part.put("text", question);

            JSONArray partsArray = new JSONArray();
            partsArray.put(part);

            JSONObject content = new JSONObject();
            content.put("parts", partsArray);

            JSONArray contentsArray = new JSONArray();
            contentsArray.put(content);

            JSONObject requestBody = new JSONObject();
            requestBody.put("contents", contentsArray);

            StringEntity requestEntity = new StringEntity(requestBody.toString(), "UTF-8");
            postRequest.setEntity(requestEntity);

            HttpResponse response = httpClient.execute(postRequest);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                StringBuilder errorBuilder = new StringBuilder();
                String line;
                while ((line = errorReader.readLine()) != null) {
                    errorBuilder.append(line);
                }
                return "HTTP error code: " + statusCode + "\n" + errorBuilder.toString();
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line);
            }

            JSONObject jsonResponse = new JSONObject(responseBuilder.toString());

            String reply = jsonResponse
                    .getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text");

            return reply.trim();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error fetching response from Gemini.";
        }
    }

}
