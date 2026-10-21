package laba4.domain;

public class EnrollmentRejectedException extends Exception {

    private final String studentId;
    private final String courseId;
    private final EnrollmentReason reasonCode;

    public EnrollmentRejectedException(String studentId, String courseId, EnrollmentReason reasonCode) {
        super("Enrollment rejected: " + reasonCode);
        this.studentId = studentId;
        this.courseId = courseId;
        this.reasonCode = reasonCode;
    }

    public EnrollmentRejectedException(String studentId, String courseId, EnrollmentReason reasonCode, Throwable cause) {
        super("Enrollment rejected: " + reasonCode, cause);
        this.studentId = studentId;
        this.courseId = courseId;
        this.reasonCode = reasonCode;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getCourseId() {
        return courseId;
    }

    public EnrollmentReason getReasonCode() {
        return reasonCode;
    }

    @Override
    public String toString() {
        return "EnrollmentRejectedException{reason=" + reasonCode + "}";
    }
}
