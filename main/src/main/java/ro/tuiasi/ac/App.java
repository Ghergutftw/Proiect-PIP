package ro.tuiasi.ac;

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
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static ro.tuiasi.ac.FileAnalysis.excelReader;
import static ro.tuiasi.ac.FileAnalysis.pdfReader;

public class App extends JFrame {

    private static List<Analysis> listaAnalize = new ArrayList<>();
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
            JSONObject content = FileAnalysis.pdfReader(selectedFile);

            // 2. Prompt to instruct ChatGPT on what to do with the text
            listaAnalize = PrepareResponse.processResponse(chatGPTService.getChatGPTResponse("Attach a severity rank " +
                    "for each analysis that you will find in this text and I want the response to be directly in " +
                    "json format starting with ```json" +
                    ", (the fields that i want will be named exactly denumireAnaliza, rezultat, intervalReferinta, severityRank) " + content));

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