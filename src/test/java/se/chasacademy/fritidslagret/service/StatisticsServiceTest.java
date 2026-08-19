package se.chasacademy.fritidslagret.service;

import org.junit.jupiter.api.Test;
import se.chasacademy.fritidslagret.domain.loan.Loan;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StatisticsServiceTest {
    @Test
    void snapshotCountsMembersAvailabilityActiveLoansAndMostBorrowedEquipment() {
        ServiceFixture fixture = new ServiceFixture();
        fixture.addMember("MED-1");
        fixture.addMember("MED-2");
        fixture.addBicycle("CYK-1");
        fixture.addBicycle("CYK-2");

        Loan first = fixture.loanService.borrow("MED-1", "CYK-1");
        fixture.loanService.returnEquipment(first.getId(), "MED-1");
        fixture.loanService.borrow("MED-2", "CYK-1");
        fixture.loanService.borrow("MED-1", "CYK-2");

        StatisticsSnapshot snapshot = new StatisticsService(
                fixture.equipmentRepository,
                fixture.memberRepository,
                fixture.loanRepository).getSnapshot();

        assertEquals(2L, snapshot.getActiveLoans());
        assertEquals(2L, snapshot.getTotalMembers());
        assertEquals(0L, snapshot.getAvailableEquipment());
        assertEquals(1, snapshot.getMostBorrowedEquipment().size());
        assertEquals("CYK-1", snapshot.getMostBorrowedEquipment().get(0).getEquipment().getId());
        assertEquals(2L, snapshot.getMostBorrowedEquipment().get(0).getLoanCount());
    }
}
