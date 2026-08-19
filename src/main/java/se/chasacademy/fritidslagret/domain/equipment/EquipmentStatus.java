package se.chasacademy.fritidslagret.domain.equipment;

public enum EquipmentStatus {
    AVAILABLE("Tillgänglig"),
    ON_LOAN("Utlånad");

    private final String displayName;

    EquipmentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
