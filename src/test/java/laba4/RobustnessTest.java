package laba4;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import laba4.domain.Course;
import laba4.domain.CourseStatus;
import laba4.domain.Student;
import laba4.hub.CourseHub;

class RobustnessTest {

    @TempDir
    Path tempDir;

    @Test
    void fileContentIsReadFromTempDirNotWorkingDirectory() throws IOException {
        Path file = tempDir.resolve("courses.txt");
        Files.writeString(file, "CS-101");

        assertEquals("CS-101", Files.readString(file));
    }

    @Test
    void newCourseHasItsOwnState() {
        Course first = new Course("CS-101", "Java", 40, 20, CourseStatus.OPEN, null);
        Course second = new Course("CS-102", "Python", 30, 10, CourseStatus.OPEN, null);
        first.completeHours(40);

        assertEquals(0, second.getCompletedHours());
    }

    @Test
    void courseLookupDoesNotDependOnInsertionOrder() {
        CourseHub hub = new CourseHub();
        hub.addCourse(new Course("CS-102", "Python", 30, 10, CourseStatus.OPEN, null));
        hub.addCourse(new Course("CS-101", "Java", 40, 20, CourseStatus.OPEN, null));

        assertTrue(hub.findCourse("CS-101").isPresent());
        assertTrue(hub.findCourse("CS-102").isPresent());
    }

    @Test
    void studentToStringIsCheckedByContainsNotByFullText() {
        Student student = new Student("s7", "Petr");

        assertTrue(student.toString().contains("s7"));
        assertTrue(student.toString().contains("Petr"));
    }
}
