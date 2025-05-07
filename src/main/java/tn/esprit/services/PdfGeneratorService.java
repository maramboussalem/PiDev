package tn.esprit.services;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import com.sun.javafx.charts.Legend;
import tn.esprit.entities.Diagnostic;
import tn.esprit.entities.Utilisateur;

import java.awt.*;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class PdfGeneratorService {
    private Map<Integer, String> patientIdToName = new HashMap<>();
    private Map<Integer, String> medecinIdToName = new HashMap<>();

    public PdfGeneratorService() {
        ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();

        try {
            for (Utilisateur p : serviceUtilisateur.getPatients()) {
                String fullName = p.getNom() + " " + p.getPrenom();
                patientIdToName.put(p.getId(), fullName);
            }

            for (Utilisateur m : serviceUtilisateur.getMedecins()) {
                String fullName = m.getNom() + " " + m.getPrenom();
                medecinIdToName.put(m.getId(), fullName);
            }

        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des utilisateurs : " + e.getMessage());
        }
    }

    public void generatePdf(Diagnostic diagnostic, String filePath) {
        try {
            Document document = new Document(PageSize.A4);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // ---- Set Document Styles ----
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, Color.decode("#003366"));
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Color.decode("#003366"));
            Font contentFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.BLACK);
            Font subHeaderFont = FontFactory.getFont(FontFactory.HELVETICA, 14, Color.GRAY);
            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.WHITE);
            Font diagnosticIdFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.decode("#003366"));

            // ---- Header Section with Logo Placeholder ----
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{1, 3});
            headerTable.setSpacingAfter(20f);

            // Logo Placeholder (Replace with actual image path in production)
            Image logo = Image.getInstance("src/main/resources/images/logo.jpg");
            logo.scaleToFit(100, 100); // Resize the logo if needed
            PdfPCell logoCell = new PdfPCell(logo);
            logoCell.setBorder(Rectangle.NO_BORDER);
            logoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerTable.addCell(logoCell);


            // Title and Subtitle
            PdfPCell titleCell = new PdfPCell();
            titleCell.setBorder(Rectangle.NO_BORDER);
            titleCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            Paragraph title = new Paragraph("Bright Mind", titleFont);
            title.setAlignment(Paragraph.ALIGN_RIGHT);
            titleCell.addElement(title);
            Paragraph subTitle = new Paragraph("SYSTÈME INTELLIGENT DE GESTION HOSPITALIER", subHeaderFont);
            subTitle.setAlignment(Paragraph.ALIGN_RIGHT);
            titleCell.addElement(subTitle);
            headerTable.addCell(titleCell);

            document.add(headerTable);

            // ---- Created By and Diagnostic ID ----
            Paragraph createdBy = new Paragraph("Créé par ActUp", subHeaderFont);
            createdBy.setAlignment(Paragraph.ALIGN_CENTER);
            createdBy.setSpacingAfter(10f);
            document.add(createdBy);

            Paragraph diagnosticId = new Paragraph("Numéro de Diagnostic : " + diagnostic.getId(), diagnosticIdFont);
            diagnosticId.setAlignment(Paragraph.ALIGN_CENTER);
            diagnosticId.setSpacingAfter(20f);
            document.add(diagnosticId);

            // ---- Diagnostic Report Section ----
            Paragraph reportTitle = new Paragraph("Rapport du Diagnostic", sectionFont);
            reportTitle.setAlignment(Paragraph.ALIGN_LEFT);
            reportTitle.setSpacingBefore(20f);
            reportTitle.setSpacingAfter(15f);
            document.add(reportTitle);

            // Enhanced Table Styling
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(20f);
            table.setWidths(new float[]{1, 3});

            // Table Styling
            PdfPCell headerCellStyle = new PdfPCell();
            headerCellStyle.setBackgroundColor(Color.decode("#E6F0FA"));
            headerCellStyle.setPadding(8f);
            headerCellStyle.setBorderColor(Color.decode("#003366"));

            PdfPCell contentCellStyle = new PdfPCell();
            contentCellStyle.setPadding(8f);
            contentCellStyle.setBorderColor(Color.decode("#003366"));

            String nomPatient = patientIdToName.getOrDefault(diagnostic.getPatient_id(), "Inconnu");
            String nomMedecin = medecinIdToName.getOrDefault(diagnostic.getMedecin_id(), "Inconnu");


            // Table Content
            String[][] tableData = {
                    {"Nom", diagnostic.getName()},
                    {"Description", diagnostic.getDescription()},
                    {"Date du Diagnostic", diagnostic.getDate_diagnostic() != null ? diagnostic.getDate_diagnostic().toString() : "N/A"},
                    {"Nom Patient", "Mr " + nomPatient},
                    {"Nom Médecin", "Dr " + nomMedecin}
            };


            for (String[] row : tableData) {
                PdfPCell headerCell = new PdfPCell(new Phrase(row[0], sectionFont));
                headerCell.setBackgroundColor(Color.decode("#E6F0FA"));
                headerCell.setPadding(8f);
                headerCell.setBorderColor(Color.decode("#003366"));
                table.addCell(headerCell);

                PdfPCell contentCell = new PdfPCell(new Phrase(row[1], contentFont));
                contentCell.setPadding(8f);
                contentCell.setBorderColor(Color.decode("#003366"));
                table.addCell(contentCell);
            }

            document.add(table);

            // ---- Footer Section ----
            PdfPTable footerTable = new PdfPTable(1);
            footerTable.setWidthPercentage(100);
            footerTable.setSpacingBefore(30f);

            PdfPCell footerHeaderCell = new PdfPCell(new Phrase("Contactez-nous", sectionFont));
            footerHeaderCell.setBorder(Rectangle.NO_BORDER);
            footerHeaderCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerHeaderCell.setPaddingBottom(5f);
            footerTable.addCell(footerHeaderCell);

            PdfPCell footerContentCell = new PdfPCell(new Phrase(
                    "Adresse: 123 Rue de l'Innovation, Tunis, Tunisie\n" +
                            "Téléphone: +216 70 000 000\n" +
                            "Email: contact@brightmind.com", footerFont));
            footerContentCell.setBackgroundColor(Color.decode("#003366"));
            footerContentCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerContentCell.setPadding(15f);
            footerContentCell.setBorder(Rectangle.NO_BORDER);
            footerTable.addCell(footerContentCell);

            document.add(footerTable);

            // ---- Close document ----
            document.close();
            System.out.println("PDF généré avec succès : " + filePath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}