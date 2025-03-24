package ro.tuiasi.ac;

import io.github.cdimascio.dotenv.Dotenv;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class App {
    public static void main(String[] args) throws IOException, InterruptedException {
        // Load environment variables from .env file
        Dotenv dotenv = Dotenv.load();

        // Retrieve the API key from the .env file
        String apiKey = dotenv.get("DUMMY_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("API key not provided! Please set the OPENAI_API_KEY in the .env file.");
            System.exit(1);
        }

        String body = """
                {
                    "model": "gpt-4",
                    "messages": [
                        {
                            "role": "user",
                            "content": "Tell me a good dad joke about cats"
                        }
                    ]
                }""";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
    }
}