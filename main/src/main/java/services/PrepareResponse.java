package services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        List<Analysis> allAnalyses = new ArrayList<>();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(chatGPTResponse);

        // Navighează prin JSON pentru a obține conținutul text al răspunsului
        JsonNode contentNode = rootNode
                .path("choices")
                .get(0)
                .path("message")
                .path("content");

        String contentString = contentNode.asText();

        // Extrage blocul JSON dintre delimitatorii ```json ... ```
        Pattern pattern = Pattern.compile("```json\\s*(\\{.*?\\})\\s*```", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(contentString);

        String extractedJson;
        if (matcher.find()) {
            extractedJson = matcher.group(1);
        } else {
            throw new IllegalStateException("JSON block not found in the content!");
        }

        // Parsează JSON-ul extras
        System.out.println("Extracted JSON:\n" + extractedJson);
        JsonNode extractedJsonNode = mapper.readTree(extractedJson);

        // Verifica daca results este null
        JsonNode resultsNode = extractedJsonNode.get("results");
        if (resultsNode == null || !resultsNode.isArray()) {
            throw new IllegalStateException("'results' field missing or is not an array in the JSON content.");
        }

        // Parcurge fiecare analiză și creează obiecte Analysis
        for (JsonNode result : extractedJsonNode.get("results")) {
            String denumire = result.path("denumireAnaliza").asText();
            double rezultat = result.path("rezultat").asDouble();
            String intervalReferinta = result.path("intervalReferinta").asText();
            String severityRank = result.path("severityRank").asText();

            Analysis analysis = new Analysis(denumire, rezultat, intervalReferinta, severityRank);
            allAnalyses.add(analysis);
        }

        return allAnalyses;
    }
    public static String processObservation(String chatGPTResponse) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(chatGPTResponse);

        // Get the content text from the first choice
        JsonNode contentNode = rootNode
                .path("choices")
                .get(0)
                .path("message")
                .path("content");

        if (contentNode.isMissingNode() || contentNode.isNull()) {
            throw new IllegalStateException("Missing 'content' in ChatGPT response.");
        }

        String contentString = contentNode.asText().trim();

        // Try to extract an `observation` from a JSON block if it exists
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

        // If no JSON block, return the plain content as observation
        return contentString;
    }

}


