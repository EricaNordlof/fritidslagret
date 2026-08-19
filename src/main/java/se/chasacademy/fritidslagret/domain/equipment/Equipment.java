package se.chasacademy.fritidslagret.domain.equipment;

import se.chasacademy.fritidslagret.exception.LoanRuleException;

import java.util.Objects;

/** Gemensam abstraktion för all utrustning som kan lånas. */
public abstract class Equipment implements LoanTerms {
    private final String id;
    private final String name;
    private EquipmentStatus status;

    protected Equipment(String id, String name) {
        this.id = requireText(id, "Utrustnings-ID");
        this.name = requireText(name, "Utrustningsnamn");
        this.status = EquipmentStatus.AVAILABLE;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public EquipmentStatus getStatus() {
        return status;
    }

    public boolean isAvailable() {
        return status == EquipmentStatus.AVAILABLE;
    }

    public void checkOut() {
        if (!isAvailable()) {
            throw new LoanRuleException("Utrustningen " + id + " är redan utlånad.");
        }
        status = EquipmentStatus.ON_LOAN;
    }

    public void checkIn() {
        if (isAvailable()) {
            throw new LoanRuleException("Utrustningen " + id + " är redan tillgänglig.");
        }
        status = EquipmentStatus.AVAILABLE;
    }

    public abstract String getTypeName();

    private static String requireText(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " får inte vara null.");
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " får inte vara tomt.");
        }
        return trimmed;
    }

    @Override
    public String toString() {
        return "%s | %s | %s | max %d dagar | %s".formatted(
                id, getTypeName(), name, getMaxLoanDays(), status.getDisplayName());
    }
}
