package com.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.models.Candidat;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public class PdfExporterUtil {

    public static void exportAllToPDF(Map<String, List<Candidat>> candidatsParParcours, String fileName) {
        try {
            Document document = new Document();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Liste de Selection de master", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            for (Map.Entry<String, List<Candidat>> entry : candidatsParParcours.entrySet()) {
                String parcours = entry.getKey();
                List<Candidat> candidats = entry.getValue();

                Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
                Paragraph subtitle = new Paragraph("Parcours: " + parcours, subtitleFont);
                subtitle.setSpacingBefore(20);
                subtitle.setSpacingAfter(10);
                document.add(subtitle);

                PdfPTable table = new PdfPTable(8);
                table.setWidthPercentage(100);

                String[] headers = {"Nom", "Prénom", "Parcours", "Établissement", "Bonnus", "Malus", "Moyenne Classement", "Statut"};
                for (String header : headers) {
                    PdfPCell cell = new PdfPCell(new Phrase(header));
                    cell.setBackgroundColor(new BaseColor(200, 200, 200));
                    table.addCell(cell);
                }

                for (Candidat candidat : candidats) {
                    table.addCell(candidat.getNom());
                    table.addCell(candidat.getPrenom());
                    table.addCell(candidat.getParcours());
                    table.addCell(candidat.getEtablissement().getNom());
                    table.addCell(String.valueOf(candidat.getBonnus()));
                    table.addCell(String.valueOf(candidat.getMalus()));
                    table.addCell(String.format("%.2f", candidat.getMoyenneClassement()));
                    table.addCell(candidat.getStatut());
                }

                document.add(table);
            }

            document.close();

            FacesContext facesContext = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

            try (OutputStream os = response.getOutputStream()) {
                baos.writeTo(os);
                os.flush();
            }

            facesContext.responseComplete();
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }
}