package se.chasacademy.fritidslagret.domain.loan;

import java.time.LocalDate;
import java.util.Objects;

/** Värdeobjekt som ägs av ett lån och visar komposition. */
public final class LoanPeriod {
    private final LocalDate loanDate;
    private final LocalDate dueDate;

    public LoanPeriod(LocalDate loanDate, LocalDate dueDate) {
        this.loanDate = Objects.requireNonNull(loanDate, "Lånedatum får inte vara null.");
        this.dueDate = Objects.requireNonNull(dueDate, "Förfallodatum får inte vara null.");
        if (dueDate.isBefore(loanDate)) {
            throw new IllegalArgumentException("Förfallodatum får inte ligga före lånedatum.");
        }
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }
}
