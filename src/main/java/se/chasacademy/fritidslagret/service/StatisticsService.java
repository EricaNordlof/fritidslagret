package se.chasacademy.fritidslagret.service;

import se.chasacademy.fritidslagret.domain.equipment.Equipment;
import se.chasacademy.fritidslagret.domain.loan.Loan;
import se.chasacademy.fritidslagret.repository.EquipmentRepository;
import se.chasacademy.fritidslagret.repository.LoanRepository;
import se.chasacademy.fritidslagret.repository.MemberRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class StatisticsService {
    private final EquipmentRepository equipmentRepository;
    private final MemberRepository memberRepository;
    private final LoanRepository loanRepository;

    public StatisticsService(
            EquipmentRepository equipmentRepository,
            MemberRepository memberRepository,
            LoanRepository loanRepository) {
        this.equipmentRepository = Objects.requireNonNull(equipmentRepository);
        this.memberRepository = Objects.requireNonNull(memberRepository);
        this.loanRepository = Objects.requireNonNull(loanRepository);
    }

    public StatisticsSnapshot getSnapshot() {
        long available = 0;
        for (Equipment equipment : equipmentRepository.findAll()) {
            if (equipment.isAvailable()) {
                available++;
            }
        }

        return new StatisticsSnapshot(
                loanRepository.findActive().size(),
                memberRepository.findAll().size(),
                available,
                findMostBorrowedEquipment());
    }

    private List<EquipmentLoanCount> findMostBorrowedEquipment() {
        Map<String, Long> counts = new LinkedHashMap<>();
        for (Loan loan : loanRepository.findAll()) {
            String equipmentId = loan.getEquipment().getId();
            counts.merge(equipmentId, 1L, Long::sum);
        }

        long highestCount = 0;
        for (long count : counts.values()) {
            highestCount = Math.max(highestCount, count);
        }

        List<EquipmentLoanCount> result = new ArrayList<>();
        if (highestCount == 0) {
            return result;
        }
        for (Map.Entry<String, Long> entry : counts.entrySet()) {
            if (entry.getValue() == highestCount) {
                equipmentRepository.findById(entry.getKey()).ifPresent(equipment ->
                        result.add(new EquipmentLoanCount(equipment, entry.getValue())));
            }
        }
        return result;
    }
}
