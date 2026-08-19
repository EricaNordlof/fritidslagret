package se.chasacademy.fritidslagret.repository;

import se.chasacademy.fritidslagret.domain.loan.Loan;

import java.util.List;
import java.util.Optional;

public interface LoanRepository {
    void save(Loan loan);

    Optional<Loan> findById(String id);

    List<Loan> findAll();

    List<Loan> findActive();

    List<Loan> findActiveByMemberId(String memberId);

    Optional<Loan> findActiveByEquipmentId(String equipmentId);
}
