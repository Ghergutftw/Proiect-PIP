package ro.tuiasi.ac;

import services.ChatGPTService;
import javax.swing.*;
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
        String message = "Hello, how are you?";
      //  String response = chatGPTService.getChatGPTResponse(message);
        // System.out.println(response);
        SwingUtilities.invokeLater(() -> {
            App app = new App();
            app.setVisible(true);
        });
    }

}