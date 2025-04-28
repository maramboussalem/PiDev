package tn.esprit.utils;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmailSender {
    private static final String SMTP_HOST = "longevityplus.store";
    private static final int SMTP_PORT = 465;
    private static final String SMTP_USERNAME = "ranine@longevityplus.store";
    private static final String SMTP_PASSWORD = "RanineRanine123";

    private static final Logger logger = Logger.getLogger(EmailSender.class.getName());

    public static boolean sendInvoiceEmail(String recipientEmail, String clientName,
                                           double totalAmount, File invoicePdf) {
        try {
            // Configure properties
            Properties props = new Properties();
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.socketFactory.port", SMTP_PORT);
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.debug", "true");

            // Create session
            Session session = Session.getInstance(props,
                    new Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(SMTP_USERNAME, SMTP_PASSWORD);
                        }
                    });

            // Create message
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SMTP_USERNAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Votre facture Bright Mind");

            // Create multipart message
            Multipart multipart = new MimeMultipart();

            // HTML content
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(buildInvoiceEmailContent(clientName, totalAmount),
                    "text/html; charset=utf-8");
            multipart.addBodyPart(htmlPart);

            // Add PDF invoice attachment
            if (invoicePdf != null && invoicePdf.exists()) {
                MimeBodyPart attachmentPart = new MimeBodyPart();
                attachmentPart.attachFile(invoicePdf);
                attachmentPart.setFileName("Facture_Bright Mind.pdf");
                multipart.addBodyPart(attachmentPart);
            }

            message.setContent(multipart);

            // Send email
            Transport.send(message);
            logger.info("Email with invoice sent to " + recipientEmail);

            return true;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to send email with invoice", e);
            return false;
        } finally {
            // Clean up attachment file
            if (invoicePdf != null && invoicePdf.exists()) {
                boolean deleted = invoicePdf.delete();
                if (!deleted) {
                    logger.warning("Failed to delete temporary invoice file: " + invoicePdf.getPath());
                }
            }
        }
    }

    private static String buildInvoiceEmailContent(String clientName, double totalAmount) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "  <meta charset='UTF-8'>" +
                "  <title>Facture Bright Mind</title>" +
                "<style>" +
                "  body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f2f4f8; color: #333; margin: 0; padding: 0; }" +
                "  .container { max-width: 650px; margin: 40px auto; background: #fff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1); }" +
                "  .header { background: linear-gradient(90deg, #00b8bb, #0097a7); padding: 30px 20px; text-align: center; color: white; }" +
                "  .header h1 { margin: 0; font-size: 26px; letter-spacing: 1px; }" +
                "  .content { padding: 30px 25px; background-color: #fafafa; }" +
                "  .content p { margin: 10px 0; font-size: 15px; line-height: 1.6; }" +
                "  .total { display: inline-block; margin-top: 15px; padding: 8px 16px; background-color: #00b8bb; color: white; font-weight: bold; border-radius: 6px; font-size: 17px; }" +
                "  .footer { text-align: center; padding: 20px; font-size: 12px; color: #999; background-color: #f0f0f0; }" +
                "</style>" +

        "</head>" +
                "<body>" +
                "  <div class='container'>" +
                "    <div class='header'>" +
                "      <h1>Merci pour votre commande</h1>" +
                "    </div>" +
                "    <div class='content'>" +
                "      <p>Bonjour " + clientName + ",</p>" +
                "      <p>Votre commande a bien été enregistrée. Vous trouverez ci-joint votre facture.</p>" +
                "      <p>Montant total : <span class='total'>" + String.format("%.2f", totalAmount) + " DT</span></p>" +
                "      <p>Pour toute question concernant votre commande, n'hésitez pas à répondre à cet email.</p>" +
                "      <p>Cordialement,<br>L'équipe Bright Mind</p>" +
                "    </div>" +
                "    <div class='footer'>" +
                "      <p>© 2025 Bright Mind. Tous droits réservés.</p>" +
                "    </div>" +
                "  </div>" +
                "</body>" +
                "</html>";
    }
}