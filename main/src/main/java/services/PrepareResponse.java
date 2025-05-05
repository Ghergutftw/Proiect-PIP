package services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PrepareResponse {

    /**
     * Procesează răspunsul primit de la ChatGPT și returnează un vector de liste cu obiecte Analysis.
     *
     * @param chatGPTResponse Răspunsul primit de la ChatGPT
     * @return Vector de liste de obiecte Analysis (câte o listă pentru fiecare răspuns din choices)
     */
    public static List<Analysis> processResponse(String chatGPTResponse) throws JsonProcessingException {
        List<Analysis> allAnalyses = new ArrayList<>();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(chatGPTResponse);

        JsonNode contentNode = rootNode
                .path("choices")
                .get(0)
                .path("message")
                .path("content");

        String contentString = contentNode.asText();

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


