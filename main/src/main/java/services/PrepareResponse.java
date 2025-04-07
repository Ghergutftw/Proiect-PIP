package services;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class PrepareResponse {

    private static void setField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Could not set field: " + fieldName, e);
        }
    }

    public static ChatGPTResponse createMockResponse() {
        ChatGPTResponse response = new ChatGPTResponse();

        // Creăm un conținut JSON valid care poate fi parsat
        String jsonContent = """
    {
        "results": [
            {
                "denumireAnaliza": "Hemoglobină (MOCK)",
                "rezultat": 15.0,
                "UM": "g/dL",
                "intervalReferinta": "12.0-16.0",
                "severitate": "normal"
            },
            {
                "denumireAnaliza": "Glicemie (MOCK)",
                "rezultat": 95.0,
                "UM": "mg/dL",
                "intervalReferinta": "70-99",
                "severitate": "normal"
            }
        ]
    }
    """;

        // Creăm un Message
        ChatGPTResponse.Message message = new ChatGPTResponse.Message();
        setField(message, "role", "assistant");
        setField(message, "content", jsonContent);  // Folosim JSON valid aici

        // Creăm un Choice și setăm mesajul
        ChatGPTResponse.Choice choice = new ChatGPTResponse.Choice();
        setField(choice, "message", message);

        // Setăm toate câmpurile din ChatGPTResponse
        setField(response, "id", "chatcmpl-mock123");
        setField(response, "object", "chat.completion.mock");
        setField(response, "model", "gpt-4-mock");
        List<ChatGPTResponse.Choice> choices = new ArrayList<>();
        choices.add(choice);
        setField(response, "choices", choices);

        return response;
    }

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


