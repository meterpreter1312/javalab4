package laba4.service;

import laba4.domain.Course;
import laba4.domain.CourseStatus;
import laba4.domain.EnrollmentReason;
import laba4.domain.EnrollmentRejectedException;
import laba4.domain.Student;

public class EnrollmentService {

    public void enroll(Student student, Course course) throws EnrollmentRejectedException {
        if (student == null || course == null) {
            throw new IllegalArgumentException("student and course must not be null");
        }
        if (course.getStatus() != CourseStatus.OPEN) {
            throw new EnrollmentRejectedException(student.getId(), course.getCode(), EnrollmentReason.WRONG_STATUS);
        }
        if (!course.hasSeats()) {
            throw new EnrollmentRejectedException(student.getId(), course.getCode(), EnrollmentReason.NO_SEATS);
        }
        String prerequisite = course.getPrerequisiteCode();
        if (prerequisite != null && !student.hasCompleted(prerequisite)) {
            throw new EnrollmentRejectedException(student.getId(), course.getCode(), EnrollmentReason.MISSING_PREREQUISITE);
        }
        if (student.isEnrolledIn(course.getCode())) {
            throw new EnrollmentRejectedException(student.getId(), course.getCode(), EnrollmentReason.DUPLICATE);
        }
        course.reserveSeat();
        student.enrollCourse(course.getCode());
    }
}
