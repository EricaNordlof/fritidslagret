package se.chasacademy.fritidslagret.service;

import se.chasacademy.fritidslagret.domain.equipment.Equipment;

import java.util.Objects;

public final class EquipmentLoanCount {
    private final Equipment equipment;
    private final long loanCount;

    public EquipmentLoanCount(Equipment equipment, long loanCount) {
        this.equipment = Objects.requireNonNull(equipment);
        this.loanCount = loanCount;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public long getLoanCount() {
        return loanCount;
    }
}
