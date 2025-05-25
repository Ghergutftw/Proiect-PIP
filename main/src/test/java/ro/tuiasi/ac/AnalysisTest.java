package ro.tuiasi.ac;

import org.junit.jupiter.api.Test;
import services.Analysis;

import static org.junit.jupiter.api.Assertions.*;

class AnalysisTest {

    @Test
    void constructor_ShouldSetAllFields() {
        Analysis analysis = new Analysis("Test", 1.0, "0-2", "Low");

        assertEquals("Test", analysis.getDenumireAnaliza());
        assertEquals(1.0, analysis.getRezultat());
        assertEquals("0-2", analysis.getIntervalReferinta());
        assertEquals("Low", analysis.getSeveritate());
    }

    @Test
    void equalsAndHashCode_ShouldWorkCorrectly() {
        Analysis analysis1 = new Analysis("Test", 1.0, "0-2", "Low");
        Analysis analysis2 = new Analysis("Test", 1.0, "0-2", "Low");
        Analysis analysis3 = new Analysis("Different", 2.0, "1-3", "Medium");

        assertEquals(analysis1, analysis2);
        assertNotEquals(analysis1, analysis3);
        assertEquals(analysis1.hashCode(), analysis2.hashCode());
        assertNotEquals(analysis1.hashCode(), analysis3.hashCode());
    }

    @Test
    void toString_ShouldContainAllFields() {
        Analysis analysis = new Analysis("Test", 1.0, "0-2", "Low");
        String str = analysis.toString();

        assertTrue(str.contains("Test"));
        assertTrue(str.contains("1.0"));
        assertTrue(str.contains("0-2"));
        assertTrue(str.contains("Low"));
    }
}