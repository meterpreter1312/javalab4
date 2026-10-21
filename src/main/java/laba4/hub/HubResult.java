package laba4.hub;

import laba4.domain.Course;

public class HubResult {

    private final boolean success;
    private final HubErrorCode errorCode;
    private final String message;
    private final Throwable cause;
    private final int processedCount;
    private final Course course;

    private HubResult(boolean success, HubErrorCode errorCode, String message, Throwable cause,
                      int processedCount, Course course) {
        this.success = success;
        this.errorCode = errorCode;
        this.message = message;
        this.cause = cause;
        this.processedCount = processedCount;
        this.course = course;
    }

    public static HubResult ok() {
        return new HubResult(true, null, null, null, 0, null);
    }

    public static HubResult ok(int processedCount) {
        return new HubResult(true, null, null, null, processedCount, null);
    }

    public static HubResult ok(Course course) {
        return new HubResult(true, null, null, null, 0, course);
    }

    public static HubResult fail(HubErrorCode errorCode, String message, Throwable cause) {
        return new HubResult(false, errorCode, message, cause, 0, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public HubErrorCode getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }

    public Throwable getCause() {
        return cause;
    }

    public int getProcessedCount() {
        return processedCount;
    }

    public Course getCourse() {
        return course;
    }
}
