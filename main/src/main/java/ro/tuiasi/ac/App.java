package ro.tuiasi.ac;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import services.Analysis;
import services.ChatGPTService;
import services.PrepareResponse;

import javax.swing.border.EmptyBorder;
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

    private JLabel statusLabel;
    private JLabel loadingLabel;
    private final JTextArea observatieTextArea;
    private static List<Analysis> listaAnalize = new ArrayList<>();
    private static String observatieGPT;
    ChatGPTService chatGPTService = new ChatGPTService();
    ObjectMapper mapper = new ObjectMapper();

    public App() {
        setTitle("Analiză Medicală - Uploader");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 245, 245));

        // Styling panel for upload buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(240, 240, 240));
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JButton uploadButtonExcel = createStyledButton(" Upload Excel");
        JButton uploadButtonPDF = createStyledButton(" Upload PDF");

        buttonPanel.add(uploadButtonExcel);
        buttonPanel.add(Box.createVerticalStrut(20));
        buttonPanel.add(uploadButtonPDF);

        add(buttonPanel, BorderLayout.WEST);

        String[] columns = {"Analiza", "Rezultat", "Interval de referință", "Severitate"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel) {
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int col) {
                Component c = super.prepareRenderer(renderer, row, col);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(235, 245, 255));
                } else {
                    c.setBackground(new Color(180, 220, 240));
                }
                return c;
            }
        };

        // Table Styling
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setGridColor(Color.LIGHT_GRAY);
        table.setShowHorizontalLines(true);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JTableHeader th = table.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 14));
        th.setBackground(new Color(210, 230, 250));
        th.setForeground(Color.DARK_GRAY);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Observation Text Area
        observatieTextArea = new JTextArea(4, 60);
        observatieTextArea.setLineWrap(true);
        observatieTextArea.setWrapStyleWord(true);
        observatieTextArea.setEditable(false);
        observatieTextArea.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        observatieTextArea.setBorder(BorderFactory.createTitledBorder("Observație generală"));

        JScrollPane observatieScroll = new JScrollPane(observatieTextArea);
        observatieScroll.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(observatieScroll, BorderLayout.SOUTH);



        // Action Listeners
        uploadButtonExcel.addActionListener(e -> {
            try {
                uploadExcel();
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                refreshTable(tableModel);
            }
        });

        uploadButtonPDF.addActionListener(e -> {
            try {
                uploadPDF();
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                refreshTable(tableModel);
            }
        });
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(new Color(66, 133, 244));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        return button;
    }

    private void refreshTable(DefaultTableModel tableModel) {
        tableModel.setRowCount(0);
        for (Analysis analiza : listaAnalize) {
            Object[] rowData = {
                    analiza.getDenumireAnaliza(),
                    analiza.getRezultat(),
                    analiza.getIntervalReferinta(),
                    analiza.getSeveritate()
            };
            tableModel.addRow(rowData);
        }
    }

    private void refreshObservatie() {
        if (observatieGPT != null && !observatieGPT.isEmpty()) {
            observatieTextArea.setText(observatieGPT);
        } else {
            observatieTextArea.setText("Nicio observație generată încă.");
        }
    }


    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            App app = new App();
            app.setVisible(true);
        });
    }

    public void uploadPDF() throws IOException {
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
            String listaAnalizeJson = mapper.writeValueAsString(listaAnalize);
            String promptGPT = "Analizează următorul JSON care conține rezultatele unor analize medicale. Îți cer să îmi oferi o observație generală de maximum 100 de cuvinte, într-un limbaj clar și util pentru pacient (nu medical avansat). Vreau să îmi spui:\n" +
                    "\n" +
                    "Unde sunt cele mai îngrijorătoare rezultate (care ies cel mai mult din intervalul de referință, dar sa-mi spui fara sa mentionezi intervalul de referinta),\n" +
                    "\n" +
                    "Ce riscuri pot fi asociate acestor valori (dacă se poate),\n" +
                    "\n" +
                    "Și ce recomandări generale ar putea urma pacientul pentru a-și îmbunătăți starea de sănătate.:" + listaAnalizeJson;
            observatieGPT = PrepareResponse.processObservation(chatGPTService.getChatGPTResponse(promptGPT));
            refreshObservatie();
        }
    }

    public void uploadExcel() throws IOException {
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
            String listaAnalizeJson = mapper.writeValueAsString(listaAnalize);
            String promptGPT = "Analizează următorul JSON care conține rezultatele unor analize medicale. Îți cer să îmi oferi o observație generală de maximum 100 de cuvinte, într-un limbaj clar și util pentru pacient (nu medical avansat). Vreau să îmi spui:\n" +
                    "\n" +
                    "Unde sunt cele mai îngrijorătoare rezultate (care ies cel mai mult din intervalul de referință, dar sa-mi spui fara sa mentionezi intervalul de referinta),\n" +
                    "\n" +
                    "Ce riscuri pot fi asociate acestor valori (dacă se poate),\n" +
                    "\n" +
                    "Și ce recomandări generale ar putea urma pacientul pentru a-și îmbunătăți starea de sănătate.:" + listaAnalizeJson;
            observatieGPT = PrepareResponse.processObservation(chatGPTService.getChatGPTResponse(promptGPT));
            refreshObservatie();
        }
    }
}