package se.chasacademy.fritidslagret.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.chasacademy.fritidslagret.domain.loan.Loan;
import se.chasacademy.fritidslagret.domain.member.MemberStatus;
import se.chasacademy.fritidslagret.exception.EquipmentUnavailableException;
import se.chasacademy.fritidslagret.exception.LoanLimitExceededException;
import se.chasacademy.fritidslagret.exception.LoanRuleException;
import se.chasacademy.fritidslagret.exception.MemberNotEligibleException;
import se.chasacademy.fritidslagret.exception.UnauthorizedReturnException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoanServiceTest {
    private ServiceFixture fixture;

    @BeforeEach
    void setUp() {
        fixture = new ServiceFixture();
        fixture.addMember("MED-1");
        fixture.addMember("MED-2");
        fixture.addMember("MED-3");
        fixture.addBicycle("CYK-1");
        fixture.addBicycle("CYK-2");
        fixture.addBicycle("CYK-3");
        fixture.addBicycle("CYK-4");
    }

    @Test
    void activeMemberCanBorrowAvailableEquipment() {
        Loan loan = fixture.loanService.borrow("MED-1", "CYK-1");

        assertTrue(loan.isActive());
        assertEquals(LocalDate.of(2026, 8, 19), loan.getPeriod().getLoanDate());
        assertEquals(LocalDate.of(2026, 8, 26), loan.getPeriod().getDueDate());
        assertFalse(fixture.inventoryService.getRequired("CYK-1").isAvailable());
    }

    @Test
    void thirdLoanIsAllowedButFourthIsRejectedWithoutChangingEquipment() {
        fixture.loanService.borrow("MED-1", "CYK-1");
        fixture.loanService.borrow("MED-1", "CYK-2");
        fixture.loanService.borrow("MED-1", "CYK-3");

        assertEquals(3, fixture.loanService.getActiveLoanCountForMember("MED-1"));
        assertThrows(LoanLimitExceededException.class,
                () -> fixture.loanService.borrow("MED-1", "CYK-4"));
        assertTrue(fixture.inventoryService.getRequired("CYK-4").isAvailable());
        assertEquals(3, fixture.loanService.getAllLoans().size());
    }

    @Test
    void alreadyBorrowedEquipmentCannotReceiveASecondLoan() {
        fixture.loanService.borrow("MED-1", "CYK-1");

        assertThrows(EquipmentUnavailableException.class,
                () -> fixture.loanService.borrow("MED-2", "CYK-1"));
        assertEquals(1, fixture.loanService.getActiveLoans().size());
    }

    @Test
    void inactiveMemberCannotBorrowAndEquipmentStaysAvailable() {
        fixture.memberService.changeStatus("MED-2", MemberStatus.INACTIVE);

        assertThrows(MemberNotEligibleException.class,
                () -> fixture.loanService.borrow("MED-2", "CYK-1"));
        assertTrue(fixture.inventoryService.getRequired("CYK-1").isAvailable());
        assertTrue(fixture.loanService.getAllLoans().isEmpty());
    }

    @Test
    void onlyBorrowerCanCloseActiveLoan() {
        Loan loan = fixture.loanService.borrow("MED-1", "CYK-1");
        fixture.waitlistService.join("MED-2", "CYK-1");

        assertThrows(UnauthorizedReturnException.class,
                () -> fixture.loanService.returnEquipment(loan.getId(), "MED-3"));
        assertTrue(loan.isActive());
        assertFalse(fixture.inventoryService.getRequired("CYK-1").isAvailable());
        assertEquals(List.of("MED-2"), fixture.waitlistService.getQueue("CYK-1"));
        assertEquals(1, fixture.loanService.getAllLoans().size());
    }

    @Test
    void returnOnDueDateIsOnTimeAndNextDayIsLate() {
        Loan onTimeLoan = fixture.loanService.borrow("MED-1", "CYK-1");
        fixture.clock.setDate(LocalDate.of(2026, 8, 26));
        ReturnResult onTime = fixture.loanService.returnEquipment(onTimeLoan.getId(), "MED-1");

        assertFalse(onTime.isReturnedLate());
        assertFalse(onTimeLoan.isActive());
        assertEquals(LocalDate.of(2026, 8, 26), onTimeLoan.getReturnedOn());
        assertEquals(0, fixture.loanService.getActiveLoanCountForMember("MED-1"));
        assertTrue(fixture.inventoryService.getRequired("CYK-1").isAvailable());
        assertThrows(LoanRuleException.class,
                () -> fixture.loanService.returnEquipment(onTimeLoan.getId(), "MED-1"));

        Loan lateLoan = fixture.loanService.borrow("MED-1", "CYK-2");
        fixture.clock.setDate(LocalDate.of(2026, 9, 3));
        ReturnResult late = fixture.loanService.returnEquipment(lateLoan.getId(), "MED-1");

        assertTrue(late.isReturnedLate());
    }

    @Test
    void waitlistIsFifoAndCreatesAutomaticLoanOnReturn() {
        Loan originalLoan = fixture.loanService.borrow("MED-1", "CYK-1");
        fixture.waitlistService.join("MED-2", "CYK-1");
        fixture.waitlistService.join("MED-3", "CYK-1");
        fixture.clock.setDate(LocalDate.of(2026, 8, 22));

        ReturnResult result = fixture.loanService.returnEquipment(originalLoan.getId(), "MED-1");

        Loan automaticLoan = result.getAutomaticLoan().orElseThrow();
        assertEquals("MED-2", automaticLoan.getBorrower().getId());
        assertEquals(LocalDate.of(2026, 8, 22), automaticLoan.getPeriod().getLoanDate());
        assertEquals(LocalDate.of(2026, 8, 29), automaticLoan.getPeriod().getDueDate());
        assertEquals(List.of("MED-3"), fixture.waitlistService.getQueue("CYK-1"));
        assertFalse(fixture.inventoryService.getRequired("CYK-1").isAvailable());
    }

    @Test
    void ineligibleFirstMemberIsRemovedAndNextMemberGetsLoan() {
        Loan originalLoan = fixture.loanService.borrow("MED-1", "CYK-1");
        fixture.waitlistService.join("MED-2", "CYK-1");
        fixture.waitlistService.join("MED-3", "CYK-1");
        fixture.memberService.changeStatus("MED-2", MemberStatus.INACTIVE);

        ReturnResult result = fixture.loanService.returnEquipment(originalLoan.getId(), "MED-1");

        assertEquals("MED-3", result.getAutomaticLoan().orElseThrow().getBorrower().getId());
        assertEquals(List.of("MED-2"), result.getSkippedMemberIds());
        assertTrue(fixture.waitlistService.getQueue("CYK-1").isEmpty());
    }

    @Test
    void waitingMemberWithThreeActiveLoansIsSkipped() {
        Loan originalLoan = fixture.loanService.borrow("MED-1", "CYK-1");
        fixture.waitlistService.join("MED-2", "CYK-1");
        fixture.waitlistService.join("MED-3", "CYK-1");
        fixture.loanService.borrow("MED-2", "CYK-2");
        fixture.loanService.borrow("MED-2", "CYK-3");
        fixture.loanService.borrow("MED-2", "CYK-4");

        ReturnResult result = fixture.loanService.returnEquipment(originalLoan.getId(), "MED-1");

        assertEquals("MED-3", result.getAutomaticLoan().orElseThrow().getBorrower().getId());
        assertEquals(List.of("MED-2"), result.getSkippedMemberIds());
    }

    @Test
    void waitingMemberWithTwoLoansReceivesAutomaticThirdLoan() {
        Loan originalLoan = fixture.loanService.borrow("MED-1", "CYK-1");
        fixture.waitlistService.join("MED-2", "CYK-1");
        fixture.loanService.borrow("MED-2", "CYK-2");
        fixture.loanService.borrow("MED-2", "CYK-3");

        ReturnResult result = fixture.loanService.returnEquipment(originalLoan.getId(), "MED-1");

        assertEquals("MED-2", result.getAutomaticLoan().orElseThrow().getBorrower().getId());
        assertEquals(3, fixture.loanService.getActiveLoanCountForMember("MED-2"));
    }

    @Test
    void closedHistoricalLoansDoNotCountTowardActiveLoanLimit() {
        for (int index = 0; index < 3; index++) {
            Loan historicalLoan = fixture.loanService.borrow("MED-1", "CYK-1");
            fixture.loanService.returnEquipment(historicalLoan.getId(), "MED-1");
        }

        fixture.loanService.borrow("MED-1", "CYK-1");
        fixture.loanService.borrow("MED-1", "CYK-2");
        fixture.loanService.borrow("MED-1", "CYK-3");

        assertEquals(3, fixture.loanService.getActiveLoanCountForMember("MED-1"));
        assertEquals(6, fixture.loanService.getAllLoans().size());
    }

    @Test
    void equipmentStaysAvailableWhenNobodyInQueueIsEligible() {
        Loan originalLoan = fixture.loanService.borrow("MED-1", "CYK-1");
        fixture.waitlistService.join("MED-2", "CYK-1");
        fixture.memberService.changeStatus("MED-2", MemberStatus.SUSPENDED);

        ReturnResult result = fixture.loanService.returnEquipment(originalLoan.getId(), "MED-1");

        assertTrue(result.getAutomaticLoan().isEmpty());
        assertEquals(List.of("MED-2"), result.getSkippedMemberIds());
        assertTrue(fixture.inventoryService.getRequired("CYK-1").isAvailable());
    }
}
