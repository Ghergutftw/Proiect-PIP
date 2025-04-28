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

            String body = "{\n" +
                    "  \"model\": \"gpt-4o-mini\",\n" +
                    "  \"messages\": [\n" +
                    "    {\n" +
                    "      \"role\": \"user\",\n" +
                    "      \"content\": \"Attach a severity rank for each analysis I will give you and I want the response to be in a json format {\\\"results\\\":[{\\\"denumireAnaliza\\\":\\\"Hemoglobin\\\",\\\"rezultat\\\":15.2,\\\"intervalReferinta\\\":\\\"13.5-17.5\\\"},{\\\"denumireAnaliza\\\":\\\"White Blood Cells\\\",\\\"rezultat\\\":13.2,\\\"intervalReferinta\\\":\\\"4.0-11.0\\\"},{\\\"denumireAnaliza\\\":\\\"Platelets\\\",\\\"rezultat\\\":200,\\\"intervalReferinta\\\":\\\"150-400\\\"},{\\\"denumireAnaliza\\\":\\\"Glucose (fasting)\\\",\\\"rezultat\\\":110,\\\"intervalReferinta\\\":\\\"70-99\\\"},{\\\"denumireAnaliza\\\":\\\"Cholesterol\\\",\\\"rezultat\\\":180,\\\"intervalReferinta\\\":\\\"125-200\\\"},{\\\"denumireAnaliza\\\":\\\"HDL Cholesterol\\\",\\\"rezultat\\\":35,\\\"intervalReferinta\\\":\\\"40-60\\\"},{\\\"denumireAnaliza\\\":\\\"LDL Cholesterol\\\",\\\"rezultat\\\":140,\\\"intervalReferinta\\\":\\\"0-129\\\"},{\\\"denumireAnaliza\\\":\\\"Triglycerides\\\",\\\"rezultat\\\":175,\\\"intervalReferinta\\\":\\\"0-150\\\"},{\\\"denumireAnaliza\\\":\\\"Creatinine\\\",\\\"rezultat\\\":1,\\\"intervalReferinta\\\":\\\"0.6-1.3\\\"},{\\\"denumireAnaliza\\\":\\\"Urea\\\",\\\"rezultat\\\":50,\\\"intervalReferinta\\\":\\\"17-43\\\"},{\\\"denumireAnaliza\\\":\\\"C-Reactive Protein\\\",\\\"rezultat\\\":0.5,\\\"intervalReferinta\\\":\\\"0.0-3.0\\\"},{\\\"denumireAnaliza\\\":\\\"ALT (SGPT)\\\",\\\"rezultat\\\":60,\\\"intervalReferinta\\\":\\\"7-56\\\"},{\\\"denumireAnaliza\\\":\\\"AST (SGOT)\\\",\\\"rezultat\\\":35,\\\"intervalReferinta\\\":\\\"5-40\\\"},{\\\"denumireAnaliza\\\":\\\"Vitamin D\\\",\\\"rezultat\\\":18,\\\"intervalReferinta\\\":\\\"20-50\\\"},{\\\"denumireAnaliza\\\":\\\"Calcium\\\",\\\"rezultat\\\":9.1,\\\"intervalReferinta\\\":\\\"8.6-10.3\\\"},{\\\"denumireAnaliza\\\":\\\"Iron\\\",\\\"rezultat\\\":55,\\\"intervalReferinta\\\":\\\"60-170\\\"}]}\"\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}\n";

//            String body = """
//                    {
//                        "model": "gpt-4o-mini",
//                        "messages": [
//                            {
//                                "role": "user",
//                                "content": "%s"
//                            }
//                        ]
//                    }""".formatted(message);


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
}
