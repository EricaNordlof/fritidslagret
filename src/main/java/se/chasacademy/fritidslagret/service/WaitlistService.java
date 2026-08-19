package se.chasacademy.fritidslagret.service;

import se.chasacademy.fritidslagret.domain.equipment.Equipment;
import se.chasacademy.fritidslagret.domain.loan.Loan;
import se.chasacademy.fritidslagret.domain.member.Member;
import se.chasacademy.fritidslagret.domain.waitlist.WaitingList;
import se.chasacademy.fritidslagret.exception.EntityNotFoundException;
import se.chasacademy.fritidslagret.exception.LoanRuleException;
import se.chasacademy.fritidslagret.exception.MemberNotEligibleException;
import se.chasacademy.fritidslagret.repository.EquipmentRepository;
import se.chasacademy.fritidslagret.repository.LoanRepository;
import se.chasacademy.fritidslagret.repository.MemberRepository;
import se.chasacademy.fritidslagret.repository.WaitingListRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class WaitlistService {
    private final WaitingListRepository waitingListRepository;
    private final MemberRepository memberRepository;
    private final EquipmentRepository equipmentRepository;
    private final LoanRepository loanRepository;

    public WaitlistService(
            WaitingListRepository waitingListRepository,
            MemberRepository memberRepository,
            EquipmentRepository equipmentRepository,
            LoanRepository loanRepository) {
        this.waitingListRepository = Objects.requireNonNull(waitingListRepository);
        this.memberRepository = Objects.requireNonNull(memberRepository);
        this.equipmentRepository = Objects.requireNonNull(equipmentRepository);
        this.loanRepository = Objects.requireNonNull(loanRepository);
    }

    public int join(String memberId, String equipmentId) {
        Member member = requireMember(memberId);
        Equipment equipment = requireEquipment(equipmentId);

        if (!member.canBorrow()) {
            throw new MemberNotEligibleException("Endast aktiva medlemmar kan ställa sig i kö.");
        }
        if (equipment.isAvailable()) {
            throw new LoanRuleException("Utrustningen är tillgänglig och ska lånas direkt i stället.");
        }

        Optional<Loan> activeLoan = loanRepository.findActiveByEquipmentId(equipmentId);
        if (activeLoan.isPresent() && activeLoan.get().isBorrowedBy(memberId)) {
            throw new LoanRuleException("Låntagaren kan inte köa till utrustningen som hen redan lånar.");
        }

        return waitingListRepository.getOrCreate(equipmentId).enqueue(memberId);
    }

    public boolean leave(String memberId, String equipmentId) {
        requireMember(memberId);
        requireEquipment(equipmentId);
        WaitingList waitingList = waitingListRepository.findByEquipmentId(equipmentId)
                .orElseThrow(() -> new LoanRuleException("Det finns ingen väntelista för utrustningen."));
        return waitingList.remove(memberId);
    }

    public List<String> getQueue(String equipmentId) {
        requireEquipment(equipmentId);
        return waitingListRepository.findByEquipmentId(equipmentId)
                .map(WaitingList::snapshot)
                .orElseGet(List::of);
    }

    public List<WaitingList> getAll() {
        return waitingListRepository.findAll().stream()
                .filter(waitingList -> !waitingList.isEmpty())
                .toList();
    }

    Optional<String> pollNext(String equipmentId) {
        return waitingListRepository.findByEquipmentId(equipmentId)
                .flatMap(WaitingList::poll);
    }

    private Member requireMember(String memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Ingen medlem hittades med ID " + memberId + "."));
    }

    private Equipment requireEquipment(String equipmentId) {
        return equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Ingen utrustning hittades med ID " + equipmentId + "."));
    }
}
