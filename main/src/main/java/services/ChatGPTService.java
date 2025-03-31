package services;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ChatGPTService {

    public ChatGPTResponse getChatGPTResponse(String message) {
        try {
            Dotenv dotenv = Dotenv.load();
            String apiKey = dotenv.get("OPENAI_API_KEY");

            if (apiKey == null || apiKey.isEmpty()) {
                throw new IllegalStateException("API key not really provided! Please set the OPENAI_API_KEY in the .env file.");
            }

            String body = """
                    {
                        "model": "gpt-4o-mini",
                        "messages": [
                            {
                                "role": "user",
                                "content": "%s"
                            }
                        ]
                    }""".formatted(message);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Convert JSON string to Java object
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(response.body(), ChatGPTResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
