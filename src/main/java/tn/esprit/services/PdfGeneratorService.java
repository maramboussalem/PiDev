package tn.esprit.services;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import tn.esprit.entities.Diagnostic;

import java.awt.*;
import java.io.FileOutputStream;

public class PdfGeneratorService {

    public void generatePdf(Diagnostic diagnostic, String filePath) {
        try {
            Document document = new Document(PageSize.A4);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // ---- Set Document Styles ----
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, Color.decode("#003366"));
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.decode("#003366"));
            Font contentFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.BLACK);
            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.WHITE);

            // ---- Title Section ----
            Paragraph title = new Paragraph("Bright Mind", titleFont);
            title.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(title);

            Paragraph subTitle = new Paragraph("SYSTÈME INTELLIGENT DE GESTION HOSPITALIER", FontFactory.getFont(FontFactory.HELVETICA, 14, Color.GRAY));
            subTitle.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(subTitle);

            Paragraph createdBy = new Paragraph("Créé par ActUp", FontFactory.getFont(FontFactory.HELVETICA, 12, Color.GRAY));
            createdBy.setAlignment(Paragraph.ALIGN_CENTER);
            createdBy.setSpacingAfter(20f);
            document.add(createdBy);

            // ---- Diagnostic Report Section ----
            Paragraph reportTitle = new Paragraph("Rapport du Diagnostic", sectionFont);
            reportTitle.setAlignment(Paragraph.ALIGN_CENTER);
            reportTitle.setSpacingBefore(30f);
            document.add(reportTitle);

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(20f);
            table.setWidths(new float[]{1, 3});

            // Table Headers
            table.addCell(new PdfPCell(new Phrase("ID", sectionFont)));
            table.addCell(new PdfPCell(new Phrase(String.valueOf(diagnostic.getId()), contentFont)));

            table.addCell(new PdfPCell(new Phrase("Nom", sectionFont)));
            table.addCell(new PdfPCell(new Phrase(diagnostic.getName(), contentFont)));

            table.addCell(new PdfPCell(new Phrase("Description", sectionFont)));
            table.addCell(new PdfPCell(new Phrase(diagnostic.getDescription(), contentFont)));

            table.addCell(new PdfPCell(new Phrase("Date du Diagnostic", sectionFont)));
            table.addCell(new PdfPCell(new Phrase(diagnostic.getDate_diagnostic() != null ? diagnostic.getDate_diagnostic().toString() : "N/A", contentFont)));

            table.addCell(new PdfPCell(new Phrase("ID Patient", sectionFont)));
            table.addCell(new PdfPCell(new Phrase(String.valueOf(diagnostic.getPatient_id()), contentFont)));

            table.addCell(new PdfPCell(new Phrase("ID Médecin", sectionFont)));
            table.addCell(new PdfPCell(new Phrase(String.valueOf(diagnostic.getMedecin_id()), contentFont)));

            document.add(table);

            // ---- Footer Section ----
            PdfPTable footerTable = new PdfPTable(1);
            footerTable.setWidthPercentage(100);
            footerTable.setSpacingBefore(20f);
            footerTable.setSpacingAfter(10f);
            footerTable.addCell(new PdfPCell(new Phrase("Contactez-nous", sectionFont)));

            PdfPCell footerCell = new PdfPCell(new Phrase(
                    "Adresse: 123 Rue de l'Innovation, Tunis, Tunisie\n" +
                            "Téléphone: +216 70 000 000\n" +
                            "Email: contact@brightmind.com", footerFont));
            footerCell.setBackgroundColor(Color.decode("#003366"));
            footerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerCell.setPaddingTop(10f);
            footerCell.setPaddingBottom(10f);
            footerCell.setBorder(Rectangle.NO_BORDER);
            footerTable.addCell(footerCell);

            document.add(footerTable);

            // ---- Close document ----
            document.close();
            System.out.println("PDF généré avec succès : " + filePath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
