package ro.tuiasi.ac;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.LocationTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import org.apache.pdfbox.pdmodel.PDDocument;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.apache.pdfbox.text.PDFTextStripper;
import technology.tabula.*;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;

public class PdfAnalysis {

    public static void pdfReader(File file) throws IOException {
        PdfReader reader = new PdfReader(String.valueOf(file));
        int pages = reader.getNumberOfPages();
        for (int i = 1; i <= pages; i++) {
            LocationTextExtractionStrategy strategy = new LocationTextExtractionStrategy();
            String pageText = PdfTextExtractor.getTextFromPage(reader, i, strategy);
            System.out.println("Page " + i + ":\n" + pageText);
        }

        reader.close();
    }
}
