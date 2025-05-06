package tn.esprit.utils;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

public class NewsletterService {
    private static final String API_KEY = "";
    private static final String LIST_ID = "";
    private static final String DATA_CENTER = ""; // Example: us21

    public static String subscribe(String email) {
        try {
            URL url = new URL("https://" + DATA_CENTER + ".api.mailchimp.com/3.0/lists/" + LIST_ID + "/members/");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString(("anystring:" + API_KEY).getBytes()));
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String jsonInputString = "{"
                    + "\"email_address\":\"" + email + "\","
                    + "\"status\":\"subscribed\""
                    + "}";

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                return "SUCCESS";
            } else {
                // Read error response
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "utf-8"));
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                if (response.toString().contains("is already a list member")) {
                    return "ALREADY_SUBSCRIBED";
                }

                return "ERROR: " + response.toString();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "EXCEPTION: " + e.getMessage();
        }
    }
}
