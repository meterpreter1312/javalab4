package laba4.domain;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class Student {

    private final String id;
    private final String name;
    private final Set<String> completedCodes = new LinkedHashSet<>();
    private final Set<String> enrolledCodes = new LinkedHashSet<>();

    public Student(String id, String name) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be blank");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<String> getCompletedCodes() {
        return Collections.unmodifiableSet(completedCodes);
    }

    public Set<String> getEnrolledCodes() {
        return Collections.unmodifiableSet(enrolledCodes);
    }

    public void completeCourse(String courseCode) {
        completedCodes.add(CourseCode.normalize(courseCode));
    }

    public void enrollCourse(String courseCode) {
        enrolledCodes.add(CourseCode.normalize(courseCode));
    }

    public boolean hasCompleted(String courseCode) {
        return completedCodes.contains(courseCode);
    }

    public boolean isEnrolledIn(String courseCode) {
        return enrolledCodes.contains(courseCode);
    }

    @Override
    public String toString() {
        return "Student{id=" + id + ", name=" + name + "}";
    }
}
