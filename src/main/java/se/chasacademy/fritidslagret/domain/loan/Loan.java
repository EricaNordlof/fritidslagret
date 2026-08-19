package se.chasacademy.fritidslagret.domain.loan;

import se.chasacademy.fritidslagret.domain.equipment.Equipment;
import se.chasacademy.fritidslagret.domain.member.Member;
import se.chasacademy.fritidslagret.exception.LoanRuleException;

import java.time.LocalDate;
import java.util.Objects;

public final class Loan {
    private final String id;
    private final Member borrower;
    private final Equipment equipment;
    private final LoanPeriod period;
    private LoanStatus status;
    private LocalDate returnedOn;

    public Loan(String id, Member borrower, Equipment equipment, LocalDate loanDate) {
        this.id = requireText(id, "Låne-ID");
        this.borrower = Objects.requireNonNull(borrower, "Låntagare får inte vara null.");
        this.equipment = Objects.requireNonNull(equipment, "Utrustning får inte vara null.");
        LocalDate dueDate = equipment.calculateDueDate(loanDate);
        this.period = new LoanPeriod(loanDate, dueDate);
        this.status = LoanStatus.ACTIVE;
    }

    public String getId() {
        return id;
    }

    public Member getBorrower() {
        return borrower;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public LoanPeriod getPeriod() {
        return period;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public LocalDate getReturnedOn() {
        return returnedOn;
    }

    public boolean isActive() {
        return status == LoanStatus.ACTIVE;
    }

    public boolean isBorrowedBy(String memberId) {
        return borrower.getId().equals(memberId);
    }

    public void close(LocalDate returnDate) {
        Objects.requireNonNull(returnDate, "Återlämningsdatum får inte vara null.");
        if (!isActive()) {
            throw new LoanRuleException("Lånet " + id + " är redan avslutat.");
        }
        if (returnDate.isBefore(period.getLoanDate())) {
            throw new LoanRuleException("Återlämningsdatum får inte ligga före lånedatum.");
        }
        returnedOn = returnDate;
        status = LoanStatus.CLOSED;
    }

    public boolean isOverdue(LocalDate referenceDate) {
        Objects.requireNonNull(referenceDate, "Referensdatum får inte vara null.");
        LocalDate comparisonDate = isActive() ? referenceDate : returnedOn;
        return comparisonDate.isAfter(period.getDueDate());
    }

    @Override
    public String toString() {
        String returned = returnedOn == null ? "-" : returnedOn.toString();
        return "%s | %s | %s | %s–%s | %s | åter: %s".formatted(
                id,
                borrower.getId(),
                equipment.getId(),
                period.getLoanDate(),
                period.getDueDate(),
                status.getDisplayName(),
                returned);
    }

    private static String requireText(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " får inte vara null.");
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " får inte vara tomt.");
        }
        return trimmed;
    }
}
