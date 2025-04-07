package ro.tuiasi.ac;

import services.Analysis;
import services.ChatGPTResponse;
import services.ChatGPTService;
import services.PrepareResponse;

import javax.swing.*;
import java.util.List;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import static ro.tuiasi.ac.PdfAnalysis.pdfReader;

public class App extends JFrame{

    PdfAnalysis pdfAnalysis = new PdfAnalysis();

    public App() {
        // Set JFrame properties
        setTitle("PDF Uploader");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Create a button
        JButton uploadButton = new JButton("Upload PDF");
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

        // Create a panel and align the button to the bottom-left
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.add(uploadButton);

        // Add panel to JFrame
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // Function to open file chooser and allow PDF selection, then call pdfReader
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
    """;;
        ChatGPTResponse response = chatGPTService.getChatGPTResponse(message);
        // System.out.println(response);

        if (response != null) {
            System.out.println("Răspuns primit de la ChatGPT!");
            System.out.println("Se procesează datele...\n");

            PrepareResponse prepareResponse = new PrepareResponse();
            List<List<Analysis>> vectorAnalize = prepareResponse.processResponse(response);

            System.out.println("=== REZULTATE ANALIZE ===");
            System.out.println("Număr seturi de rezultate: " + vectorAnalize.size());
            System.out.println("--------------------------\n");

            // Iterăm prin fiecare listă de analize din vector
            for (int i = 0; i < vectorAnalize.size(); i++) {
                System.out.println("Set #" + (i + 1) + ":");
                List<Analysis> analizeCurente = vectorAnalize.get(i);

                // Afișăm fiecare analiză folosind metoda toList()
                for (Analysis analiza : analizeCurente) {
                    List<String> detaliiAnaliza = analiza.toList();  // Am redenumit variabila aici
                    for (String detaliu : detaliiAnaliza) {         // Și aici
                        System.out.println("  " + detaliu);
                    }
                    System.out.println();  // Linie goală între analize
                }
                System.out.println("----------------------");
            }
        } else {
            System.out.println("Eroare: Nu s-a primit răspuns de la ChatGPT.");
        }

        SwingUtilities.invokeLater(() -> {
            App app = new App();
            app.setVisible(true);
        });
    }
}