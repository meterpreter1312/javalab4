package laba4.domain;

public class CourseImportException extends Exception {

    private final int recordNumber;

    public CourseImportException(int recordNumber, String message, Throwable cause) {
        super("record #" + recordNumber + ": " + message, cause);
        this.recordNumber = recordNumber;
    }

    public CourseImportException(int recordNumber, String message) {
        super("record #" + recordNumber + ": " + message);
        this.recordNumber = recordNumber;
    }

    public int getRecordNumber() {
        return recordNumber;
    }
}
