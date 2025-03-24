package ro.tuiasi.ac;

import io.github.cdimascio.dotenv.Dotenv;
import services.ChatGPTService;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class App {
    public static void main(String[] args) {
        ChatGPTService chatGPTService = new ChatGPTService();
        String message = "Hello, how are you?";
        String response = chatGPTService.getChatGPTResponse(message);
        System.out.println(response);
    }
}