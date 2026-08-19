package se.chasacademy.fritidslagret.exception;

public final class EntityNotFoundException extends FritidslagretException {
    private static final long serialVersionUID = 1L;

    public EntityNotFoundException(String message) {
        super(message);
    }
}
