package laba4.domain;

public class CourseParseException extends Exception {

    public CourseParseException(String message) {
        super(message);
    }

    public CourseParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
