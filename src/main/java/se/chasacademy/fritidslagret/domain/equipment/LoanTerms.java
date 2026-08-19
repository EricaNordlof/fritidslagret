package se.chasacademy.fritidslagret.domain.equipment;

import java.time.LocalDate;
import java.util.Objects;

/** Regler som avgör hur länge en utrustning får lånas. */
public interface LoanTerms {
    int getMaxLoanDays();

    default LocalDate calculateDueDate(LocalDate loanDate) {
        Objects.requireNonNull(loanDate, "Lånedatum får inte vara null.");
        if (getMaxLoanDays() < 1) {
            throw new IllegalStateException("Maximal lånetid måste vara minst en dag.");
        }
        return loanDate.plusDays(getMaxLoanDays());
    }
}
