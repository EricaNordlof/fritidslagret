package se.chasacademy.fritidslagret.service;

import se.chasacademy.fritidslagret.domain.equipment.Equipment;
import se.chasacademy.fritidslagret.domain.loan.Loan;
import se.chasacademy.fritidslagret.domain.member.Member;
import se.chasacademy.fritidslagret.exception.EntityNotFoundException;
import se.chasacademy.fritidslagret.exception.EquipmentUnavailableException;
import se.chasacademy.fritidslagret.exception.LoanLimitExceededException;
import se.chasacademy.fritidslagret.exception.LoanRuleException;
import se.chasacademy.fritidslagret.exception.MemberNotEligibleException;
import se.chasacademy.fritidslagret.exception.UnauthorizedReturnException;
import se.chasacademy.fritidslagret.repository.EquipmentRepository;
import se.chasacademy.fritidslagret.repository.LoanRepository;
import se.chasacademy.fritidslagret.repository.MemberRepository;
import se.chasacademy.fritidslagret.util.LoanIdGenerator;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class LoanService {
    public static final int MAX_ACTIVE_LOANS = 3;

    private final MemberRepository memberRepository;
    private final EquipmentRepository equipmentRepository;
    private final LoanRepository loanRepository;
    private final WaitlistService waitlistService;
    private final LoanIdGenerator idGenerator;
    private final Clock clock;

    public LoanService(
            MemberRepository memberRepository,
            EquipmentRepository equipmentRepository,
            LoanRepository loanRepository,
            WaitlistService waitlistService,
            LoanIdGenerator idGenerator,
            Clock clock) {
        this.memberRepository = Objects.requireNonNull(memberRepository);
        this.equipmentRepository = Objects.requireNonNull(equipmentRepository);
        this.loanRepository = Objects.requireNonNull(loanRepository);
        this.waitlistService = Objects.requireNonNull(waitlistService);
        this.idGenerator = Objects.requireNonNull(idGenerator);
        this.clock = Objects.requireNonNull(clock);
    }

    public Loan borrow(String memberId, String equipmentId) {
        Member member = requireMember(memberId);
        Equipment equipment = requireEquipment(equipmentId);
        validateMemberCanBorrow(member);

        if (!equipment.isAvailable() || loanRepository.findActiveByEquipmentId(equipmentId).isPresent()) {
            throw new EquipmentUnavailableException(
                    "Utrustningen " + equipmentId + " är redan utlånad. Medlemmen kan ställa sig i kö.");
        }

        return createLoan(member, equipment, LocalDate.now(clock));
    }

    public ReturnResult returnEquipment(String loanId, String memberId) {
        Loan loan = requireLoan(loanId);
        if (!loan.isActive()) {
            throw new LoanRuleException("Lånet " + loanId + " är redan avslutat.");
        }
        if (!loan.isBorrowedBy(memberId)) {
            throw new UnauthorizedReturnException("Endast registrerad låntagare kan avsluta lånet.");
        }
        if (loan.getEquipment().isAvailable()) {
            throw new IllegalStateException("Utrustningsstatus och aktivt lån är inte synkroniserade.");
        }

        LocalDate returnDate = LocalDate.now(clock);
        loan.close(returnDate);
        loan.getEquipment().checkIn();
        boolean returnedLate = loan.isOverdue(returnDate);

        List<String> skippedMemberIds = new ArrayList<>();
        Loan automaticLoan = createLoanForFirstEligibleWaitingMember(
                loan.getEquipment(), returnDate, skippedMemberIds);

        return new ReturnResult(loan, returnedLate, automaticLoan, skippedMemberIds);
    }

    public List<Loan> getActiveLoans() {
        return loanRepository.findActive();
    }

    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    public int getActiveLoanCountForMember(String memberId) {
        return loanRepository.findActiveByMemberId(memberId).size();
    }

    private Loan createLoanForFirstEligibleWaitingMember(
            Equipment equipment,
            LocalDate loanDate,
            List<String> skippedMemberIds) {
        Optional<String> nextMemberId;
        while ((nextMemberId = waitlistService.pollNext(equipment.getId())).isPresent()) {
            Optional<Member> member = memberRepository.findById(nextMemberId.get());
            if (member.isEmpty() || !canBorrow(member.get())) {
                skippedMemberIds.add(nextMemberId.get());
                continue;
            }
            return createLoan(member.get(), equipment, loanDate);
        }
        return null;
    }

    private Loan createLoan(Member member, Equipment equipment, LocalDate loanDate) {
        String loanId = Objects.requireNonNull(idGenerator.nextId(), "Låne-ID får inte vara null.");
        if (loanRepository.findById(loanId).isPresent()) {
            throw new IllegalStateException("Låne-ID " + loanId + " har redan genererats.");
        }
        Loan loan = new Loan(loanId, member, equipment, loanDate);
        equipment.checkOut();
        loanRepository.save(loan);
        return loan;
    }

    private boolean canBorrow(Member member) {
        return member.canBorrow()
                && loanRepository.findActiveByMemberId(member.getId()).size() < MAX_ACTIVE_LOANS;
    }

    private void validateMemberCanBorrow(Member member) {
        if (!member.canBorrow()) {
            throw new MemberNotEligibleException(
                    "Medlemmen " + member.getId() + " är inte aktiv och får inte låna.");
        }
        int activeLoans = loanRepository.findActiveByMemberId(member.getId()).size();
        if (activeLoans >= MAX_ACTIVE_LOANS) {
            throw new LoanLimitExceededException(
                    "Medlemmen har redan maximalt " + MAX_ACTIVE_LOANS + " aktiva lån.");
        }
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

    private Loan requireLoan(String loanId) {
        return loanRepository.findById(loanId)
                .orElseThrow(() -> new EntityNotFoundException("Inget lån hittades med ID " + loanId + "."));
    }
}
