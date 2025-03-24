package ro.tuiasi.ac;

import services.ChatGPTService;

public class App {
    public static void main(String[] args) {
        ChatGPTService chatGPTService = new ChatGPTService();
        String message = "Hello, how are you?";
        String response = chatGPTService.getChatGPTResponse(message);
        System.out.println(response);
    }
}