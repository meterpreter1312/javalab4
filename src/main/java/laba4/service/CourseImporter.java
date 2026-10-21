package laba4.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import laba4.domain.Course;
import laba4.domain.CourseImportException;

public class CourseImporter {

    private int processedCount;

    public int getProcessedCount() {
        return processedCount;
    }

    public List<Course> importFrom(Reader reader) throws CourseImportException, IOException {
        List<Course> courses = new ArrayList<>();
        processedCount = 0;
        int recordNumber = 0;
        try (BufferedReader br = new BufferedReader(reader)) {
            String line;
            while ((line = br.readLine()) != null) {
                recordNumber = recordNumber + 1;
                if (line.isBlank()) {
                    continue;
                }
                try {
                    Course course = CourseParser.parse(line);
                    courses.add(course);
                    processedCount = processedCount + 1;
                } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
                    throw new CourseImportException(recordNumber, "malformed record", e);
                }
            }
        }
        return courses;
    }
}
