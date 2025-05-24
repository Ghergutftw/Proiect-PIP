package services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Clasa {@code PrepareResponse} este responsabilă pentru procesarea răspunsului JSON
 * generat de ChatGPT și extragerea informațiilor despre analizele medicale într-o
 * formă structurată sub forma unor obiecte {@link Analysis}.
 * <p>
 * Această clasă se ocupă de extragerea blocului JSON marcat de delimitatorii ```json și ``` și
 * transformarea sa într-o listă de obiecte {@code Analysis}.
 * </p>
 *
 * @author [Lucian]
 */

public class PrepareResponse {

    /**
     * Procesează răspunsul primit de la ChatGPT și extrage lista de analize medicale.
     *
     * @param chatGPTResponse Răspunsul complet în format JSON întors de modelul ChatGPT.
     *                        Acest răspuns trebuie să conțină un câmp "choices" care include o
     *                        secțiune "content" cu bloc JSON delimitat de ```json ... ```.
     * @return Lista de obiecte {@link Analysis} extrase din răspuns.
     * @throws JsonProcessingException Dacă apar erori la parsarea JSON-ului.
     * @throws IllegalStateException Dacă blocul JSON nu este găsit în răspunsul text.
     */
    public static List<Analysis> processResponse(String chatGPTResponse) throws JsonProcessingException {
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

        if (!matcher.find()) {
            throw new IllegalStateException("JSON block not found in the content!");
        }

        String extractedJson = matcher.group(1);
        JsonNode extractedJsonNode = mapper.readTree(extractedJson);

        JsonNode resultsNode = extractedJsonNode.get("results");
        if (resultsNode == null || !resultsNode.isArray()) {
            throw new IllegalStateException("'results' field missing or is not an array in the JSON content.");
        }

        List<Analysis> allAnalyses = new ArrayList<>();
        for (JsonNode result : resultsNode) {
            // Validate all required fields exist
            if (!result.has("denumireAnaliza") || !result.has("rezultat") ||
                    !result.has("intervalReferinta") || !result.has("severityRank")) {
                throw new IllegalStateException("Missing required field in analysis result");
            }

            String denumire = result.get("denumireAnaliza").asText();
            double rezultat = result.get("rezultat").asDouble();
            String intervalReferinta = result.get("intervalReferinta").asText();
            String severityRank = result.get("severityRank").asText();

            Analysis analysis = new Analysis(denumire, rezultat, intervalReferinta, severityRank);
            allAnalyses.add(analysis);
        }

        return allAnalyses;
    }

    public static String processObservation(String chatGPTResponse) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(chatGPTResponse);

        JsonNode contentNode = rootNode
                .path("choices")
                .get(0)
                .path("message")
                .path("content");

        if (contentNode.isMissingNode() || contentNode.isNull()) {
            throw new IllegalStateException("Missing 'content' in ChatGPT response.");
        }

        String contentString = contentNode.asText().trim();

        Pattern pattern = Pattern.compile("```json\\s*(\\{.*?\\})\\s*```", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(contentString);

        if (matcher.find()) {
            String extractedJson = matcher.group(1);
            JsonNode extractedJsonNode = mapper.readTree(extractedJson);
            JsonNode observationNode = extractedJsonNode.get("observation");

            if (observationNode != null && !observationNode.isNull()) {
                return observationNode.asText().trim();
            } else {
                throw new IllegalStateException("'observation' field missing in the JSON content.");
            }
        }

        return contentString;
    }

}




