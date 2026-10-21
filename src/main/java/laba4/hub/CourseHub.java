package laba4.hub;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import laba4.domain.Course;
import laba4.domain.CourseCode;
import laba4.domain.CourseImportException;
import laba4.domain.CourseParseException;
import laba4.domain.EnrollmentRejectedException;
import laba4.domain.Student;
import laba4.service.CourseImporter;
import laba4.service.CourseParser;
import laba4.service.EnrollmentService;

public class CourseHub {

    public static final int PRICE_PER_HOUR = 100;

    private final Map<String, Course> courses = new HashMap<>();
    private final Map<String, Student> students = new HashMap<>();
    private final EnrollmentService enrollmentService = new EnrollmentService();

    public void addCourse(Course course) {
        if (course == null) {
            throw new IllegalArgumentException("course must not be null");
        }
        courses.put(course.getCode(), course);
    }

    public void addStudent(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("student must not be null");
        }
        students.put(student.getId(), student);
    }

    public int getCourseCount() {
        return courses.size();
    }

    public Optional<Course> findCourse(String code) {
        if (code == null) {
            throw new IllegalArgumentException("code must not be null");
        }
        String normalized = CourseCode.normalize(code);
        return Optional.ofNullable(courses.get(normalized));
    }

    public Course parseCourse(String line) throws CourseParseException {
        if (line == null) {
            throw new IllegalArgumentException("line must not be null");
        }
        try {
            return CourseParser.parse(line);
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            throw new CourseParseException("cannot parse course record", e);
        }
    }

    public void enroll(String studentId, String courseId) throws EnrollmentRejectedException {
        if (studentId == null || studentId.isBlank()) {
            throw new IllegalArgumentException("studentId must not be blank");
        }
        if (courseId == null || courseId.isBlank()) {
            throw new IllegalArgumentException("courseId must not be blank");
        }
        Student student = students.get(studentId);
        if (student == null) {
            throw new IllegalArgumentException("unknown student: " + studentId);
        }
        String normalized = CourseCode.normalize(courseId);
        Course course = courses.get(normalized);
        if (course == null) {
            throw new IllegalArgumentException("unknown course: " + normalized);
        }
        enrollmentService.enroll(student, course);
    }

    public HubResult enrollSafe(String studentId, String courseId) {
        if (studentId == null || studentId.isBlank()) {
            return HubResult.fail(HubErrorCode.INVALID_ARGUMENT, "studentId must not be blank", null);
        }
        if (courseId == null || courseId.isBlank()) {
            return HubResult.fail(HubErrorCode.INVALID_ARGUMENT, "courseId must not be blank", null);
        }
        String normalized;
        try {
            normalized = CourseCode.normalize(courseId);
        } catch (IllegalArgumentException e) {
            return HubResult.fail(HubErrorCode.INVALID_COURSE_CODE, "invalid course code", e);
        }
        Course course = courses.get(normalized);
        if (course == null) {
            return HubResult.fail(HubErrorCode.COURSE_NOT_FOUND, "course not found", null);
        }
        Student student = students.get(studentId);
        if (student == null) {
            return HubResult.fail(HubErrorCode.STUDENT_NOT_FOUND, "student not found", null);
        }
        try {
            enrollmentService.enroll(student, course);
        } catch (EnrollmentRejectedException e) {
            return HubResult.fail(HubErrorCode.ENROLLMENT_REJECTED, e.getMessage(), e);
        }
        return HubResult.ok();
    }

    public HubResult importCourses(Reader reader) {
        if (reader == null) {
            throw new IllegalArgumentException("reader must not be null");
        }
        CourseImporter importer = new CourseImporter();
        List<Course> imported;
        try {
            imported = importer.importFrom(reader);
        } catch (CourseImportException e) {
            return HubResult.fail(HubErrorCode.IMPORT_FAILED, e.getMessage(), e);
        } catch (IOException e) {
            return HubResult.fail(HubErrorCode.IMPORT_FAILED, "import failed", e);
        }
        for (Course course : imported) {
            addCourse(course);
        }
        return HubResult.ok(importer.getProcessedCount());
    }

    public int calculatePrice(Course course) {
        if (course == null) {
            throw new IllegalArgumentException("course must not be null");
        }
        return course.getHours() * PRICE_PER_HOUR;
    }
}
