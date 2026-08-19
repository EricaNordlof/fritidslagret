package se.chasacademy.fritidslagret.exception;

public final class LoanLimitExceededException extends LoanRuleException {
    private static final long serialVersionUID = 1L;

    public LoanLimitExceededException(String message) {
        super(message);
    }
}
