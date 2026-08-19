package se.chasacademy.fritidslagret.domain.member;

import java.util.Objects;

public final class Member {
    private final String id;
    private final String name;
    private MemberStatus status;

    public Member(String id, String name) {
        this.id = requireText(id, "Medlems-ID");
        this.name = requireText(name, "Medlemsnamn");
        this.status = MemberStatus.ACTIVE;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public MemberStatus getStatus() {
        return status;
    }

    public boolean canBorrow() {
        return status == MemberStatus.ACTIVE;
    }

    public void changeStatus(MemberStatus newStatus) {
        this.status = Objects.requireNonNull(newStatus, "Medlemsstatus får inte vara null.");
    }

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
        return "%s | %s | %s".formatted(id, name, status.getDisplayName());
    }
}
