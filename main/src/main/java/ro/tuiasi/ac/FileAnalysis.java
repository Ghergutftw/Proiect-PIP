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


/**
 * Clasa {@code FileAnalysis} oferă funcționalități pentru citirea și extragerea
 * conținutului din fișiere PDF și Excel, returnând datele într-un format ușor
 * de prelucrat (text sau JSON).
 *
 * <p>
 * Această clasă este utilă în special în aplicații care necesită extragerea automată
 * a informațiilor din rapoarte PDF sau foi de calcul Excel care conțin rezultate de analize.
 * </p>
 *
 * <p>
 * Pentru fișiere PDF, textul este extras pagină cu pagină folosind iText.
 * Pentru fișiere Excel, datele sunt convertite într-un obiect {@link JSONObject}
 * structurat, fiecare rând reprezentând o analiză.
 * </p>
 *
 * @author [Andrei]
 */
public class FileAnalysis {

    /**
     * Citește conținutul unui fișier PDF și returnează textul complet ca un {@code String}.
     *
     * <p>
     * Metoda parcurge fiecare pagină din PDF folosind o strategie de extragere a textului
     * care păstrează poziționarea logică a cuvintelor.
     * </p>
     *
     * @param file Fișierul PDF ce trebuie citit.
     * @return Textul complet extras din fișierul PDF.
     * @throws IOException Dacă fișierul nu poate fi citit sau deschis.
     */
    public static String pdfReader(File file) throws IOException {
        PdfReader reader = new PdfReader(file.getAbsolutePath());
        StringBuilder result = new StringBuilder();
        int pages = reader.getNumberOfPages();

        for (int i = 1; i <= pages; i++) {
            LocationTextExtractionStrategy strategy = new LocationTextExtractionStrategy();
            String pageText = PdfTextExtractor.getTextFromPage(reader, i, strategy);
            result.append(pageText).append("\n"); // Add newline between pages
        }

        reader.close();
        return result.toString();    }


    /**
     * Citește conținutul unui fișier Excel (.xlsx) și returnează datele sub formă de {@link JSONObject}.
     *
     * <p>
     * Se presupune că fișierul conține o structură tabelară, unde fiecare rând (cu excepția antetului)
     * reprezintă o analiză medicală, iar coloanele sunt: denumirea analizei, intervalul de referință
     * și rezultatul obținut.
     * </p>
     *
     * @param file Fișierul Excel (.xlsx) ce trebuie analizat.
     * @return Un obiect {@code JSONObject} cu o cheie "results" care conține un array de analize.
     * @throws IOException Dacă fișierul nu poate fi citit sau deschis.
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
