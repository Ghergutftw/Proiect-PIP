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

public class App extends JFrame{
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
                uploadPDF();
            }
        });

        // Create a panel and align the button to the bottom-left
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.add(uploadButton);

        // Add panel to JFrame
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // Function to open file chooser and allow PDF selection, then call pdfReader
    private void uploadPDF() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select a PDF File");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PDF Documents", "pdf"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            System.out.println("PDF Uploaded: " + selectedFile.getAbsolutePath());

            // Here, you would add logic to store the PDF in your database

            // Call pdfReader with the selected file name
            pdfReader(selectedFile.getName());
        }
    }

    // Function to handle the uploaded PDF file
    private void pdfReader(String fileName) {
        System.out.println("Opening PDF: " + fileName);
        // Add logic to read and process the PDF file
    }


    public static void main(String[] args) {
        ChatGPTService chatGPTService = new ChatGPTService();
        String message = "Hello, how are you?";
        ChatGPTResponse response = chatGPTService.getChatGPTResponse(message);
        // System.out.println(response);

        if (response != null) {
            // Process the response using PrepareResponse
            PrepareResponse prepareResponse = new PrepareResponse();
            List<Analysis> analyses = prepareResponse.processResponse(response);

            // Print the analysis details
            System.out.println("Detalii analiza:");
            for (Analysis analysis : analyses) {
                List<String> analysisDetails = analysis.toList(); // Use the toList() method
                for (String detail : analysisDetails) {
                    System.out.println(detail);
                }
            }
        } else {
            System.out.println("Nu s-a primit un răspuns valid de la ChatGPT.");
        }

        SwingUtilities.invokeLater(() -> {
            App app = new App();
            app.setVisible(true);
        });
    }

}