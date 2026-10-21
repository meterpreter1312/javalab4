package laba4.demo;

public class LoggedResource implements AutoCloseable {

    private final String name;
    private boolean failOnUse;
    private boolean failOnClose;

    public LoggedResource(String name) {
        this.name = name;
        System.out.println("open " + name);
    }

    public void setFailOnUse(boolean failOnUse) {
        this.failOnUse = failOnUse;
    }

    public void setFailOnClose(boolean failOnClose) {
        this.failOnClose = failOnClose;
    }

    public void use() {
        System.out.println("use " + name);
        if (failOnUse) {
            throw new IllegalStateException("use failed: " + name);
        }
    }

    @Override
    public void close() {
        System.out.println("close " + name);
        if (failOnClose) {
            throw new IllegalStateException("close failed: " + name);
        }
    }
}
