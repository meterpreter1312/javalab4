package laba4;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import laba4.domain.CourseCode;

class CourseCodeTest {

    @ParameterizedTest(name = "{0} -> {1}")
    @CsvSource({
            "cs-101, CS-101",
            "CS-101, CS-101",
            "'  cs-101  ', CS-101",
            "ma-202, MA-202",
            "it-07, IT-07",
            "web-99, WEB-99"
    })
    void normalize_returnsCanonicalForm(String input, String expected) {
        assertEquals(expected, CourseCode.normalize(input));
    }

    @ParameterizedTest(name = "{0} -> IllegalArgumentException")
    @ValueSource(strings = {"", "CS", "CS-", "-101", "CS101", "C-101", "CS-1A", "TOOLONG-101", "CS-12345", "12-101"})
    void normalize_rejectsInvalidCodes(String input) {
        assertThrows(IllegalArgumentException.class, () -> CourseCode.normalize(input));
    }
}
