package ro.tuiasi.ac;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import services.Analysis;
import services.ChatGPTService;
import services.PrepareResponse;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static ro.tuiasi.ac.FileAnalysis.excelReader;

public class App extends JFrame {
    private JTextArea observatieTextArea;
    private static List<Analysis> listaAnalize = new ArrayList<>();
    private static String observatieGPT;
    private ChatGPTService chatGPTService = new ChatGPTService();
    private ObjectMapper mapper = new ObjectMapper();
    private TableRowSorter<DefaultTableModel> sorter;
    private DefaultTableModel tableModel;
    private JTable table;

    public App() {
        // Frame setup
        setTitle("Excel and PDF Uploader");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Initialize components
        initializeComponents();
    }

    private void initializeComponents() {
        // Button Panel (West)
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.WEST);

        // Table setup
        String[] columns = {"Analiza", "Rezultat", "Interval de referinta", "Severitate"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                return String.class; // Uniform sorting behavior
            }
        };
        table = new JTable(tableModel);
        styleTable();

        // Sort Panel (North)
        JPanel sortPanel = createSortPanel();
        add(sortPanel, BorderLayout.NORTH);

        // Observation Text Area (South)
        observatieTextArea = createObservationArea();
        add(new JScrollPane(observatieTextArea), BorderLayout.SOUTH);

        // Table Scroll Pane (Center)
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Initialize sorter
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        JButton uploadButtonExcel = new JButton("Upload Excel");
        JButton uploadButtonPDF = new JButton("Upload PDF");

        uploadButtonExcel.setAlignmentX(Component.CENTER_ALIGNMENT);
        uploadButtonPDF.setAlignmentX(Component.CENTER_ALIGNMENT);

        uploadButtonExcel.addActionListener(e -> {
            try {
                uploadExcel();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error uploading Excel: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        uploadButtonPDF.addActionListener(e -> {
            try {
                uploadPDF();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error uploading PDF: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonPanel.add(uploadButtonExcel);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(uploadButtonPDF);

        return buttonPanel;
    }

    private JPanel createSortPanel() {
        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JComboBox<String> sortFieldCombo = new JComboBox<>(
                new String[]{"Analiza", "Severitate"});

        JButton sortButton = new JButton("Sort");
        sortButton.addActionListener(e -> {
            int columnIndex = sortFieldCombo.getSelectedIndex();
            if (columnIndex == 1)
                columnIndex = 3;
            toggleSort(columnIndex);
        });

        sortPanel.add(new JLabel("Sort by:"));
        sortPanel.add(sortFieldCombo);
        sortPanel.add(sortButton);

        return sortPanel;
    }

    private JTextArea createObservationArea() {
        JTextArea area = new JTextArea(5, 60);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);
        area.setFont(new Font("Arial", Font.ITALIC, 14));
        area.setBorder(BorderFactory.createTitledBorder("Observație generală"));
        return area;
    }

    private void styleTable() {
        // Center alignment
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Font and row height
        table.setFont(new Font("Arial", Font.BOLD, 14));
        table.setRowHeight(30);

        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setBackground(new Color(70, 130, 180));
        header.setForeground(Color.WHITE);

        // Zebra striping
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(240, 240, 240));
                }
                return c;
            }
        });
    }

    private void toggleSort(int columnIndex) {
        if (sorter.getSortKeys().size() > 0 &&
                sorter.getSortKeys().get(0).getColumn() == columnIndex) {
            // Reverse current sort order
            SortOrder current = sorter.getSortKeys().get(0).getSortOrder();
            SortOrder newOrder = current == SortOrder.ASCENDING ?
                    SortOrder.DESCENDING : SortOrder.ASCENDING;
            sorter.setSortKeys(List.of(new RowSorter.SortKey(columnIndex, newOrder)));
        } else {
            // New column sort
            sorter.setSortKeys(List.of(new RowSorter.SortKey(columnIndex, SortOrder.ASCENDING)));
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Analysis analiza : listaAnalize) {
            tableModel.addRow(new Object[]{
                    analiza.getDenumireAnaliza(),
                    analiza.getRezultat(),
                    analiza.getIntervalReferinta(),
                    analiza.getSeveritate()
            });
        }
    }

    private void refreshObservatie() {
        observatieTextArea.setText(observatieGPT != null && !observatieGPT.isEmpty() ?
                observatieGPT : "Nicio observație generată încă.");
    }

    public void uploadPDF() throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select a PDF File");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PDF Documents", "pdf"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String content = FileAnalysis.pdfReader(selectedFile);

            listaAnalize = PrepareResponse.processResponse(chatGPTService.getChatGPTResponse(
                    "Attach a severity rank for EACH analysis that you will find in this text following this pattern: " +
                            "analize, rezultate, um, interval biologic de referinta (basically look more after the combination: " +
                            "number unit measure interval) and I want the response to be directly in json format starting with: " +
                            "Here's the JSON formatted response with severity ranks based on the provided analyses " +
                            "(('Low','Medium','High')):\n\n```json\n{\n  \"results\": [" +
                            ", (the fields that i want will be named exactly denumireAnaliza, rezultat, " +
                            "intervalReferinta, severityRank(I want this to be a word, not a number)) " + content));

            generateObservation();
        }
    }

    public void uploadExcel() throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select an Excel File");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel Files", "xlsx", "xls"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            JSONObject content = excelReader(selectedFile);

            listaAnalize = PrepareResponse.processResponse(chatGPTService.getChatGPTResponse(
                    "Attach a severity rank for each analysis I will give you and I want the response to be in a json format" +
                            ", (the fields that i want will be named exactly denumireAnaliza, rezultat, intervalReferinta, " +
                            "severityRank(('Low','Medium','High')) " + content));

            generateObservation();
        }
    }

    private void generateObservation() throws IOException {
        String listaAnalizeJson = mapper.writeValueAsString(listaAnalize);
        String promptGPT = "Analizează următorul JSON care conține rezultatele unor analize medicale. Îți cer să îmi " +
                "oferi o observație generală de maximum 100 de cuvinte, într-un limbaj clar și util pentru pacient " +
                "(nu medical avansat). Vreau să îmi spui:\n\n" +
                "Unde sunt cele mai îngrijorătoare rezultate (care ies cel mai mult din intervalul de referință, " +
                "dar sa-mi spui fara sa mentionezi intervalul de referinta),\n\n" +
                "Ce riscuri pot fi asociate acestor valori (dacă se poate),\n\n" +
                "Și ce recomandări generale ar putea urma pacientul pentru a-și îmbunătăți starea de sănătate.:" +
                listaAnalizeJson;

        observatieGPT = PrepareResponse.processObservation(chatGPTService.getChatGPTResponse(promptGPT));
        refreshTable();
        refreshObservatie();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App().setVisible(true));
    }

    public Collection<Analysis> getListaAnalize() {
        return listaAnalize;
    }
}