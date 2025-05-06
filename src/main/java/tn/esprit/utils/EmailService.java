package tn.esprit.utils;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import tn.esprit.entities.Utilisateur;

import java.util.List;

public class EmailService {

    public static void sendPostCreatedToMultiple(List<Utilisateur> recipient, String postTitle) {
        for (Utilisateur email : recipient) {
            sendPostCreatedEmail(email.getEmail(), postTitle);
        }
    }

    public static void sendPostCreatedEmail(String recipientEmail, String postTitle) {
        try {
            HtmlEmail email = new HtmlEmail();
            email.setHostName("smtp.gmail.com");
            email.setSmtpPort(587);
            email.setAuthentication("", "");
            email.setStartTLSEnabled(true);

            email.setFrom("agrebaouiyoussef123@gmail.com", "Sigh"); // Optional sender name
            email.setSubject("🚀 New Post Created!");

            String htmlMessage = "<!DOCTYPE html>" +
                    "<html lang='en'>" +
                    "<head>" +
                    "  <meta charset='UTF-8'>" +
                    "  <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                    "  <style>" +
                    "    body { margin: 0; padding: 20px; background-color: #f2f2f2; font-family: Arial, sans-serif; }" +
                    "    .container { background-color: #ffffff; margin: 0 auto; padding: 30px 20px; border-radius: 12px; max-width: 600px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); }" +
                    "    .header { background-color: #4CAF50; color: white; text-align: center; padding: 30px 20px; border-top-left-radius: 12px; border-top-right-radius: 12px; }" +
                    "    .header h1 { margin: 10px 0 0 0; font-size: 24px; }" +
                    "    .content { padding: 20px; text-align: left; color: #333333; font-size: 16px; line-height: 1.6; }" +
                    "    .button { display: inline-block; background-color: #28a745; color: white; padding: 12px 24px; margin: 20px 0; text-decoration: none; font-weight: bold; border-radius: 6px; }" +
                    "    .footer { font-size: 13px; color: #777777; text-align: center; margin-top: 30px; }" +
                    "    .separator { border-top: 2px solid #4CAF50; margin: 30px 0; }" +
                    "    a { color: #28a745; text-decoration: none; }" +
                    "  </style>" +
                    "</head>" +
                    "<body>" +
                    "  <div class='container'>" +
                    "    <div class='header'>" +
                    "      <h1>New Post Published 🎉</h1>" +
                    "    </div>" +
                    "    <div class='content'>" +
                    "      <p>Hello,</p>" +
                    "      <p>A new post titled <strong style='color: #2196F3;'>" + postTitle + "</strong> has been created.</p>" +
                    "      <div style='text-align:center;'>" +
                    "        <a href='#' class='button'>View Post</a>" +
                    "      </div>" +
                    "      <p>Visit our platform to check it out!</p>" +
                    "      <div class='separator'></div>" +
                    "      <p style='font-size: 14px;'>This is an automated message from <strong>Your App Name</strong>.</p>" +
                    "    </div>" +
                    "    <div class='footer'>" +
                    "      <p>© 2025 Sigh. All rights reserved.</p>" +
                    "    </div>" +
                    "  </div>" +
                    "</body>" +
                    "</html>";


            email.setHtmlMsg(htmlMessage);


            email.setTextMsg("A new post '" + postTitle + "' has been created. Check it on our platform!");

            email.addTo(recipientEmail);

            email.send();


        } catch (EmailException e) {
            System.out.println("Failed to send email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

