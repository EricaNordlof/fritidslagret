package se.chasacademy.fritidslagret.exception;

public final class EquipmentUnavailableException extends LoanRuleException {
    private static final long serialVersionUID = 1L;

    public EquipmentUnavailableException(String message) {
        super(message);
    }
}
