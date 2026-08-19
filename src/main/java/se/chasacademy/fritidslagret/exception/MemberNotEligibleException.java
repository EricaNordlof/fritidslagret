package se.chasacademy.fritidslagret.exception;

public final class MemberNotEligibleException extends LoanRuleException {
    private static final long serialVersionUID = 1L;

    public MemberNotEligibleException(String message) {
        super(message);
    }
}
