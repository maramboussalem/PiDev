package tn.esprit.services;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import tn.esprit.entities.Utilisateur;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Scanner;

public class GitHubSignInService {

    public Utilisateur getUserInfoFromCode(String code) {
        try {
            String clientId = "Ov23lilS83pZi0D8YYOE";
            String clientSecret = "421b99308dd041a90699bcf885100175da1a63a2";

            // Échange du code contre un token
            URL url = new URL("https://github.com/login/oauth/access_token");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            String params = "client_id=" + clientId + "&client_secret=" + clientSecret + "&code=" + code;
            try (OutputStream os = conn.getOutputStream()) {
                os.write(params.getBytes());
            }

            Scanner scanner = new Scanner(conn.getInputStream());
            String responseBody = scanner.useDelimiter("\\A").next();
            scanner.close();

            JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();
            String accessToken = json.get("access_token").getAsString();

            // Récupération des infos utilisateur GitHub
            URL userApiUrl = new URL("https://api.github.com/user");
            HttpURLConnection userConn = (HttpURLConnection) userApiUrl.openConnection();
            userConn.setRequestProperty("Authorization", "token " + accessToken);
            userConn.setRequestProperty("Accept", "application/json");

            Scanner userScanner = new Scanner(userConn.getInputStream());
            String userInfoStr = userScanner.useDelimiter("\\A").next();
            userScanner.close();

            JsonObject userJson = JsonParser.parseString(userInfoStr).getAsJsonObject();

            // Récupérer aussi l'email
            URL emailApiUrl = new URL("https://api.github.com/user/emails");
            HttpURLConnection emailConn = (HttpURLConnection) emailApiUrl.openConnection();
            emailConn.setRequestProperty("Authorization", "token " + accessToken);
            emailConn.setRequestProperty("Accept", "application/json");

            Scanner emailScanner = new Scanner(emailConn.getInputStream());
            String emailInfoStr = emailScanner.useDelimiter("\\A").next();
            emailScanner.close();

            String email = JsonParser.parseString(emailInfoStr).getAsJsonArray()
                    .get(0).getAsJsonObject()
                    .get("email").getAsString();

            Utilisateur user = new Utilisateur();
            user.setNom(userJson.get("login").getAsString());
            user.setPrenom("GitHub User");
            user.setEmail(email);
            user.setRole("patient");
            user.setRoles(Arrays.asList("patient"));
            user.setImg_url("default.jpg");
            user.setIs_active(true);
            user.setMotdepasse("github");
            user.setAdresse("Non Remplie");
            user.setVerification_code("0000");
            user.setActivation_token("github_token");
            user.setAntecedents_medicaux("Non Remplie");
            user.setHopital("Non Remplie");
            user.setSexe("Non Remplie");
            user.setSpecialite("Non Remplie");
            user.setTelephone("+21655388787");
            user.setCaptcha(null);

            return user;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
