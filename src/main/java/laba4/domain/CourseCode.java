package laba4.domain;

public final class CourseCode {

    private CourseCode() {
    }

    public static String normalize(String raw) {
        if (raw == null) {
            throw new IllegalArgumentException("code must not be null");
        }
        String cleaned = raw.trim().toUpperCase().replace(" ", "");
        if (!isValid(cleaned)) {
            throw new IllegalArgumentException("invalid course code: " + raw);
        }
        return cleaned;
    }

    public static boolean isValid(String code) {
        if (code == null || code.isEmpty()) {
            return false;
        }
        int dash = code.indexOf('-');
        if (dash <= 0 || dash != code.lastIndexOf('-') || dash == code.length() - 1) {
            return false;
        }
        String prefix = code.substring(0, dash);
        String suffix = code.substring(dash + 1);
        if (prefix.length() < 2 || prefix.length() > 4) {
            return false;
        }
        for (int i = 0; i < prefix.length(); i++) {
            if (!Character.isLetter(prefix.charAt(i))) {
                return false;
            }
        }
        if (suffix.length() < 2 || suffix.length() > 4) {
            return false;
        }
        for (int i = 0; i < suffix.length(); i++) {
            if (!Character.isDigit(suffix.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
