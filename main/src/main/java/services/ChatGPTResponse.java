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
    private List<Choice> choices; // aici primeste ce are nevoie lucian

    // Getter pentru lista de choices
    public List<Choice> getChoices() {
        return choices;
    } // aici returneaza ce are lucian nevoie

    public String getMessageContent() {
        return choices != null && !choices.isEmpty() ? choices.get(0).message.content : null;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)  // Ignore unexpected fields like "index"
    public static class Choice {
        @JsonProperty("message")
        private Message message;

        public Message getMessage() {
            return message;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Message {
        @JsonProperty("role")
        private String role;

        @JsonProperty("content")
        private String content;

        public String getContent() {
            return content;
        }
    }
}
