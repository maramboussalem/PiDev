package tn.esprit.services;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfo;
import tn.esprit.entities.Utilisateur;

import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class GoogleSignInService {

    public Utilisateur getUserInfoFromCode(String code) {
        try {
            String decodedCode = URLDecoder.decode(code, StandardCharsets.UTF_8);

            var httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            var jsonFactory = JacksonFactory.getDefaultInstance();

            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory,
                    new InputStreamReader(GoogleSignInService.class.getResourceAsStream("/client_secret.json")));

            GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                    httpTransport,
                    jsonFactory,
                    "https://oauth2.googleapis.com/token",
                    clientSecrets.getDetails().getClientId(),
                    clientSecrets.getDetails().getClientSecret(),
                    decodedCode,
                    "http://localhost"
            ).execute();

            Credential credential = new GoogleCredential.Builder()
                    .setTransport(httpTransport)
                    .setJsonFactory(jsonFactory)
                    .setClientSecrets(clientSecrets)
                    .build()
                    .setFromTokenResponse(tokenResponse);

            Oauth2 oauth2 = new Oauth2.Builder(httpTransport, jsonFactory, credential)
                    .setApplicationName("JavaFX Google Login")
                    .build();

            Userinfo userInfo = oauth2.userinfo().get().execute();

            // Création d'un objet Utilisateur
            Utilisateur user = new Utilisateur();
            user.setNom(userInfo.getName());
            user.setPrenom(userInfo.getFamilyName() != null ? userInfo.getFamilyName() : "Nom non renseigné");
            user.setEmail(userInfo.getEmail());
            user.setRole("patient");
            user.setRoles(Arrays.asList("patient"));
            user.setImg_url("1743611899369_3.jpg");
            user.setIs_active(true);
            user.setMotdepasse("google"); // Mot de passe fictif
            user.setAdresse("Non Remplie");
            user.setVerification_code("1233");
            user.setActivation_token("aaaaaa");
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
