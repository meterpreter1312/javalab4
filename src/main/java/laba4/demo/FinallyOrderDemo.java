package laba4.demo;

public class FinallyOrderDemo {

    public static void main(String[] args) {
        System.out.println("--- return inside try ---");
        System.out.println("main got: " + returnInsideTry());

        System.out.println("--- return inside catch ---");
        System.out.println("main got: " + returnInsideCatch());

        System.out.println("--- throw inside try passes through finally ---");
        try {
            throwInsideTry();
        } catch (RuntimeException e) {
            System.out.println("main caught: " + e.getMessage());
        }

        System.out.println("--- throw inside finally hides original ---");
        try {
            throwInsideFinally();
        } catch (RuntimeException e) {
            System.out.println("main caught: " + e.getMessage());
        }

        System.out.println("--- no return statement inside finally blocks above ---");
    }

    static String returnInsideTry() {
        try {
            System.out.println("try");
            return "value from try";
        } finally {
            System.out.println("finally");
        }
    }

    static String returnInsideCatch() {
        try {
            System.out.println("try");
            throw new IllegalStateException("boom");
        } catch (IllegalStateException e) {
            System.out.println("catch");
            return "value from catch";
        } finally {
            System.out.println("finally");
        }
    }

    static void throwInsideTry() {
        try {
            System.out.println("try");
            throw new RuntimeException("failure from try");
        } finally {
            System.out.println("finally");
        }
    }

    static void throwInsideFinally() {
        try {
            System.out.println("try");
            throw new RuntimeException("original failure");
        } finally {
            System.out.println("finally");
            throw new RuntimeException("failure from finally");
        }
    }
}
