package ro.tuiasi.ac;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.json.JSONObject;
import static org.junit.jupiter.api.Assertions.*;
import services.Analysis;
import services.ChatGPTService;
import services.PrepareResponse;

/**
 * Test suite for PDF and Excel analysis functionality
 */
class ExcelAndPDFValidationTest {

    /**
     * Test case to verify that the Excel file is not empty
     * @throws IOException if file operations fail
     * @testType Positive
     * @assertion The parsed JSON should contain at least one result
     * @precondition Valid non-empty Excel file exists
     * @postcondition JSON result contains data
     */
    @Test
    void testExcelFileIsNotEmpty() throws IOException {
        // Arrange
        File dataFile = new File("src/test/java/ro/tuiasi/ac/fisiereTest/analize.xlsx");

        // Act
        JSONObject result = PdfAnalysis.excelReader(dataFile);

        // Assert
        assertFalse(result.getJSONArray("results").isEmpty(),
                "Excel file should contain at least one analysis record");
    }

    /**
     * Tests reading Excel file with missing columns
     */
    @Test
    void testExcelReaderWithMissingColumns() {
        File missingColFile = new File("src/test/java/ro/tuiasi/ac/fisiereTest/analize_faracol.xlsx");
        assertThrows(IllegalStateException.class, () -> PdfAnalysis.excelReader(missingColFile));
    }

    /**
     * Tests reading Excel file with null values
     * @throws IOException if file operations fail
     */
    @Test
    void testExcelReaderWithNullValues() throws IOException {
        File nullValuesFile = new File("src/test/java/ro/tuiasi/ac/fisiereTest/analize.xlsx");
        JSONObject result = PdfAnalysis.excelReader(nullValuesFile);
        assertEquals(3, result.getJSONArray("results").length());
    }

    /**
     * Tests reading Excel file with wrong data types
     */
    @Test
    void testExcelReaderWithWrongDataTypes() {
        File wrongTypesFile = new File("src/test/java/ro/tuiasi/ac/fisiereTest/analize.xlsx");
        assertThrows(NumberFormatException.class, () -> PdfAnalysis.excelReader(wrongTypesFile));
    }

    /**
     * Tests reading an empty PDF file
     */
    @Test
    void testPdfReaderWithEmptyFile() {
        File emptyPdf = new File("src/test/java/ro/tuiasi/ac/fisiereTest/analize.pdf");
        assertDoesNotThrow(() -> PdfAnalysis.pdfReader(emptyPdf));
    }

    /**
     * Tests reading PDF without table content
     * @throws IOException if file operations fail
     */
    @Test
    void testPdfReaderWithoutTable() throws IOException {
        File noTablePdf = new File("src/test/java/ro/tuiasi/ac/fisiereTest/analize.pdf");
        PdfAnalysis.pdfReader(noTablePdf);
        // Should not throw exceptions for non-table PDFs
    }
}

/**
 * Test suite for ChatGPT service functionality
 */
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
class AppTest {

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

/**
 * Test suite for Analysis model
 */
class AnalysisTest {

    /**
     * Tests analysis model with null values
     */
    @Test
    void testAnalysisWithNullValues() {
        assertThrows(NullPointerException.class, () ->
                new Analysis(null, 0.0, null, null));
    }
}