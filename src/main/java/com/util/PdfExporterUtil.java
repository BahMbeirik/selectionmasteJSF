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
import java.util.Map.Entry;

public class PdfExporterUtil {

	public static void exportAllToPDF(Map<String, Map<String, Map<String, List<Candidat>>>> candidatsParParcoursEtEtablissement, String fileName) {
	    try {
	        Document document = new Document(PageSize.A3);
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        PdfWriter.getInstance(document, baos);
	        document.open();

	        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
	        Paragraph title = new Paragraph("Liste de Selection de master", titleFont);
	        title.setAlignment(Element.ALIGN_CENTER);
	        document.add(title);

	        // Iterate through parcours
	        for (Map.Entry<String, Map<String, Map<String, List<Candidat>>>> parcoursEntry : candidatsParParcoursEtEtablissement.entrySet()) {
	            String parcours = parcoursEntry.getKey();
	            Map<String, Map<String, List<Candidat>>> etablissementMap = parcoursEntry.getValue();

	            Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
	            Paragraph subtitle = new Paragraph("Parcours: " + parcours, subtitleFont);
	            subtitle.setSpacingBefore(20);
	            subtitle.setSpacingAfter(10);
	            document.add(subtitle);

	            // Iterate through établissements and specialities
	            for (Map.Entry<String, Map<String, List<Candidat>>> etablissementEntry : etablissementMap.entrySet()) {
	                String etablissement = etablissementEntry.getKey();
	                Map<String, List<Candidat>> specialitesMap = etablissementEntry.getValue();

	                Font etablissementFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
	                Paragraph etablissementParagraph = new Paragraph("Établissement: " + etablissement, etablissementFont);
	                etablissementParagraph.setSpacingBefore(10);
	                etablissementParagraph.setSpacingAfter(5);
	                document.add(etablissementParagraph);

	                // Create a table for candidates
	                PdfPTable table = new PdfPTable(18);
	                table.setWidthPercentage(100);
	                
	             // Définir les largeurs relatives des colonnes
	                float[] columnWidths = new float[]{3, 2, 1.5f, 1.5f, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 1.5f, 1.5f};
	                try {
	                    table.setWidths(columnWidths);
	                } catch (DocumentException e) {
	                    e.printStackTrace();
	                }

	                String[] headers = {"Nom et Prénom", "N°I ou bac","Ann Bac","Ann Dip","Moy 1","Moy 2","Moy 3","Moy 4","Moy 5","Moy 6", "Bonnus", "Malus","Pénalité", "MC","Spécialité", "Étab dip", "Choix", "Décision"};
	                for (String header : headers) {
	                    PdfPCell cell = new PdfPCell(new Phrase(header, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, BaseColor.WHITE)));
	                    cell.setBackgroundColor(new BaseColor(41, 128, 185)); // Couleur bleu
	                    cell.setPaddingBottom(0.5f);
//	                    cell.setBorderWidth(0);
	                    cell.setBorderWidthBottom(0.5f);
	                    cell.setBorderWidthLeft(0);
	                    cell.setBorderWidthRight(1);
	                    cell.setBorderWidthTop(0);
	                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
	                    table.addCell(cell);
	                }

	                // Add candidates data to the table
	                for (Map.Entry<String, List<Candidat>> specialiteEntry : specialitesMap.entrySet()) {
	                    List<Candidat> candidats = specialiteEntry.getValue();
	                    for (Candidat candidat : candidats) {
	                    	addStyledCell(table, candidat.getNom() + " " + candidat.getPrenom());
	                        addStyledCell(table, candidat.getNumero_I_ou_bac());
	                        addStyledCell(table, String.valueOf(candidat.getAnneeBac()));
	                        addStyledCell(table, String.valueOf(candidat.getAnneeLicence()));
	                        addStyledCell(table, String.format("%.2f", candidat.getMoyenneReleve1()));
	                        addStyledCell(table, String.format("%.2f", candidat.getMoyenneReleve2()));
	                        addStyledCell(table, String.format("%.2f", candidat.getMoyenneReleve3()));
	                        addStyledCell(table, String.format("%.2f", candidat.getMoyenneReleve4()));
	                        addStyledCell(table, String.format("%.2f", candidat.getMoyenneReleve5()));
	                        addStyledCell(table, String.format("%.2f", candidat.getMoyenneReleve6()));
	                        addStyledCell(table, String.format("%.2f", candidat.getBonnus()));
	                        addStyledCell(table, String.format("%.2f", candidat.getMalus()));
	                        addStyledCell(table, String.format("%.2f", candidat.getPenalite()));
	                        addStyledCell(table, String.format("%.2f", candidat.getMoyenneClassement()));
	                        addStyledCell(table, candidat.getFilier());
	                        addStyledCell(table, candidat.getEtablissement().getNom());
	                        addStyledCell(table, candidat.getParcours());
	                        addStyledCell(table, candidat.getStatut());
	                    }
	                }

	                document.add(table);
	                
	            }
	            
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

	private static void addStyledCell(PdfPTable table, String content) {
        PdfPCell cell = new PdfPCell(new Phrase(content, FontFactory.getFont(FontFactory.HELVETICA, 7)));
//        cell.setPadding(3);
	     cell.setBorderWidthBottom(0.5f);
	     cell.setBorderWidthLeft(0.5f);
	     cell.setBorderWidthRight(0);
	     cell.setBorderWidthTop(0);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

}