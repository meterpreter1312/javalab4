package laba4.demo;

public class TryWithResourcesDemo {

    public static void main(String[] args) {
        System.out.println("=== success ===");
        try (LoggedResource resource = new LoggedResource("reader-1")) {
            resource.use();
        }
        System.out.println("result: no exception");

        System.out.println("=== failure in use ===");
        try (LoggedResource resource = new LoggedResource("reader-2")) {
            resource.setFailOnUse(true);
            resource.use();
        } catch (IllegalStateException e) {
            System.out.println("caught: " + e.getMessage());
            System.out.println("suppressed count: " + e.getSuppressed().length);
        }

        System.out.println("=== failure in use and in close ===");
        try (LoggedResource resource = new LoggedResource("reader-3")) {
            resource.setFailOnUse(true);
            resource.setFailOnClose(true);
            resource.use();
        } catch (IllegalStateException e) {
            System.out.println("primary: " + e.getMessage());
            System.out.println("suppressed count: " + e.getSuppressed().length);
            for (Throwable suppressed : e.getSuppressed()) {
                System.out.println("suppressed: " + suppressed.getMessage());
            }
        }
    }
}
