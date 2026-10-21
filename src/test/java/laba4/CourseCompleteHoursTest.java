package laba4;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import laba4.domain.Course;
import laba4.domain.CourseStatus;

class CourseCompleteHoursTest {

    @Test
    void completeHours_onValidInputReturnsAccumulatedTotal() {
        Course course = new Course("CS-101", "Java Basics", 40, 20, CourseStatus.OPEN, null);

        int total = course.completeHours(30);

        assertEquals(30, total);
    }
}
