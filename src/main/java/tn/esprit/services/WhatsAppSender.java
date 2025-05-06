package tn.esprit.services;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import tn.esprit.entities.Utilisateur;

public class WhatsAppSender {

    // Your Twilio Account SID and Auth Token
    public static final String ACCOUNT_SID = "";
    public static final String AUTH_TOKEN = "";

    public static void sendWhatsAppMessage(Utilisateur utilisateur, String messageBody) {
        // Initialize Twilio
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        // Get phone number and add country code
        String toPhoneNumber = utilisateur.getTelephone();

        if (toPhoneNumber != null && !toPhoneNumber.isBlank()) {
            // Add +216 if missing
            if (!toPhoneNumber.startsWith("+216")) {
                toPhoneNumber = "+216" + toPhoneNumber;
            }
        } else {
            System.out.println("Phone number is invalid.");
            return;
        }

        // Send WhatsApp message
        Message message = Message.creator(
                new PhoneNumber("whatsapp:" + toPhoneNumber), // Full international number
                new PhoneNumber("whatsapp:+14155238886"),      // Your Twilio WhatsApp number
                messageBody
        ).create();

        System.out.println("Message sent! SID: " + message.getSid());
    }


}
