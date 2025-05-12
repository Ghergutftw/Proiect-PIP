import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.json.JSONObject;
import static org.junit.jupiter.api.Assertions.*;

import ro.tuiasi.ac.App;
import ro.tuiasi.ac.FileAnalysis;
import services.Analysis;
import services.ChatGPTService;
import services.PrepareResponse;

import java.io.FileInputStream;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for PDF and Excel analysis functionality
 */
@Nested
class ExcelAndPDFValidationTest {

    /**
     * Test case to verify that the Excel file is not empty
     *
     * @throws IOException if file operations fail
     * @testType Positive
     * @assertion The parsed JSON should contain at least one result
     * @precondition Valid non-empty Excel file exists
     * @postcondition JSON result contains data
     */
    @Test
    void testExcelFileIsNotEmpty() throws IOException {
        // Arrange
        File dataFile = new File("src/test/java/ro/tuiasi/ac/fisiereTest/analize_cu_el_null.xlsx");

        // Act
        JSONObject result = FileAnalysis.excelReader(dataFile);

        // Assert
        assertFalse(result.getJSONArray("results").isEmpty(),
                "Excel file should contain at least one analysis record");
    }

    /**
     * Tests reading Excel file with missing columns
     */

    @Test
    public void testNumarColoane() throws Exception {
        File file = new File("src/test/java/ro/tuiasi/ac/fisiereTest/analize.xlsx");
        assertTrue(file.exists(), "Fișierul Excel nu există!");

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            assertNotNull(sheet, "Foaia de calcul este null!");

            Row headerRow = sheet.getRow(0);
            assertNotNull(headerRow, "Rândul de antet este null!");

            int lastCellIndex = headerRow.getLastCellNum(); // indexul ultimei celule + 1
            assertEquals(3, lastCellIndex, "Antetul nu conține exact 3 coloane!");
        }
    }

    /**
     * Tests reading Excel file with null values
     *
     * @throws IOException if file operations fail
     */
    @Test
    public void testNoNullCellsInExcelFile() throws Exception {
        File file = new File("src/test/java/ro/tuiasi/ac/fisiereTest/analize.xlsx");
        assertTrue(file.exists(), "Fișierul Excel nu există!");

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0); // Prima foaie din Excel
            assertNotNull(sheet, "Foaia de calcul este null!");

            for (Row row : sheet) {
                AtomicInteger nr = new AtomicInteger();
                row.forEach(cell -> nr.getAndIncrement());
                boolean isEmpty = nr.get() == 3;
                assertTrue(isEmpty, "Celulă goală la rândul " + row.getRowNum());
            }
        }
    }

    /**
     * Tests reading Excel file with wrong data types
     */
    @Test
    void testFileIsXlsxExtension() {
        File file = new File("src/test/java/ro/tuiasi/ac/fisiereTest/analize.xlsx");

        assertTrue(file.exists(), "Fișierul nu există!");
        assertTrue(file.getName().toLowerCase().endsWith(".xlsx"), "Fișierul nu are extensia .xlsx!");
    }

    /**
     * Tests reading an empty PDF file
     */
    @Test
    void testPdfGol() {
        File emptyPdf = new File("src/test/java/ro/tuiasi/ac/fisiereTest/analize.pdf");
        assertDoesNotThrow(() -> FileAnalysis.pdfReader(emptyPdf));
    }

    /**
     * Testeaza ca fisierul incarcat sa fie de tip Pdf
     *
     * @throws IOException if file operations fail
     */
    @Test
    void testPdfTip() {
        File file = new File("src/test/java/ro/tuiasi/ac/fisiereTest/analize.pdf");

        assertTrue(file.exists(), "Fișierul nu există!");
        assertTrue(file.getName().toLowerCase().endsWith(".pdf"), "Fișierul nu are extensia .pdf!");
    }

}
/**
 * Test suite for ChatGPT service functionality
 */
@Nested
class ChatGPTServiceTest {

    /**
     * Tests successful connection to chat service
     */
    @Test
    void testSuccessfulChatConnection() {
        ChatGPTService service = new ChatGPTService();
        String response = service.getChatGPTResponse("Test message");
        assertNotNull(response);
    }

    /**
     * Tests sending empty message to chat
     */
    @Test
    void testSendEmptyMessage() {
        ChatGPTService service = new ChatGPTService();
        String response = service.getChatGPTResponse("");
        assertNull(response);
    }
}

/**
 * Test suite for response processing functionality
 */
@Nested
class PrepareResponseTest {

    /**
     * Tests response field validation
     * @throws JsonProcessingException if JSON parsing fails
     */
    @Test
    void testResponseFieldValidation() throws JsonProcessingException {
        String validResponse = "{\"choices\":[{\"message\":{\"content\":\"```json\\n" +
                "{\\\"results\\\":[{\\\"denumireAnaliza\\\":\\\"Test\\\",\\\"rezultat\\\":1.0," +
                "\\\"intervalReferinta\\\":\\\"0-2\\\",\\\"severityRank\\\":\\\"Low\\\"}]}\\n```\"}}]}";

        List<Analysis> analyses = PrepareResponse.processResponse(validResponse);
        Analysis analysis = analyses.get(0);
        assertEquals("Test", analysis.getDenumireAnaliza());
        assertEquals(1.0, analysis.getRezultat());
        assertEquals("0-2", analysis.getIntervalReferinta());
        assertEquals("Low", analysis.getSeveritate());
    }

    /**
     * Tests connection with chat but sending nothing
     * @throws JsonProcessingException if JSON parsing fails
     */
    @Test
    void testEmptyChatResponse() throws JsonProcessingException {
        String emptyResponse = "{\"choices\":[{\"message\":{\"content\":\"\"}}]}";
        assertThrows(IllegalStateException.class, () -> PrepareResponse.processResponse(emptyResponse));
    }
}

/**
 * Test suite for main application functionality
 */
@Nested
class appTest {

    /**
     * Tests loading empty PDF through UI
     */
    @Test
    void testUploadEmptyPdf() {
        App app = new App();
        assertDoesNotThrow(app::uploadPDF);
    }

    /**
     * Tests loading empty Excel through UI
     */
    @Test
    void testUploadEmptyExcel() {
        App app = new App();
        assertDoesNotThrow(app::uploadExcel);
    }
}

public void main() {
}