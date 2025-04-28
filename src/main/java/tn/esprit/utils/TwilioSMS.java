package tn.esprit.utils;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import java.util.Random;

public class TwilioSMS {

    // Remplace ces valeurs par tes vraies identifiants Twilio
    public static final String ACCOUNT_SID = "";
    public static final String AUTH_TOKEN = "";
    public static final String TWILIO_PHONE_NUMBER = ""; // Ton numéro Twilio

    static {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    // Méthode pour envoyer le SMS
    public static void sendSMS(String to, String messageBody) {
        // Vérification et formatage du numéro de téléphone
        String formattedTo = formatPhoneNumber(to);

        if (formattedTo != null) {
            Message message = Message.creator(
                    new PhoneNumber(formattedTo), // Numéro destinataire
                    new PhoneNumber(TWILIO_PHONE_NUMBER), // Numéro Twilio
                    messageBody // Contenu du message
            ).create();

            System.out.println("SMS envoyé avec SID : " + message.getSid());
        } else {
            System.out.println("Le numéro de téléphone est invalide.");
        }
    }

    // Méthode pour générer un code de vérification à 6 chiffres
    public static String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // Génère un code à 6 chiffres
        return String.valueOf(code);
    }

    // Méthode pour formater le numéro de téléphone en E.164 (ex : +11234567890)
    private static String formatPhoneNumber(String phoneNumber) {
        // Vérifie que le numéro commence par "+"
        if (phoneNumber != null && phoneNumber.startsWith("+")) {
            return phoneNumber; // Le numéro est déjà formaté correctement
        } else {
            System.out.println("Numéro de téléphone invalide. Veuillez inclure le code pays.");
            return null; // Retourne null si le format est incorrect
        }
    }
}
