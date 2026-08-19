package se.chasacademy.fritidslagret.exception;

public final class UnauthorizedReturnException extends LoanRuleException {
    private static final long serialVersionUID = 1L;

    public UnauthorizedReturnException(String message) {
        super(message);
    }
}
