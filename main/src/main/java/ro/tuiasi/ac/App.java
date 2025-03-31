package ro.tuiasi.ac;

import services.ChatGPTResponse;
import services.ChatGPTService;

public class App {
    public static void main(String[] args) {
        ChatGPTService chatGPTService = new ChatGPTService();
        String message = "Hello, how are you?";
        ChatGPTResponse chatGPTResponse = chatGPTService.getChatGPTResponse(message);
        System.out.println(chatGPTResponse);
    }
}