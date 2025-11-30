package fr.maa.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.DottedLineSeparator;
import fr.maa.models.Billet;
import fr.maa.models.Client;
import fr.maa.models.Representation;
import fr.maa.models.Spectacle;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class BilletPDFService {

    private static final Font TITLE_FONT = new Font(Font.HELVETICA, 20, Font.BOLD, Color.WHITE);
    private static final Font SUBTITLE_FONT = new Font(Font.HELVETICA, 12, Font.BOLD, new Color(33, 150, 243));
    private static final Font LABEL_FONT = new Font(Font.HELVETICA, 10, Font.BOLD, new Color(55, 55, 55));
    private static final Font VALUE_FONT = new Font(Font.HELVETICA, 12, Font.NORMAL, new Color(33, 33, 33));
    private static final Font DISCLAIMER_FONT = new Font(Font.HELVETICA, 9, Font.ITALIC, new Color(90, 90, 90));

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm", Locale.FRENCH);
    private final DecimalFormat priceFormat = new DecimalFormat("0.00 €");

    public void generateBillets(List<Billet> billets, Client client, Spectacle spectacle, Representation representation, Window owner) {
        if (billets == null || billets.isEmpty()) {
            return;
        }

        File target = chooseFile(owner, spectacle);
        if (target == null) {
            return;
        }

        try (FileOutputStream outputStream = new FileOutputStream(ensurePdfExtension(target))) {
            Document document = new Document(PageSize.A5.rotate(), 36, 36, 24, 24);
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            document.open();

            for (int i = 0; i < billets.size(); i++) {
                Billet billet = billets.get(i);
                addTicketPage(document, writer, billet, client, spectacle, representation);
                if (i < billets.size() - 1) {
                    document.newPage();
                }
            }

            document.close();
        } catch (IOException | DocumentException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur PDF", "Impossible de générer le PDF des billets : " + e.getMessage());
        }
    }

    private File chooseFile(Window owner, Spectacle spectacle) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer les billets en PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier PDF", "*.pdf"));
        if (spectacle != null && spectacle.getTitre() != null) {
            fileChooser.setInitialFileName("billets-" + spectacle.getTitre().replaceAll("[^a-zA-Z0-9-_]", "_") + ".pdf");
        }
        return fileChooser.showSaveDialog(owner);
    }

    private File ensurePdfExtension(File target) {
        if (target.getName().toLowerCase(Locale.ROOT).endsWith(".pdf")) {
            return target;
        }
        return new File(target.getParentFile(), target.getName() + ".pdf");
    }

    private void addTicketPage(Document document, PdfWriter writer, Billet billet, Client client, Spectacle spectacle, Representation representation) throws DocumentException, IOException {
        PdfPTable header = new PdfPTable(2);
        header.setWidthPercentage(100);
        header.setWidths(new float[]{2f, 1f});

        PdfPCell titleCell = new PdfPCell(new Phrase("BILLET D’ENTRÉE", TITLE_FONT));
        titleCell.setBackgroundColor(new Color(33, 150, 243));
        titleCell.setPadding(14f);
        titleCell.setBorder(Rectangle.NO_BORDER);
        titleCell.setHorizontalAlignment(Element.ALIGN_LEFT);

        PdfPCell codeCell = new PdfPCell(new Phrase(billet.getNumero(), SUBTITLE_FONT));
        codeCell.setBackgroundColor(new Color(33, 150, 243));
        codeCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        codeCell.setBorder(Rectangle.NO_BORDER);
        codeCell.setPadding(14f);

        header.addCell(titleCell);
        header.addCell(codeCell);
        document.add(header);

        document.add(Chunk.NEWLINE);

        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setSpacingBefore(6f);
        infoTable.setSpacingAfter(12f);

        infoTable.addCell(labelCell("Client", client.getPrenom() + " " + client.getNom()));
        infoTable.addCell(labelCell("Spectacle", spectacle.getTitre()));
        infoTable.addCell(labelCell("Date & heure", representation.getDateHeure().format(dateFormatter)));
        infoTable.addCell(labelCell("Salle", representation.getSalle()));
        infoTable.addCell(labelCell("Numéro", billet.getNumero()));
        infoTable.addCell(labelCell("Prix", priceFormat.format(billet.getPrix())));

        document.add(infoTable);

        Paragraph qrTitle = new Paragraph("QR Code de validation", SUBTITLE_FONT);
        qrTitle.setAlignment(Element.ALIGN_CENTER);
        document.add(qrTitle);

        Image qr = buildQrCodeImage(billet, client, spectacle, representation);
        if (qr != null) {
            qr.scaleToFit(140, 140);
            qr.setAlignment(Element.ALIGN_CENTER);
            document.add(qr);
        }

        DottedLineSeparator dottedLineSeparator = new DottedLineSeparator();
        dottedLineSeparator.setGap(4f);
        dottedLineSeparator.setLineWidth(1f);
        document.add(new Chunk(dottedLineSeparator));

        Paragraph disclaimer = new Paragraph("Non échangeable, non remboursable – Merci de présenter ce billet à l’entrée.", DISCLAIMER_FONT);
        disclaimer.setSpacingBefore(10f);
        disclaimer.setAlignment(Element.ALIGN_CENTER);
        document.add(disclaimer);

        Paragraph signature = new Paragraph("Festival Manager", SUBTITLE_FONT);
        signature.setAlignment(Element.ALIGN_CENTER);
        signature.setSpacingBefore(6f);
        document.add(signature);
    }

    private PdfPCell labelCell(String label, String value) {
        Phrase phrase = new Phrase();
        phrase.add(new Chunk(label + "\n", LABEL_FONT));
        phrase.add(new Chunk(value, VALUE_FONT));

        PdfPCell cell = new PdfPCell(phrase);
        cell.setPadding(10f);
        cell.setBorderColor(new Color(220, 220, 220));
        cell.setBackgroundColor(new Color(250, 250, 250));
        return cell;
    }

    private Image buildQrCodeImage(Billet billet, Client client, Spectacle spectacle, Representation representation) {
        try {
            String payload = "BILLET:" + billet.getNumero() +
                    "|CLIENT:" + client.getPrenom() + " " + client.getNom() +
                    "|SPECTACLE:" + spectacle.getTitre() +
                    "|DATE:" + representation.getDateHeure().format(dateFormatter) +
                    "|SALLE:" + representation.getSalle();

            BitMatrix matrix = new MultiFormatWriter().encode(payload, BarcodeFormat.QR_CODE, 280, 280);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", stream);
            return Image.getInstance(stream.toByteArray());
        } catch (Exception e) {
            showAlert(Alert.AlertType.WARNING, "QR Code", "Le QR code n’a pas pu être généré : " + e.getMessage());
            return null;
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
