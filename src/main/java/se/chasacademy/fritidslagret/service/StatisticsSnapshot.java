package se.chasacademy.fritidslagret.service;

import java.util.List;

public final class StatisticsSnapshot {
    private final long activeLoans;
    private final long totalMembers;
    private final long availableEquipment;
    private final List<EquipmentLoanCount> mostBorrowedEquipment;

    public StatisticsSnapshot(
            long activeLoans,
            long totalMembers,
            long availableEquipment,
            List<EquipmentLoanCount> mostBorrowedEquipment) {
        this.activeLoans = activeLoans;
        this.totalMembers = totalMembers;
        this.availableEquipment = availableEquipment;
        this.mostBorrowedEquipment = List.copyOf(mostBorrowedEquipment);
    }

    public long getActiveLoans() {
        return activeLoans;
    }

    public long getTotalMembers() {
        return totalMembers;
    }

    public long getAvailableEquipment() {
        return availableEquipment;
    }

    public List<EquipmentLoanCount> getMostBorrowedEquipment() {
        return mostBorrowedEquipment;
    }
}
