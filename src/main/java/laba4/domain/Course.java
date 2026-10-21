package laba4.domain;

public class Course {

    public static final int MIN_HOURS = 10;
    public static final int MAX_HOURS = 100;

    private final String code;
    private final String title;
    private final int hours;
    private final int capacity;
    private final CourseStatus status;
    private final String prerequisiteCode;
    private int seatsLeft;
    private int completedHours;

    public Course(String code, String title, int hours, int capacity, CourseStatus status, String prerequisiteCode) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title must not be blank");
        }
        if (hours < MIN_HOURS || hours > MAX_HOURS) {
            throw new IllegalArgumentException("hours must be between " + MIN_HOURS + " and " + MAX_HOURS + ", got " + hours);
        }
        if (capacity < 0) {
            throw new IllegalArgumentException("capacity must not be negative");
        }
        if (status == null) {
            throw new IllegalArgumentException("status must not be null");
        }
        this.code = CourseCode.normalize(code);
        this.title = title;
        this.hours = hours;
        this.capacity = capacity;
        this.status = status;
        this.prerequisiteCode = prerequisiteCode == null || prerequisiteCode.isBlank()
                ? null
                : CourseCode.normalize(prerequisiteCode);
        this.seatsLeft = capacity;
    }

    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public int getHours() {
        return hours;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getSeatsLeft() {
        return seatsLeft;
    }

    public CourseStatus getStatus() {
        return status;
    }

    public String getPrerequisiteCode() {
        return prerequisiteCode;
    }

    public int getCompletedHours() {
        return completedHours;
    }

    public int completeHours(int hours) {
        if (hours <= 0) {
            throw new IllegalArgumentException("hours to complete must be positive");
        }
        completedHours = completedHours + hours;
        return completedHours;
    }

    public boolean hasSeats() {
        return seatsLeft > 0;
    }

    public void reserveSeat() {
        if (seatsLeft <= 0) {
            throw new IllegalStateException("no seats left in " + code);
        }
        seatsLeft = seatsLeft - 1;
    }

    @Override
    public String toString() {
        return "Course{code=" + code + ", title=" + title + ", hours=" + hours + "}";
    }
}
