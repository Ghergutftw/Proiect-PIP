package services;

import io.github.cdimascio.dotenv.Dotenv;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ChatGPTService {

    public String getChatGPTResponse(String message) {
        try {
            Dotenv dotenv = Dotenv.load();
            String apiKey = dotenv.get("OPENAI_API_KEY");

            if (apiKey == null || apiKey.isEmpty()) {
                throw new IllegalStateException("API key not provided! Please set the OPENAI_API_KEY in the .env file.");
            }

            String body = String.format(
                    "{\n" +
                            "  \"model\": \"gpt-4o-mini\",\n" +
                            "  \"messages\": [\n" +
                            "    {\n" +
                            "      \"role\": \"user\",\n" +
                            "      \"content\": \"%s\"\n" +
                            "    }\n" +
                            "  ]\n" +
                            "}",
                    escapeJson(message)
            );


            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println(response.body());
            return response.body();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String escapeJson(String input) {
        return input
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

}
