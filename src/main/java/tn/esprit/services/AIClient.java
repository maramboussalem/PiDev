package tn.esprit.services;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;

public class AIClient {
    private static final String API_KEY = "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"; // Replace with your key
    private static final String URL = "https://openrouter.ai/api/v1/chat/completions";
    private static final String MODEL = "mistralai/mistral-7b-instruct"; // Free & fast model

    /**
     * Generates a structured prompt for diagnostics or general queries.
     *
     * @param task        The task (e.g., "diagnose", "explain", "summarize").
     * @param input       The main input (e.g., symptoms, question, or data).
     * @param context     Additional context (e.g., patient age, medical history).
     * @param outputFormat Desired output format (e.g., "JSON", "text", "list").
     * @return A formatted prompt string.
     */
    public static String generatePrompt(String task, String input, String context, String outputFormat) {
        StringBuilder prompt = new StringBuilder();

        // Define the role and task
        prompt.append("You are an expert medical diagnostic assistant. Your role is to analyze patient vital signs and provide a clear, concise diagnosis. ");

        // Add context if provided
        if (context != null && !context.isEmpty()) {
            prompt.append("Context: ").append(context).append(". ");
        }

        // Add the main input
        prompt.append("Task: ").append(task).append(" the following vital signs: ").append(input).append(". ");

        // Specify output format
        prompt.append("Provide the response in the following format: ");
        switch (outputFormat.toLowerCase()) {
            case "json":
                prompt.append("a JSON object with two fields: 'pathologie' (the diagnosed condition) and 'diagnostic' (a detailed explanation and recommendations). ");
                prompt.append("Example: For input 'Vital signs: Pulse Rate: 50 BPM, SpO2: 80%, Systolic BP: 120 mmHg' and context 'Patient name: John Doe', ");
                prompt.append("return: {\"pathologie\": \"Bradycardia and Hypoxemia\", \"diagnostic\": \"Low pulse rate (50 BPM) and low oxygen saturation (80%) detected. Immediate medical evaluation recommended.\"}");
                break;
            case "list":
                prompt.append("a bullet-point list with 'Pathologie' and 'Diagnostic'. ");
                prompt.append("Example: \n- Pathologie: Bradycardia\n- Diagnostic: Low pulse rate detected, consult a cardiologist.");
                break;
            default:
                prompt.append("plain text with 'Pathologie: [condition]' and 'Diagnostic: [details]' separated by a newline. ");
                prompt.append("Example: Pathologie: Bradycardia\nDiagnostic: Low pulse rate detected, consult a cardiologist.");
        }

        // Add general instructions
        prompt.append("\nEnsure the response is professional, concise, and based only on the provided data. ");
        prompt.append("If the data is insufficient for a diagnosis, state 'Pathologie: Inconnu' and provide a recommendation to consult a healthcare professional.");

        return prompt.toString();
    }

    /**
     * Sends a prompt to the AI model and retrieves the response.
     *
     * @param prompt The prompt to send to the AI.
     * @return The AI's response as a string.
     * @throws IOException If an error occurs during the API call.
     */
    public static String getDiagnostic(String prompt) throws IOException {
        OkHttpClient client = new OkHttpClient();

        // Create the message payload
        JSONObject message = new JSONObject()
                .put("role", "user")
                .put("content", prompt);

        JSONObject body = new JSONObject()
                .put("model", MODEL)
                .put("messages", new JSONArray().put(message));

        // Build the request
        Request request = new Request.Builder()
                .url(URL)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(body.toString(), MediaType.get("application/json")))
                .build();

        // Execute the request
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("API error: " + response.code() + " - " + response.message());
            }

            String responseBody = response.body().string();
            JSONObject result = new JSONObject(responseBody);
            return result.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");
        } catch (Exception e) {
            throw new IOException("Failed to process API request: " + e.getMessage(), e);
        }
    }

    /**
     * Example usage of the AIClient.
     */
    public static void main(String[] args) {
        try {
            // Example: Generate a diagnostic prompt
            String task = "diagnose";
            String input = "fever, cough, shortness of breath";
            String context = "40-year-old female, history of asthma";
            String outputFormat = "json";

            String prompt = generatePrompt(task, input, context, outputFormat);
            System.out.println("Generated Prompt: " + prompt);

            // Get the AI response
            String response = getDiagnostic(prompt);
            System.out.println("AI Response: " + response);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}