package ro.tuiasi.ac;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import services.Analysis;
import services.PrepareResponse;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PrepareResponseTest {

    @Test
    void processResponse_ShouldHandleMultipleAnalyses() throws JsonProcessingException {
        String response = """
                {
                  "choices": [
                    {
                      "message": {
                        "content": "```json\\n{\\n  \\"results\\": [\\n    {\\"denumireAnaliza\\": \\"Test1\\", \\"rezultat\\": 1.0, \\"intervalReferinta\\": \\"0-2\\", \\"severityRank\\": \\"Low\\"},\\n    {\\"denumireAnaliza\\": \\"Test2\\", \\"rezultat\\": 3.0, \\"intervalReferinta\\": \\"2-4\\", \\"severityRank\\": \\"Medium\\"}\\n  ]\\n}\\n```"
                      }
                    }
                  ]
                }""";

        List<Analysis> result = PrepareResponse.processResponse(response);
        assertEquals(2, result.size());
        assertEquals("Test1", result.get(0).getDenumireAnaliza());
        assertEquals(1.0, result.get(0).getRezultat());
        assertEquals("0-2", result.get(0).getIntervalReferinta());
        assertEquals("Low", result.get(0).getSeveritate());

        assertEquals("Test2", result.get(1).getDenumireAnaliza());
        assertEquals(3.0, result.get(1).getRezultat());
        assertEquals("2-4", result.get(1).getIntervalReferinta());
        assertEquals("Medium", result.get(1).getSeveritate());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "missing denumireAnaliza",
            "missing rezultat",
            "missing intervalReferinta",
            "missing severityRank"
    })
    void processResponse_ShouldThrowException_ForMissingFields(String missingField) {
        String template = "{ \"choices\": [ { \"message\": { \"content\": \"```json\\n{ \\\"results\\\": [ {%s} ] }\\n```\" } } ] }";

        String json = switch (missingField) {
            case "missing denumireAnaliza" -> String.format(template,
                    "\\\"rezultat\\\": 1.0, \\\"intervalReferinta\\\": \\\"0-2\\\", \\\"severityRank\\\": \\\"Low\\\"");
            case "missing rezultat" -> String.format(template,
                    "\\\"denumireAnaliza\\\": \\\"Test\\\", \\\"intervalReferinta\\\": \\\"0-2\\\", \\\"severityRank\\\": \\\"Low\\\"");
            case "missing intervalReferinta" -> String.format(template,
                    "\\\"denumireAnaliza\\\": \\\"Test\\\", \\\"rezultat\\\": 1.0, \\\"severityRank\\\": \\\"Low\\\"");
            case "missing severityRank" -> String.format(template,
                    "\\\"denumireAnaliza\\\": \\\"Test\\\", \\\"rezultat\\\": 1.0, \\\"intervalReferinta\\\": \\\"0-2\\\"");
            default -> "";
        };

        Exception exception = assertThrows(IllegalStateException.class,
                () -> PrepareResponse.processResponse(json));

        assertEquals("Missing required field in analysis result", exception.getMessage());
    }

    @Test
    void processObservation_ShouldExtractObservationFromJson() throws JsonProcessingException {
        String response = """
                {
                  "choices": [
                    {
                      "message": {
                        "content": "```json\\n{\\n  \\"observation\\": \\"Test observation\\"\\n}\\n```"
                      }
                    }
                  ]
                }""";

        String observation = PrepareResponse.processObservation(response);
        assertEquals("Test observation", observation);
    }

    @Test
    void processObservation_ShouldReturnRawContent_WhenNoJsonBlock() throws JsonProcessingException {
        String response = """
                {
                  "choices": [
                    {
                      "message": {
                        "content": "Simple text response"
                      }
                    }
                  ]
                }""";

        String observation = PrepareResponse.processObservation(response);
        assertEquals("Simple text response", observation);
    }
}