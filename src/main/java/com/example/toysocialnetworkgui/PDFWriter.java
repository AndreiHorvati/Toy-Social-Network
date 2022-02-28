package com.example.toysocialnetworkgui;

import java.io.IOException;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class PDFWriter {
    String fileName;

    public PDFWriter(String fileName) {
        this.fileName = fileName;
    }

    public void writeToFile(List<String> content) {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();

        document.addPage(page);

        try {
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            contentStream.setFont(PDType1Font.COURIER, 12);
            contentStream.setLeading(14.5f);
            contentStream.beginText();
            contentStream.newLineAtOffset(25, 700);

            content.forEach(string -> {
                try {
                    writeLineToFile(contentStream, string);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            contentStream.endText();
            contentStream.close();

            document.save(fileName + "/report" + ".pdf");
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeLineToFile(PDPageContentStream contentStream, String string) throws IOException {
        contentStream.showText(string);
        contentStream.newLine();
    }
}
