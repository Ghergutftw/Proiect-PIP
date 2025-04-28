package services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
    public static List<Analysis> processResponse(String chatGPTResponse) throws JsonProcessingException, JsonProcessingException {
        List<Analysis> allAnalyses = new ArrayList<>();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(chatGPTResponse);

        JsonNode contentNode = rootNode
                .path("choices")
                .get(0)
                .path("message")
                .path("content");

        String contentString = contentNode.asText();

        // ✅ Extrage doar JSON-ul dintre ```json și ```
        Pattern pattern = Pattern.compile("```json\\s*(\\{.*?\\})\\s*```", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(contentString);

        String extractedJson;
        if (matcher.find()) {
            extractedJson = matcher.group(1);
        } else {
            throw new IllegalStateException("JSON block not found in the content!");
        }

        // ✅ Parsează JSON-ul extras
        JsonNode extractedJsonNode = mapper.readTree(extractedJson);

        // ✅ Iterează prin rezultate
        for (JsonNode result : extractedJsonNode.get("results")) {
            String denumire = result.get("denumireAnaliza").asText();
            double rezultat = result.get("rezultat").asDouble();
            String intervalReferinta = result.get("intervalReferinta").asText();
            String severityRank = result.get("severityRank").asText();

            Analysis analysis = new Analysis(denumire, rezultat, intervalReferinta, severityRank);
            allAnalyses.add(analysis);
        }

        return allAnalyses;
    }
}


