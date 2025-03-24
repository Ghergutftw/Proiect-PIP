package services;

import io.github.cdimascio.dotenv.Dotenv;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ChatGPTService {

//    TODO : Lucian , poti modifica tipull de raspuns returnat
    public String getChatGPTResponse(String message) {
        try {
            // Load environment variables from .env file
            Dotenv dotenv = Dotenv.load();

            // Se va modifica din DUMMY_KEY -> OPEN_API_KEY daca se doreste comunicarea
            String apiKey = dotenv.get("OPENAI_API_KEY");
            if (apiKey == null || apiKey.isEmpty()) {
                System.err.println("API key not provided! Please set the OPENAI_API_KEY in the .env file.");
                System.exit(1);
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
            return response.body();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
