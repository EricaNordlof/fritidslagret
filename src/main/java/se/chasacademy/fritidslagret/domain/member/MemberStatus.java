package se.chasacademy.fritidslagret.domain.member;

public enum MemberStatus {
    ACTIVE("Aktiv"),
    INACTIVE("Inaktiv"),
    SUSPENDED("Avstängd");

    private final String displayName;

    MemberStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
