package laba4.demo;

public class StackChainDemo {

    public static void main(String[] args) {
        String badRecord = "CS-101;Java;forty";

        System.out.println("=== crash in 4-method chain ===");
        try {
            System.out.println(totalHours(badRecord));
        } catch (NumberFormatException e) {
            e.printStackTrace(System.out);
            System.out.println("type: " + e.getClass().getName());
            System.out.println("message: " + e.getMessage());
            System.out.println("place: " + e.getStackTrace()[0]);
            System.out.println("path: " + callPath(e));
        }

        System.out.println("=== fixed boundary ===");
        try {
            System.out.println(importHours(badRecord));
        } catch (IllegalArgumentException e) {
            e.printStackTrace(System.out);
            System.out.println("type: " + e.getClass().getName());
            System.out.println("message: " + e.getMessage());
            System.out.println("place: " + e.getStackTrace()[0]);
            System.out.println("path: " + callPath(e));
        }

        System.out.println("valid record: " + importHours("CS-101;Java;40"));
    }

    static int importHours(String record) {
        String[] parts = record.split(";", -1);
        if (parts.length < 3) {
            throw new IllegalArgumentException("record must contain 3 fields");
        }
        String hoursField = parts[2].trim();
        if (!hoursField.matches("\\d+")) {
            throw new IllegalArgumentException("hours must be a number: " + hoursField);
        }
        return totalHours(record);
    }

    static int totalHours(String record) {
        return parseRecord(record);
    }

    static int parseRecord(String record) {
        String[] parts = record.split(";", -1);
        return extractHours(parts);
    }

    static int extractHours(String[] parts) {
        return parseNumber(parts[2].trim());
    }

    static int parseNumber(String text) {
        return Integer.parseInt(text);
    }

    static String callPath(Throwable e) {
        StringBuilder sb = new StringBuilder();
        StackTraceElement[] frames = e.getStackTrace();
        int limit = Math.min(frames.length, 5);
        for (int i = 0; i < limit; i++) {
            if (i > 0) {
                sb.append(" -> ");
            }
            sb.append(frames[i].getMethodName());
        }
        return sb.toString();
    }
}
