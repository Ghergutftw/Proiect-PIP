package ro.tuiasi.ac;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileAnalysisTest {

    @TempDir
    static Path tempDir;
    static File testPdfFile;
    static File testExcelFile;

    @BeforeAll
    static void setup() throws IOException {
        // Create a test PDF file
        testPdfFile = tempDir.resolve("test.pdf").toFile();
        try (PDDocument document = new PDDocument()) {
            document.addPage(new PDPage());
            document.save(testPdfFile);
        }

        // Create a test Excel file
        testExcelFile = tempDir.resolve("test.xlsx").toFile();
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Test");
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("denumireAnaliza");
            headerRow.createCell(1).setCellValue("intervalReferinta");
            headerRow.createCell(2).setCellValue("rezultat");

            Row dataRow = sheet.createRow(1);
            dataRow.createCell(0).setCellValue("Test");
            dataRow.createCell(1).setCellValue("0-10");
            dataRow.createCell(2).setCellValue(5.0);

            workbook.write(Files.newOutputStream(testExcelFile.toPath()));
        }
    }

    @Test
    void pdfReader_ShouldReturnContent_ForValidPdf() throws IOException {
        String content = FileAnalysis.pdfReader(testPdfFile);
        assertNotNull(content);
        assertFalse(content.isEmpty());
    }

    @Test
    void pdfReader_ShouldThrowException_ForNonPdfFile() {
        File notPdfFile = tempDir.resolve("notpdf.txt").toFile();
        assertThrows(IOException.class, () -> FileAnalysis.pdfReader(notPdfFile));
    }

    @Test
    void excelReader_ShouldParseNumericValuesCorrectly() throws IOException {
        JSONObject result = FileAnalysis.excelReader(testExcelFile);
        JSONArray resultsArray = result.getJSONArray("results");
        assertEquals(1, resultsArray.length());

        JSONObject analysis = resultsArray.getJSONObject(0);
        assertEquals("Test", analysis.getString("denumireAnaliza"));
        assertEquals("0-10", analysis.getString("intervalReferinta"));
        assertEquals(5.0, analysis.getDouble("rezultat"));
    }

    @Test
    void excelReader_ShouldHandleDifferentDataTypes() throws IOException {
        // Create a test file with mixed data types
        File mixedDataFile = tempDir.resolve("mixed.xlsx").toFile();
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Test");
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("denumireAnaliza");
            headerRow.createCell(1).setCellValue("intervalReferinta");
            headerRow.createCell(2).setCellValue("rezultat");

            Row dataRow = sheet.createRow(1);
            dataRow.createCell(0).setCellValue("Test");
            dataRow.createCell(1).setCellValue("0-10");
            dataRow.createCell(2).setCellValue("High"); // String instead of number

            workbook.write(Files.newOutputStream(mixedDataFile.toPath()));
        }

        JSONObject result = FileAnalysis.excelReader(mixedDataFile);
        JSONObject analysis = result.getJSONArray("results").getJSONObject(0);
        assertEquals("High", analysis.getString("rezultat"));
    }
}