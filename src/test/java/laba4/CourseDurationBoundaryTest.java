package laba4;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import laba4.domain.Course;
import laba4.domain.CourseStatus;

class CourseDurationBoundaryTest {

    @Test
    void courseWithHoursBelowMinimumIsRejected() {
        int belowMin = Course.MIN_HOURS - 1;

        assertThrows(IllegalArgumentException.class,
                () -> new Course("CS-101", "Java", belowMin, 20, CourseStatus.OPEN, null));
    }

    @Test
    void courseWithMinimumHoursIsAccepted() {
        Course course = new Course("CS-101", "Java", Course.MIN_HOURS, 20, CourseStatus.OPEN, null);

        assertEquals(Course.MIN_HOURS, course.getHours());
    }

    @Test
    void courseWithMaximumHoursIsAccepted() {
        Course course = new Course("CS-101", "Java", Course.MAX_HOURS, 20, CourseStatus.OPEN, null);

        assertEquals(Course.MAX_HOURS, course.getHours());
    }

    @Test
    void courseWithHoursAboveMaximumIsRejected() {
        int aboveMax = Course.MAX_HOURS + 1;

        assertThrows(IllegalArgumentException.class,
                () -> new Course("CS-101", "Java", aboveMax, 20, CourseStatus.OPEN, null));
    }
}
