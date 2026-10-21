package laba4;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import laba4.domain.Course;
import laba4.domain.CourseImportException;
import laba4.domain.CourseParseException;
import laba4.domain.CourseStatus;
import laba4.domain.EnrollmentReason;
import laba4.domain.EnrollmentRejectedException;
import laba4.domain.Student;
import laba4.hub.CourseHub;
import laba4.hub.HubErrorCode;
import laba4.hub.HubResult;

class CourseHubTest {

    private static final String VALID_LINE = "CS-101;Java;40;20";
    private static final String MALFORMED_LINE = "CS-102;Python;abc;20";

    static class FailingReadReader extends Reader {

        boolean closed;

        @Override
        public int read(char[] cbuf, int off, int len) throws IOException {
            throw new IOException("read failed");
        }

        @Override
        public void close() {
            closed = true;
        }
    }

    static class TrackedCloseReader extends BufferedReader {

        boolean closed;

        TrackedCloseReader(String data) {
            super(new StringReader(data));
        }

        @Override
        public void close() throws IOException {
            closed = true;
            super.close();
        }
    }

    private CourseHub hub;
    private Student student;

    @BeforeEach
    void setUp() {
        hub = new CourseHub();
        hub.addCourse(new Course("CS-101", "Java", 40, 5, CourseStatus.OPEN, null));
        student = new Student("s1", "Ivan");
        hub.addStudent(student);
    }

    @Test
    void importCoursesOnSuccessPublishesRecordsAndClosesReader() {
        TrackedCloseReader reader = new TrackedCloseReader("CS-103;Go;30;10\nCS-104;Kotlin;50;15\n");

        HubResult result = hub.importCourses(reader);

        assertTrue(result.isSuccess());
        assertEquals(2, result.getProcessedCount());
        assertEquals(3, hub.getCourseCount());
        assertTrue(reader.closed);
    }

    @Test
    void importCoursesOnMalformedRecordReturnsImportFailedWithCause() {
        HubResult result = hub.importCourses(new StringReader(MALFORMED_LINE + "\n"));

        assertFalse(result.isSuccess());
        assertEquals(HubErrorCode.IMPORT_FAILED, result.getErrorCode());
        assertInstanceOf(CourseImportException.class, result.getCause());
        assertEquals(1, ((CourseImportException) result.getCause()).getRecordNumber());
        assertInstanceOf(NumberFormatException.class, result.getCause().getCause());
    }

    @Test
    void importCoursesOnReadErrorReturnsImportFailedAndClosesReader() {
        FailingReadReader reader = new FailingReadReader();

        HubResult result = hub.importCourses(reader);

        assertFalse(result.isSuccess());
        assertEquals(HubErrorCode.IMPORT_FAILED, result.getErrorCode());
        assertInstanceOf(IOException.class, result.getCause());
        assertTrue(reader.closed);
    }

    @Test
    void importCoursesOnFailedImportDoesNotPublishAnyRecord() {
        hub.importCourses(new StringReader(MALFORMED_LINE + "\n"));

        assertEquals(1, hub.getCourseCount());
    }

    @Test
    void importCoursesWithNullReaderThrowsProgrammingError() {
        assertThrows(IllegalArgumentException.class, () -> hub.importCourses(null));
    }

    @Test
    void enrollSafeOnSuccessEnrollsStudent() {
        HubResult result = hub.enrollSafe("s1", "CS-101");

        assertTrue(result.isSuccess());
        assertTrue(student.isEnrolledIn("CS-101"));
        assertEquals(4, hub.findCourse("CS-101").get().getSeatsLeft());
    }

    @ParameterizedTest(name = "[{index}] studentId={0} => INVALID_ARGUMENT")
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void enrollSafeWithBlankStudentIdReturnsInvalidArgument(String studentId) {
        HubResult result = hub.enrollSafe(studentId, "CS-101");

        assertFalse(result.isSuccess());
        assertEquals(HubErrorCode.INVALID_ARGUMENT, result.getErrorCode());
    }

    @ParameterizedTest(name = "[{index}] courseId={0} => {1}")
    @CsvSource({
            "CS-404, COURSE_NOT_FOUND",
            "bad code, INVALID_COURSE_CODE"
    })
    void enrollSafeWithBadCourseIdReturnsStableCode(String courseId, HubErrorCode expected) {
        HubResult result = hub.enrollSafe("s1", courseId);

        assertFalse(result.isSuccess());
        assertEquals(expected, result.getErrorCode());
    }

    @Test
    void enrollSafeWithEmptyCourseIdReturnsInvalidArgument() {
        HubResult result = hub.enrollSafe("s1", "");

        assertFalse(result.isSuccess());
        assertEquals(HubErrorCode.INVALID_ARGUMENT, result.getErrorCode());
    }

    @Test
    void enrollSafeWithUnknownStudentReturnsStudentNotFound() {
        HubResult result = hub.enrollSafe("s99", "CS-101");

        assertFalse(result.isSuccess());
        assertEquals(HubErrorCode.STUDENT_NOT_FOUND, result.getErrorCode());
    }

    @Test
    void enrollSafeWithInvalidCodeKeepsCause() {
        HubResult result = hub.enrollSafe("s1", "!!!");

        assertEquals(HubErrorCode.INVALID_COURSE_CODE, result.getErrorCode());
        assertInstanceOf(IllegalArgumentException.class, result.getCause());
    }

    @Test
    void enrollSafeOnFullCourseReturnsRejectedWithCauseAndKeepsState() {
        hub.addCourse(new Course("CS-500", "Full", 20, 0, CourseStatus.OPEN, null));

        HubResult result = hub.enrollSafe("s1", "CS-500");

        assertFalse(result.isSuccess());
        assertEquals(HubErrorCode.ENROLLMENT_REJECTED, result.getErrorCode());
        assertInstanceOf(EnrollmentRejectedException.class, result.getCause());
        EnrollmentRejectedException cause = (EnrollmentRejectedException) result.getCause();
        assertEquals(EnrollmentReason.NO_SEATS, cause.getReasonCode());
        assertEquals("s1", cause.getStudentId());
        assertFalse(student.isEnrolledIn("CS-500"));
        assertEquals(0, hub.findCourse("CS-500").get().getSeatsLeft());
    }

    @Test
    void enrollSafeOnClosedCourseReturnsRejectedWithWrongStatus() {
        hub.addCourse(new Course("CS-600", "Closed", 20, 5, CourseStatus.CLOSED, null));

        HubResult result = hub.enrollSafe("s1", "CS-600");

        assertEquals(HubErrorCode.ENROLLMENT_REJECTED, result.getErrorCode());
        EnrollmentRejectedException cause = (EnrollmentRejectedException) result.getCause();
        assertEquals(EnrollmentReason.WRONG_STATUS, cause.getReasonCode());
    }

    @Test
    void enrollSafeOnMissingPrerequisiteReturnsRejected() {
        hub.addCourse(new Course("CS-700", "Advanced", 20, 5, CourseStatus.OPEN, "CS-999"));

        HubResult result = hub.enrollSafe("s1", "CS-700");

        assertEquals(HubErrorCode.ENROLLMENT_REJECTED, result.getErrorCode());
        EnrollmentRejectedException cause = (EnrollmentRejectedException) result.getCause();
        assertEquals(EnrollmentReason.MISSING_PREREQUISITE, cause.getReasonCode());
    }

    @Test
    void enrollCheckedOnSuccessChangesState() throws EnrollmentRejectedException {
        hub.enroll("s1", "CS-101");

        assertTrue(student.isEnrolledIn("CS-101"));
        assertEquals(4, hub.findCourse("CS-101").get().getSeatsLeft());
    }

    @Test
    void enrollCheckedOnFullCourseThrowsWithReason() {
        hub.addCourse(new Course("CS-500", "Full", 20, 0, CourseStatus.OPEN, null));

        EnrollmentRejectedException exception = assertThrows(EnrollmentRejectedException.class,
                () -> hub.enroll("s1", "CS-500"));

        assertEquals(EnrollmentReason.NO_SEATS, exception.getReasonCode());
        assertFalse(student.isEnrolledIn("CS-500"));
    }

    @Test
    void enrollCheckedWithUnknownStudentThrowsProgrammingError() {
        assertThrows(IllegalArgumentException.class, () -> hub.enroll("s99", "CS-101"));
    }

    @Test
    void findCourseWhenExistsReturnsCourse() {
        assertEquals("Java", hub.findCourse("CS-101").get().getTitle());
    }

    @Test
    void findCourseWhenMissingReturnsEmpty() {
        assertTrue(hub.findCourse("CS-404").isEmpty());
    }

    @Test
    void findCourseWithNullThrowsProgrammingError() {
        assertThrows(IllegalArgumentException.class, () -> hub.findCourse(null));
    }

    @Test
    void parseCourseOnNonNumericHoursThrowsWithNumberFormatCause() {
        CourseParseException exception = assertThrows(CourseParseException.class,
                () -> hub.parseCourse("CS-101;Java;forty;20"));

        assertInstanceOf(NumberFormatException.class, exception.getCause());
        assertTrue(exception.getMessage().contains("record"));
    }

    @Test
    void parseCourseOnInvalidDurationThrowsWithDomainCause() {
        CourseParseException exception = assertThrows(CourseParseException.class,
                () -> hub.parseCourse("CS-101;Java;5;20"));

        assertEquals(IllegalArgumentException.class, exception.getCause().getClass());
    }

    @Test
    void parseCourseOnValidLineReturnsCourse() throws CourseParseException {
        Course course = hub.parseCourse("CS-101;Java;40;20");

        assertEquals("CS-101", course.getCode());
        assertEquals(40, course.getHours());
    }

    @Test
    void calculatePriceReturnsHoursMultipliedByRate() {
        Course course = hub.findCourse("CS-101").get();

        assertEquals(4000, hub.calculatePrice(course));
    }

    @Test
    void calculatePriceOnNullThrowsProgrammingError() {
        assertThrows(IllegalArgumentException.class, () -> hub.calculatePrice(null));
    }
}
