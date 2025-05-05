package ro.tuiasi.ac;
import java.io.File;
import java.io.FileInputStream;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.NotNull;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Utility class for analyzing PDF and Excel files.
 * <p>
 * Contains methods to read and extract text from PDF documents
 * and convert Excel rows into structured JSON format for analysis.
 */
public class PdfAnalysis {

    /**
     * Reads a PDF file and prints the text content of each page to the console.
     *
     * @param file the PDF file to be read.
     * @throws IOException if the file cannot be read.
     */
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

    /**
     * Reads an Excel (.xlsx) file and converts its content into a JSON object.
     * Expects each row to contain:
     * - Column 0: Name of analysis ("denumireAnaliza")
     * - Column 1: Reference interval ("intervalReferinta")
     * - Column 2: Result ("rezultat")
     *
     * @param file the Excel file to read.
     * @return a {@link JSONObject} containing an array of parsed analysis rows under the key "results".
     * @throws IOException if the file cannot be read.
     */
    public static JSONObject excelReader(File file) throws IOException {
        JSONObject finalResult = new JSONObject();
        JSONArray resultsArray = new JSONArray();

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {

            for (Sheet sheet : workbook) {
                boolean isFirstRow = true;

                for (Row row : sheet) {
                    if (isFirstRow) {
                        isFirstRow = false;
                        continue;
                    }

                    JSONObject analiza = getJsonObject(row);

                    if (!analiza.isEmpty()) {
                        resultsArray.put(analiza);
                    }
                }
            }
        }

        finalResult.put("results", resultsArray);
        return finalResult;
    }

    /**
     * Converts a single Excel row into a JSON object with keys:
     * - "denumireAnaliza"
     * - "intervalReferinta"
     * - "rezultat"
     *
     * @param row the Excel row to convert.
     * @return a {@link JSONObject} containing the structured data, or empty if row is invalid.
     */
    private static @NotNull JSONObject getJsonObject(Row row) {
        JSONObject analiza = new JSONObject();
        int cellIndex = 0;

        for (Cell cell : row) {
            switch (cellIndex) {
                case 0 -> analiza.put("denumireAnaliza", cell.getStringCellValue());
                case 1 -> analiza.put("intervalReferinta", cell.getStringCellValue());
                case 2 -> {
                    if (cell.getCellType() == CellType.NUMERIC) {
                        analiza.put("rezultat", cell.getNumericCellValue());
                    } else {
                        analiza.put("rezultat", cell.getStringCellValue());
                    }
                }
            }
            cellIndex++;
        }
        return analiza;
    }
}
