package ro.tuiasi.ac;

import services.Analysis;
import services.ChatGPTResponse;
import services.ChatGPTService;
import services.PrepareResponse;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static ro.tuiasi.ac.PdfAnalysis.pdfReader;

public class App extends JFrame{

    private static List<Analysis> listaAnalize;

    public App() {
        // Set JFrame properties
        setTitle("Excel Uploader");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Create a button
        JPanel buttonPanel = new JPanel();
        JButton uploadButton = new JButton("Upload Excel");
        uploadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    uploadPDF();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        buttonPanel.add(uploadButton);
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
            Object[] rowData = {analiza.getDenumireAnaliza(), analiza.getRezultat(), analiza.getUM(), analiza.getIntervalReferinta(), analiza.getSeveritate()};
            tableModel.addRow(rowData);
        }
    }

    // Function to open file chooser and allow PDF selection, then call pdfReader
    private void uploadPDF() throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select an Excel File");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel Documents", "xlsx"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            System.out.println("Excel Uploaded: " + selectedFile.getAbsolutePath());

            // Here, you would add logic to store the PDF in your database

            // Call pdfReader with the selected file name
            pdfReader(selectedFile);
        }
    }


    // Getter pentru lista de analize
    public static List<Analysis> getListaAnalize() {
        return listaAnalize;
    }

    // Metodă pentru încărcarea datelor
    public static void incarcaDateAnalize() {
        ChatGPTResponse mockResponse = PrepareResponse.createMockResponse();
        PrepareResponse prepareResponse = new PrepareResponse();
        listaAnalize = prepareResponse.processResponse(mockResponse);
    }

    public static void main(String[] args) {

        ChatGPTService chatGPTService = new ChatGPTService();
        String message = """
    {
        "results": [
            {
                "denumireAnaliza": "Hemoglobină",
                "rezultat": 14.2,
                "UM": "g/dL",
                "intervalReferinta": "12.0-16.0",
                "severitate": "normal"
            },
            {
                "denumireAnaliza": "Glicemie",
                "rezultat": 110,
                "UM": "mg/dL",
                "intervalReferinta": "70-99",
                "severitate": "crescut"
            }
        ]
    }
    """;
        //ChatGPTResponse response = chatGPTService.getChatGPTResponse(message);
        // System.out.println(response);

        ChatGPTResponse mockResponse = PrepareResponse.createMockResponse();

        System.out.println("=== TESTARE CU MOCK RESPONSE ===");
        System.out.println("Se folosește răspunsul mock...\n");

        incarcaDateAnalize();

        if (listaAnalize != null) {
            System.out.println("Răspuns mock creat cu succes!");
            System.out.println("Se procesează datele...\n");

            PrepareResponse prepareResponse = new PrepareResponse();

            System.out.println("=== REZULTATE ANALIZE ===");
            System.out.println("--------------------------\n");

            for (Analysis analiza : listaAnalize) {
                System.out.println("Severitate: " + analiza.getSeveritate());
                System.out.println("Denumire: " + analiza.getDenumireAnaliza());
                System.out.println("Rezultat: " + analiza.getRezultat());
                System.out.println("UM: " + analiza.getUM());
                System.out.println("Interval: " + analiza.getIntervalReferinta());
                System.out.println("----------------------");
            }
        } else {
            System.out.println("Nu s-au încărcat datele de analiză.");
        }

        SwingUtilities.invokeLater(() -> {
            App app = new App();
            app.setVisible(true);
        });
    }
}