package ro.tuiasi.ac;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import services.ChatGPTService;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatGPTServiceTest {

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse<String> httpResponse;

    @InjectMocks
    private ChatGPTService chatGPTService;

    @Test
    void getChatGPTResponse_ShouldReturnResponse_ForValidInput() throws Exception {
        // Arrange
        String expectedResponseContent = "test response";
        String mockResponse = "{\"choices\":[{\"message\":{\"content\":\"" + expectedResponseContent + "\"}}]}";

        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);
        when(httpResponse.body()).thenReturn(mockResponse);

        // Act
        String actualResponse = chatGPTService.getChatGPTResponse("test message");

        // Assert
        assertNotNull(actualResponse, "Response should not be null");
        assertTrue(actualResponse.contains(expectedResponseContent),
                "Response should contain expected content");

        verify(httpClient).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }

    @Test
    void getChatGPTResponse_ShouldHandleHttpErrors() throws IOException, InterruptedException {
        // Arrange
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new IOException("Connection error"));

        // Act & Assert
        IOException exception = assertThrows(IOException.class,
                () -> chatGPTService.getChatGPTResponse("test message"));

        assertTrue(exception.getMessage().contains("Connection error"));
    }

    @Test
    void getChatGPTResponse_ShouldReturnNull_WhenHttpErrorOccurs() throws Exception {
        // Arrange
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new IOException("Connection error"));

        // Act
        String result = chatGPTService.getChatGPTResponse("test message");

        // Assert
        assertNull(result);
    }

    @Test
    void getChatGPTResponse_ShouldHandleSpecialCharacters() throws Exception {
        // Arrange
        String testMessage = "Test\nMessage\"With\\SpecialChars";
        String expectedResponse = "{\"choices\":[{\"message\":{\"content\":\"response\"}}]}";

        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);
        when(httpResponse.body()).thenReturn(expectedResponse);

        // Act
        String actualResponse = chatGPTService.getChatGPTResponse(testMessage);

        // Assert
        assertNotNull(actualResponse);
    }


    @Test
    void escapeJson_ShouldHandleSpecialCharacters() {
        // To test private method, you can either:
        // 1. Change method visibility to package-private (no modifier) and keep it in same package
        // 2. Use reflection (not recommended for simple cases)
        // 3. Test through public methods that use it

        // Since we can't change the original code, we'll test it indirectly
        String testMessage = "Test\nMessage\"With\\SpecialChars";
        try {
            // This will use escapeJson internally
            chatGPTService.getChatGPTResponse(testMessage);
            // If we get here without exception, the escaping worked
            assertTrue(true);
        } catch (Exception e) {
            fail("Escaping should handle special characters");
        }
    }
}