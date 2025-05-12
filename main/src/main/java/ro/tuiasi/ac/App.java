package ro.tuiasi.ac;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import services.Analysis;
import services.ChatGPTService;
import services.PrepareResponse;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.File;
import javax.swing.JScrollPane;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static ro.tuiasi.ac.FileAnalysis.excelReader;

public class App extends JFrame {

    private static List<Analysis> listaAnalize = new ArrayList<>();
    private static String observatieGPT;
    ChatGPTService chatGPTService = new ChatGPTService();

    public App() {
        setTitle("Excel and PDF Uploader");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        JButton uploadButtonExcel = new JButton("Upload Excel");
        JButton uploadButtonPDF = new JButton("Upload PDF");
        uploadButtonExcel.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrează butoanele
        uploadButtonPDF.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.add(uploadButtonExcel);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(uploadButtonPDF);
        add(buttonPanel, BorderLayout.WEST);

        String[] columns = {"Analiza", "Rezultat", "Interval de referinta", "Severitate"};

        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        table.setRowHeight(25);
        table.setFont(new Font("Arial", Font.BOLD, 14));
        JTableHeader th = table.getTableHeader();
        th.setFont(new Font("Arial", Font.BOLD, 14));
        table.setRowHeight(30);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        uploadButtonExcel.addActionListener(e -> {
            try {
                uploadExcel();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }finally {
                refreshTable(tableModel);
            }
        });
        uploadButtonPDF.addActionListener(e -> {
            try {
                uploadPDF();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }finally {
                refreshTable(tableModel);
            }
        });
        JButton uploadExcelButton = new JButton("Upload Excel");
        uploadExcelButton.addActionListener(e -> {
            try {
                uploadExcel();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } finally {
                refreshTable(tableModel);
            }
        });



    }

    private void refreshTable(DefaultTableModel tableModel)
    {
        tableModel.setRowCount(0);

        for (Analysis analiza : listaAnalize) {
            Object[] rowData = {analiza.getDenumireAnaliza(), analiza.getRezultat(), analiza.getIntervalReferinta(), analiza.getSeveritate()};
            tableModel.addRow(rowData);
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            App app = new App();
            app.setVisible(true);
        });
    }

    void uploadPDF() throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select a PDF File");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PDF Documents", "pdf"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            System.out.println("PDF Uploaded: " + selectedFile.getAbsolutePath());

            // 1. Extract text from the PDF
            String content = FileAnalysis.pdfReader(selectedFile);

            // 2. Prompt to instruct ChatGPT on what to do with the text
            listaAnalize = PrepareResponse.processResponse(chatGPTService.getChatGPTResponse("Attach a severity rank " +
                    "for EACH analysis that you will find in this text following this pattern: analize, rezultate, um, interval" +
                    "biologic de referinta( basically look more after the combination: number   unit measure  interval " +
                    " and I want the response to be directly in " +
                    "json format starting with: Here's the JSON formatted response with severity ranks based on the provided analyses:\n" +
                    "\n" +
                    "```json\n" +
                    "{\n" +
                    "  \"results\": [" +
                    ", (the fields that i want will be named exactly denumireAnaliza, rezultat, intervalReferinta, severityRank(I want this to be a word, not a number)) " + content));
            ObjectMapper mapper = new ObjectMapper();
            String listaAnalizeJson = mapper.writeValueAsString(listaAnalize);
            String promptGPT = "Analizează următorul JSON care conține rezultatele unor analize medicale. Îți cer să îmi oferi o observație generală de maximum 100 de cuvinte, într-un limbaj clar și util pentru pacient (nu medical avansat). Vreau să îmi spui:\n" +
                    "\n" +
                    "Unde sunt cele mai îngrijorătoare rezultate (care ies cel mai mult din intervalul de referință, dar sa-mi spui fara sa mentionezi intervalul de referinta),\n" +
                    "\n" +
                    "Ce riscuri pot fi asociate acestor valori (dacă se poate),\n" +
                    "\n" +
                    "Și ce recomandări generale ar putea urma pacientul pentru a-și îmbunătăți starea de sănătate.:" + listaAnalizeJson;
            observatieGPT = PrepareResponse.processObservation(chatGPTService.getChatGPTResponse(promptGPT));
            System.out.println(promptGPT);
            System.out.println(observatieGPT);
        }
    }

    void uploadExcel() throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select an Excel File");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel Files", "xlsx", "xls"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            System.out.println("Excel Uploaded: " + selectedFile.getAbsolutePath());

            JSONObject content = excelReader(selectedFile);

            listaAnalize = PrepareResponse.processResponse(chatGPTService.getChatGPTResponse("Attach a severity rank " +
                    "for each analysis I will give you and I want the response to be in a json format" +
                    ", (the fields that i want will be named exactly denumireAnaliza, rezultat, intervalReferinta, severityRank) " + content));
        }
    }
}