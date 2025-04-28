package tn.esprit.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import tn.esprit.controllers.PanierController.PanierItem;
import tn.esprit.entities.Medicament;
import tn.esprit.utils.SessionPanier;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class PDFGenerator {

    public static File generateInvoice(SessionPanier panier, String clientName, String clientEmail) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            float margin = 50;
            float yStart = page.getMediaBox().getHeight() - margin;
            float tableWidth = page.getMediaBox().getWidth() - 2 * margin;
            float yPosition = yStart;
            float rowHeight = 20f;

            // Titre
            contentStream.setNonStrokingColor(0, 184, 187);
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 20);
            contentStream.beginText();
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText("FACTURE Bright Mind");
            contentStream.endText();
            yPosition -= 40;

            contentStream.setNonStrokingColor(0);
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText("Client: " + clientName);
            contentStream.endText();

            contentStream.beginText();
            contentStream.newLineAtOffset(margin, yPosition - 15);
            contentStream.showText("Email: " + clientEmail);
            contentStream.endText();

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            contentStream.beginText();
            contentStream.newLineAtOffset(tableWidth - 100, yPosition);
            contentStream.showText("Date: " + sdf.format(new Date()));
            contentStream.endText();

            yPosition -= 50;

            // Ligne de séparation
            contentStream.setStrokingColor(200);
            contentStream.moveTo(margin, yPosition);
            contentStream.lineTo(margin + tableWidth, yPosition);
            contentStream.stroke();
            yPosition -= 10;

            // En-tête tableau
            drawTableRow(contentStream, margin, yPosition, tableWidth,
                    new String[]{"Produit", "Prix Unitaire", "Quantité", "Total"},
                    PDType1Font.HELVETICA_BOLD);
            yPosition -= rowHeight;

            contentStream.setFont(PDType1Font.HELVETICA, 10);
            for (Map.Entry<Medicament, Integer> entry : panier.getItems().entrySet()) {
                Medicament medicament = entry.getKey();
                int quantite = entry.getValue();
                double total = medicament.getPrix() * quantite;

                drawTableRow(contentStream, margin, yPosition, tableWidth,
                        new String[]{
                                medicament.getNom(),
                                String.format("%.2f DT", medicament.getPrix()),
                                String.valueOf(quantite),
                                String.format("%.2f DT", total)
                        },
                        PDType1Font.HELVETICA);
                yPosition -= rowHeight;
            }

            // Total
            contentStream.setNonStrokingColor(0, 184, 187);
            drawTableRow(contentStream, margin, yPosition - 20, tableWidth,
                    new String[]{"", "", "TOTAL:", String.format("%.2f DT", panier.getTotal())},
                    PDType1Font.HELVETICA_BOLD);
            contentStream.setNonStrokingColor(0);

            // Pied de page
            contentStream.setFont(PDType1Font.HELVETICA_OBLIQUE, 10);
            contentStream.beginText();
            contentStream.newLineAtOffset(margin, 50);
            contentStream.showText("Merci pour votre confiance - Bright-Mind");
            contentStream.endText();
        }

        File tempFile = File.createTempFile("facture_", ".pdf");
        document.save(tempFile);
        document.close();

        return tempFile;
    }

    private static void drawTableRow(PDPageContentStream contentStream, float x, float y,
                                     float width, String[] texts, PDType1Font font) throws IOException {
        float colWidth = width / texts.length;
        float textY = y - 15;

        contentStream.setStrokingColor(230);
        contentStream.moveTo(x, y);
        contentStream.lineTo(x + width, y);
        contentStream.stroke();

        if (font.equals(PDType1Font.HELVETICA_BOLD)) {
            contentStream.setNonStrokingColor(0, 184, 187);
            contentStream.addRect(x, y - 20, width, 20);
            contentStream.fill();
            contentStream.setNonStrokingColor(255);
        } else {
            contentStream.setNonStrokingColor(0);
        }

        contentStream.setFont(font, 10);
        for (int i = 0; i < texts.length; i++) {
            contentStream.beginText();
            contentStream.newLineAtOffset(x + i * colWidth + 5, textY);
            contentStream.showText(texts[i]);
            contentStream.endText();
        }

        contentStream.setNonStrokingColor(0);
    }
}
