package by.bsu.dependency.exceptions;

public class NoSuchBeanDefinitionException extends RuntimeException {
    public NoSuchBeanDefinitionException(String message) {
        super(message);
    }
    public NoSuchBeanDefinitionException() {
      super("There is no such bean name.");
    }
}
