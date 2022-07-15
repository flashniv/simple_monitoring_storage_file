package ua.com.serverhelp.simplemetricstoragefile.entities.triggers.expressions;

public class ExpressionException extends Exception {
    public ExpressionException(String message, Exception e) {
        super(message, e);
    }
}
