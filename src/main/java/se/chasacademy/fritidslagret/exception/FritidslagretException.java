package se.chasacademy.fritidslagret.exception;

public class FritidslagretException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public FritidslagretException(String message) {
        super(message);
    }
}
