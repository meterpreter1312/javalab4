package laba4;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.junit.jupiter.api.Test;

import laba4.domain.Course;
import laba4.domain.CourseImportException;
import laba4.hub.CourseHub;
import laba4.hub.HubErrorCode;
import laba4.hub.HubResult;
import laba4.service.CourseImporter;

class CourseImporterFailureTest {

    private static final String VALID_LINE = "CS-101;Java;40;20";
    private static final String MALFORMED_LINE = "CS-102;Python;abc;20";

    static class FailingCloseReader extends BufferedReader {

        FailingCloseReader(String data) {
            super(new StringReader(data));
        }

        @Override
        public void close() throws IOException {
            super.close();
            throw new IOException("close failed");
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

    @Test
    void malformedRecordWithCloseErrorHasPrimarySuppressedAndProcessedCount() {
        String data = VALID_LINE + "\n" + MALFORMED_LINE + "\nCS-103;Go;30;10\n";
        CourseImporter importer = new CourseImporter();

        CourseImportException exception = assertThrows(CourseImportException.class,
                () -> importer.importFrom(new FailingCloseReader(data)));

        assertEquals(2, exception.getRecordNumber());
        assertInstanceOf(NumberFormatException.class, exception.getCause());
        assertEquals(1, importer.getProcessedCount());
        assertEquals(1, exception.getSuppressed().length);
        assertInstanceOf(IOException.class, exception.getSuppressed()[0]);
        assertEquals("close failed", exception.getSuppressed()[0].getMessage());
    }

    @Test
    void failedImportPublishesNothingToHub() {
        String data = VALID_LINE + "\n" + MALFORMED_LINE + "\n";
        CourseHub hub = new CourseHub();

        HubResult result = hub.importCourses(new FailingCloseReader(data));

        assertFalse(result.isSuccess());
        assertEquals(HubErrorCode.IMPORT_FAILED, result.getErrorCode());
        assertInstanceOf(CourseImportException.class, result.getCause());
        assertEquals(2, ((CourseImportException) result.getCause()).getRecordNumber());
        assertEquals(1, result.getCause().getSuppressed().length);
        assertEquals(0, hub.getCourseCount());
    }

    @Test
    void successfulImportPublishesAllRecordsAndClosesReader() {
        String data = VALID_LINE + "\nCS-103;Go;30;10\nCS-104;Kotlin;50;15\n";
        CourseHub hub = new CourseHub();
        TrackedCloseReader reader = new TrackedCloseReader(data);

        HubResult result = hub.importCourses(reader);

        assertTrue(result.isSuccess());
        assertEquals(3, result.getProcessedCount());
        assertEquals(3, hub.getCourseCount());
        assertTrue(reader.closed);
    }

    @Test
    void malformedRecordWithoutCloseErrorHasNoSuppressed() {
        String data = MALFORMED_LINE + "\n";
        CourseImporter importer = new CourseImporter();

        CourseImportException exception = assertThrows(CourseImportException.class,
                () -> importer.importFrom(new StringReader(data)));

        assertEquals(0, exception.getSuppressed().length);
        assertEquals(0, importer.getProcessedCount());
    }

    @Test
    void importerReturnsAllCoursesOnFullSuccess() throws IOException, CourseImportException {
        String data = VALID_LINE + "\nCS-103;Go;30;10\n";
        CourseImporter importer = new CourseImporter();

        List<Course> courses = importer.importFrom(new StringReader(data));

        assertEquals(2, courses.size());
        assertEquals("CS-101", courses.get(0).getCode());
        assertEquals("CS-103", courses.get(1).getCode());
    }
}
