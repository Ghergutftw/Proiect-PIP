package services;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatGPTResponse {
    @JsonProperty("id")
    private String id;

    @JsonProperty("object")
    private String object;

    @JsonProperty("model")
    private String model;

    @JsonProperty("choices")
    private List<Choice> choices;

    public String getMessageContent() {
        return choices != null && !choices.isEmpty() ? choices.get(0).message.content : null;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)  // Ignore unexpected fields like "index"
    public static class Choice {
        @JsonProperty("message")
        private Message message;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Message {
        @JsonProperty("role")
        private String role;

        @JsonProperty("content")
        private String content;
    }
}
