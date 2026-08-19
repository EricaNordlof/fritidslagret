package se.chasacademy.fritidslagret.repository.memory;

import se.chasacademy.fritidslagret.domain.loan.Loan;
import se.chasacademy.fritidslagret.repository.LoanRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class InMemoryLoanRepository implements LoanRepository {
    private final Map<String, Loan> loanById = new LinkedHashMap<>();

    @Override
    public void save(Loan loan) {
        loanById.put(loan.getId(), loan);
    }

    @Override
    public Optional<Loan> findById(String id) {
        return Optional.ofNullable(loanById.get(id));
    }

    @Override
    public List<Loan> findAll() {
        return List.copyOf(new ArrayList<>(loanById.values()));
    }

    @Override
    public List<Loan> findActive() {
        List<Loan> result = new ArrayList<>();
        for (Loan loan : loanById.values()) {
            if (loan.isActive()) {
                result.add(loan);
            }
        }
        return List.copyOf(result);
    }

    @Override
    public List<Loan> findActiveByMemberId(String memberId) {
        List<Loan> result = new ArrayList<>();
        for (Loan loan : loanById.values()) {
            if (loan.isActive() && loan.isBorrowedBy(memberId)) {
                result.add(loan);
            }
        }
        return List.copyOf(result);
    }

    @Override
    public Optional<Loan> findActiveByEquipmentId(String equipmentId) {
        for (Loan loan : loanById.values()) {
            if (loan.isActive() && loan.getEquipment().getId().equals(equipmentId)) {
                return Optional.of(loan);
            }
        }
        return Optional.empty();
    }
}
