package laba4.service;

import laba4.domain.Course;
import laba4.domain.CourseStatus;

public final class CourseParser {

    private CourseParser() {
    }

    public static Course parse(String line) {
        String[] parts = line.split(";", -1);
        String code = parts[0].trim();
        String title = parts[1].trim();
        int hours = Integer.parseInt(parts[2].trim());
        int capacity = Integer.parseInt(parts[3].trim());
        return new Course(code, title, hours, capacity, CourseStatus.OPEN, null);
    }
}
