package laba4;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import laba4.domain.Course;
import laba4.domain.CourseStatus;
import laba4.domain.EnrollmentReason;
import laba4.domain.EnrollmentRejectedException;
import laba4.domain.Student;
import laba4.service.EnrollmentService;

class EnrollmentFailureStateTest {

    private final EnrollmentService service = new EnrollmentService();

    @Test
    void enrollOnFullCourseThrowsReasonAndKeepsState() {
        Course course = new Course("CS-101", "Java", 40, 0, CourseStatus.OPEN, null);
        Student student = new Student("s1", "Ivan");

        EnrollmentRejectedException exception = assertThrows(EnrollmentRejectedException.class,
                () -> service.enroll(student, course));

        assertEquals(EnrollmentReason.NO_SEATS, exception.getReasonCode());
        assertEquals("s1", exception.getStudentId());
        assertEquals("CS-101", exception.getCourseId());
        assertEquals("Enrollment rejected: NO_SEATS", exception.getMessage());
        assertEquals(0, course.getSeatsLeft());
        assertTrue(student.getEnrolledCodes().isEmpty());
    }

    @Test
    void enrollOnDuplicateThrowsReasonAndDoesNotChangeSeats() {
        Course course = new Course("CS-101", "Java", 40, 5, CourseStatus.OPEN, null);
        Student student = new Student("s1", "Ivan");
        student.enrollCourse("CS-101");

        EnrollmentRejectedException exception = assertThrows(EnrollmentRejectedException.class,
                () -> service.enroll(student, course));

        assertEquals(EnrollmentReason.DUPLICATE, exception.getReasonCode());
        assertEquals(5, course.getSeatsLeft());
    }

    @Test
    void enrollOnClosedCourseThrowsWrongStatus() {
        Course course = new Course("CS-101", "Java", 40, 5, CourseStatus.CLOSED, null);
        Student student = new Student("s1", "Ivan");

        EnrollmentRejectedException exception = assertThrows(EnrollmentRejectedException.class,
                () -> service.enroll(student, course));

        assertEquals(EnrollmentReason.WRONG_STATUS, exception.getReasonCode());
        assertEquals(5, course.getSeatsLeft());
        assertTrue(student.getEnrolledCodes().isEmpty());
    }
}
