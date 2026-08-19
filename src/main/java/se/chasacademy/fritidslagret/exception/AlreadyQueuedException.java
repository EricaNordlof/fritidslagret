package se.chasacademy.fritidslagret.exception;

public final class AlreadyQueuedException extends LoanRuleException {
    private static final long serialVersionUID = 1L;

    public AlreadyQueuedException(String message) {
        super(message);
    }
}
