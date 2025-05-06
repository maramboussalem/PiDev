package tn.esprit.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class EmojiService {

    private static final String API_URL = "";

    public static List<String> fetchEmojis() {
        List<String> emojis = new ArrayList<>();
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONArray array = new JSONArray(response.toString());
            for (int i = 0; i < array.length(); i++) {
                JSONObject emoji = array.getJSONObject(i);
                JSONArray htmlCodes = emoji.getJSONArray("htmlCode");
                emojis.add(htmlCodes.getString(0)); // Add the first htmlCode
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return emojis;
    }
}
