package se.chasacademy.fritidslagret.service;

import se.chasacademy.fritidslagret.domain.loan.Loan;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class ReturnResult {
    private final Loan returnedLoan;
    private final boolean returnedLate;
    private final Loan automaticLoan;
    private final List<String> skippedMemberIds;

    public ReturnResult(
            Loan returnedLoan,
            boolean returnedLate,
            Loan automaticLoan,
            List<String> skippedMemberIds) {
        this.returnedLoan = Objects.requireNonNull(returnedLoan);
        this.returnedLate = returnedLate;
        this.automaticLoan = automaticLoan;
        this.skippedMemberIds = List.copyOf(skippedMemberIds);
    }

    public Loan getReturnedLoan() {
        return returnedLoan;
    }

    public boolean isReturnedLate() {
        return returnedLate;
    }

    public Optional<Loan> getAutomaticLoan() {
        return Optional.ofNullable(automaticLoan);
    }

    public List<String> getSkippedMemberIds() {
        return skippedMemberIds;
    }
}
