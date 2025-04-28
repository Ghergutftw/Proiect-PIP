package ro.tuiasi.ac;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;
import services.Analysis;
import services.ChatGPTResponse;
import services.ChatGPTService;
import services.PrepareResponse;

import javax.swing.*;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ro.tuiasi.ac.PdfAnalysis.excelReader;
import static ro.tuiasi.ac.PdfAnalysis.pdfReader;

public class App extends JFrame {

    private static List<Analysis> listaAnalize;
    PdfAnalysis pdfAnalysis = new PdfAnalysis();
    ChatGPTService chatGPTService = new ChatGPTService();

    public App() {
        // Set JFrame properties
        setTitle("Excel and PDF Uploader");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Create a button
        JPanel buttonPanel = new JPanel();
        JButton uploadButtonExcel = new JButton("Upload Excel");
        JButton uploadButtonPDF = new JButton("Upload PDF");
        uploadButtonExcel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    uploadExcel();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        uploadButtonPDF.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    uploadExcel();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        JButton uploadExcelButton = new JButton("Upload Excel");
        uploadExcelButton.addActionListener(e -> {
            try {
                uploadExcel();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        buttonPanel.add(uploadButtonExcel);
        buttonPanel.add(uploadButtonPDF);
        add(buttonPanel, BorderLayout.WEST);

        // Create a table with a scroll pane (right side)
        String[] columns = {"Analiza", "Rezultat", "Unitate de masura", "Interval de referinta", "Severitate"};

        // Initialize table model with empty data
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);

        // Optional: Customize table appearance
        table.setRowHeight(25);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        tableModel.setRowCount(0);

        // Add new data from the members list
        for (Analysis analiza : listaAnalize) {
            Object[] rowData = {analiza.getDenumireAnaliza(), analiza.getRezultat(), analiza.getIntervalReferinta(), analiza.getSeveritate()};
            tableModel.addRow(rowData);
        }
    }

    private void uploadPDF() throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select a PDF File");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PDF Documents", "pdf"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            System.out.println("PDF Uploaded: " + selectedFile.getAbsolutePath());

            // Here, you would add logic to store the PDF in your database

            // Call pdfReader with the selected file name
            pdfReader(selectedFile);
        }
    }

    private String uploadExcel() throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select an Excel File");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel Files", "xlsx", "xls"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            System.out.println("Excel Uploaded: " + selectedFile.getAbsolutePath());

            JSONObject content = excelReader(selectedFile);

            listaAnalize = PrepareResponse.processResponse(chatGPTService.getChatGPTResponse("Attach a severity rank for each analysis I will give you and I want the response to be in a json format " + content));
        }
        return "";

    }

    // Getter pentru lista de analize
    public static List<Analysis> getListaAnalize() {
        return listaAnalize;
    }

    // Metodă pentru încărcarea datelor
    public static void incarcaDateAnalize() {
        ChatGPTResponse mockResponse = PrepareResponse.createMockResponse();
        PrepareResponse prepareResponse = new PrepareResponse();
//        listaAnalize = prepareResponse.processResponse(mockResponse);
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            App app = new App();
            app.setVisible(true);
        });
    }
}