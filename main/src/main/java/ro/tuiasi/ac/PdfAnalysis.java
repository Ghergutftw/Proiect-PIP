package ro.tuiasi.ac;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.LocationTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

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
