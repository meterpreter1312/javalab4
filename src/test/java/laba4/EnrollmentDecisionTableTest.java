package laba4;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import laba4.domain.Course;
import laba4.domain.CourseStatus;
import laba4.domain.EnrollmentReason;
import laba4.domain.EnrollmentRejectedException;
import laba4.domain.Student;
import laba4.service.EnrollmentService;

class EnrollmentDecisionTableTest {

    private final EnrollmentService service = new EnrollmentService();

    @ParameterizedTest(name = "status={0}, seats={1}, prereq={2}, prereqDone={3}, duplicate={4} => {5}")
    @CsvSource({
            "OPEN, 5, , false, false, OK",
            "OPEN, 5, CS-101, true, false, OK",
            "OPEN, 5, CS-101, false, false, MISSING_PREREQUISITE",
            "OPEN, 5, CS-101, true, true, DUPLICATE",
            "OPEN, 5, CS-101, false, true, MISSING_PREREQUISITE",
            "OPEN, 0, CS-101, false, false, NO_SEATS",
            "OPEN, 0, CS-101, true, true, NO_SEATS",
            "CLOSED, 5, CS-101, true, false, WRONG_STATUS",
            "CLOSED, 0, CS-101, false, true, WRONG_STATUS"
    })
    void enrollFollowsDecisionTable(CourseStatus status, int seats, String prereq,
                                    boolean prereqDone, boolean duplicate, String expected)
            throws EnrollmentRejectedException {
        Course course = new Course("CS-201", "Advanced Java", 60, seats, status, prereq);
        Student student = new Student("s1", "Ivan");
        if (prereqDone) {
            student.completeCourse("CS-101");
        }
        if (duplicate) {
            student.enrollCourse("CS-201");
        }

        if ("OK".equals(expected)) {
            service.enroll(student, course);
            assertTrue(student.isEnrolledIn("CS-201"));
            assertEquals(seats - 1, course.getSeatsLeft());
        } else {
            EnrollmentRejectedException exception = assertThrows(EnrollmentRejectedException.class,
                    () -> service.enroll(student, course));
            assertEquals(EnrollmentReason.valueOf(expected), exception.getReasonCode());
        }
    }
}
