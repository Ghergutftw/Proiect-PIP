package services;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PrepareResponse {

    /**
     * Procesează răspunsul primit de la ChatGPT și returnează o listă de obiecte Analysis.
     *
     * @param chatGPTResponse Răspunsul primit de la ChatGPT
     * @return Lista de obiecte Analysis create din răspuns
     */
    public List<Analysis> processResponse(ChatGPTResponse chatGPTResponse) {
        List<Analysis> analyses = new ArrayList<>();

        // Verificăm dacă răspunsul conține date valide
        if (chatGPTResponse == null || chatGPTResponse.getMessageContent() == null) {
            System.out.println("Răspunsul de la ChatGPT este gol sau invalid.");
            return analyses;
        }

        // Parsăm conținutul mesajului primit de la ChatGPT
        String messageContent = chatGPTResponse.getMessageContent();

        try {
            // Exemplu de parsare a unui răspuns JSON simplu (în practică, structura poate fi mai complexă)
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> parsedResponse = objectMapper.readValue(messageContent, Map.class);

            // Simulăm extragerea datelor din răspuns
            List<Map<String, Object>> results = (List<Map<String, Object>>) parsedResponse.get("results");

            for (Map<String, Object> result : results) {
                String denumireAnaliza = (String) result.get("denumireAnaliza");
                double rezultat = ((Number) result.get("rezultat")).doubleValue();
                String UM = (String) result.get("UM");
                String intervalReferinta = (String) result.get("intervalReferinta");
                String severitate = (String) result.get("severitate");

                // Creăm un obiect Analysis și îl adăugăm în listă
                analyses.add(new Analysis(denumireAnaliza, rezultat, UM, intervalReferinta, severitate));
            }
        } catch (Exception e) {
            System.err.println("Eroare la parsarea răspunsului: " + e.getMessage());
            e.printStackTrace();
        }

        return analyses;
    }
}


