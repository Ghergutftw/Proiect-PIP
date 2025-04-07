package services;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class PrepareResponse {

    /**
     * Procesează răspunsul primit de la ChatGPT și returnează un vector de liste cu obiecte Analysis.
     *
     * @param chatGPTResponse Răspunsul primit de la ChatGPT
     * @return Vector de liste de obiecte Analysis (câte o listă pentru fiecare răspuns din choices)
     */
    public List<List<Analysis>> processResponse(ChatGPTResponse chatGPTResponse) {
        List<List<Analysis>> allAnalyses = new ArrayList<>();

        // Verificăm dacă răspunsul conține date valide
        if (chatGPTResponse == null || chatGPTResponse.getChoices() == null) {
            System.out.println("Răspunsul de la ChatGPT este gol sau invalid.");
            return allAnalyses;
        }

        // Iterăm prin toate răspunsurile din `choices`
        for (ChatGPTResponse.Choice choice : chatGPTResponse.getChoices()) {
            List<Analysis> analyses = new ArrayList<>();
            String messageContent = choice.getMessage().getContent();

            try {
                // Parsăm conținutul mesajului primit de la ChatGPT
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> parsedResponse = objectMapper.readValue(messageContent, Map.class);

                // Extragem lista de rezultate
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

            // Adăugăm lista de răspunsuri în vectorul final
            allAnalyses.add(analyses);
        }

        return allAnalyses;
    }
}


