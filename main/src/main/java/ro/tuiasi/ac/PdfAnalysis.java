package ro.tuiasi.ac;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.pdfbox.pdmodel.PDDocument;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import technology.tabula.*;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;

public class PdfAnalysis {

    public static String extractTablesToJson(String pdfPath, String jsonOutputPath) {
        try {
            // Load the PDF document
            PDDocument document = PDDocument.load(new File(pdfPath));

            // Extract tables using Tabula
            ObjectExtractor extractor = new ObjectExtractor(document);
            SpreadsheetExtractionAlgorithm sea = new SpreadsheetExtractionAlgorithm();

            List<Table> tables = new ArrayList<>();
            for (PageIterator it = extractor.extract(); it.hasNext(); ) {
                Page page = it.next();
                tables.addAll(sea.extract(page));  // Explicitly call extract(Page)
            }

            document.close();

            // Convert tables to JSON
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

            List<List<Map<String, String>>> jsonTables = new ArrayList<>();
            for (Table table : tables) {
                jsonTables.add(tableToJson(table));
            }

            // Save JSON output
            objectMapper.writeValue(new FileWriter(jsonOutputPath), jsonTables);
            return "JSON saved to: " + jsonOutputPath;

        } catch (IOException e) {
            e.printStackTrace();
            return "Error processing PDF: " + e.getMessage();
        }
    }

    private static List<Map<String, String>> tableToJson(Table table) {
        List<List<RectangularTextContainer>> rows = table.getRows();
        if (rows.isEmpty()) return new ArrayList<>();

        // Extract headers
        List<String> headers = new ArrayList<>();
        for (RectangularTextContainer cell : rows.get(0)) {
            headers.add(cell.getText());
        }

        // Extract row data
        List<Map<String, String>> rowData = new ArrayList<>();
        for (int i = 1; i < rows.size(); i++) {  // Skip header row
            List<RectangularTextContainer> row = rows.get(i);
            Map<String, String> rowMap = new HashMap<>();

            for (int j = 0; j < headers.size(); j++) {
                rowMap.put(headers.get(j), j < row.size() ? row.get(j).getText() : "");
            }
            rowData.add(rowMap);
        }

        return rowData;
    }
}
