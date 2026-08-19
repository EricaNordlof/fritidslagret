package se.chasacademy.fritidslagret.exception;

public final class DuplicateIdException extends FritidslagretException {
    private static final long serialVersionUID = 1L;

    public DuplicateIdException(String message) {
        super(message);
    }
}
