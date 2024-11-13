package by.bsu.dependency.exceptions;

public class ApplicationContextNotStartedException extends RuntimeException {
    public ApplicationContextNotStartedException(String message) {
        super(message);
    }
    public ApplicationContextNotStartedException() {
        super("The context has not been started.");
    }
}
