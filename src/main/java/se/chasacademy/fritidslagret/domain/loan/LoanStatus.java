package se.chasacademy.fritidslagret.domain.loan;

public enum LoanStatus {
    ACTIVE("Aktivt"),
    CLOSED("Avslutat");

    private final String displayName;

    LoanStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
