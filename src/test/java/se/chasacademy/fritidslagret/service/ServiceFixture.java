package se.chasacademy.fritidslagret.service;

import se.chasacademy.fritidslagret.domain.equipment.Bicycle;
import se.chasacademy.fritidslagret.domain.equipment.Equipment;
import se.chasacademy.fritidslagret.domain.member.Member;
import se.chasacademy.fritidslagret.repository.EquipmentRepository;
import se.chasacademy.fritidslagret.repository.LoanRepository;
import se.chasacademy.fritidslagret.repository.MemberRepository;
import se.chasacademy.fritidslagret.repository.WaitingListRepository;
import se.chasacademy.fritidslagret.repository.memory.InMemoryEquipmentRepository;
import se.chasacademy.fritidslagret.repository.memory.InMemoryLoanRepository;
import se.chasacademy.fritidslagret.repository.memory.InMemoryMemberRepository;
import se.chasacademy.fritidslagret.repository.memory.InMemoryWaitingListRepository;
import se.chasacademy.fritidslagret.util.SequentialLoanIdGenerator;

import java.time.LocalDate;

final class ServiceFixture {
    final EquipmentRepository equipmentRepository = new InMemoryEquipmentRepository();
    final MemberRepository memberRepository = new InMemoryMemberRepository();
    final LoanRepository loanRepository = new InMemoryLoanRepository();
    final WaitingListRepository waitingListRepository = new InMemoryWaitingListRepository();
    final MutableClock clock = new MutableClock(LocalDate.of(2026, 8, 19));
    final InventoryService inventoryService = new InventoryService(equipmentRepository);
    final MemberService memberService = new MemberService(memberRepository);
    final WaitlistService waitlistService = new WaitlistService(
            waitingListRepository, memberRepository, equipmentRepository, loanRepository);
    final LoanService loanService = new LoanService(
            memberRepository,
            equipmentRepository,
            loanRepository,
            waitlistService,
            new SequentialLoanIdGenerator(),
            clock);

    Member addMember(String id) {
        return memberService.register(new Member(id, "Medlem " + id));
    }

    Equipment addBicycle(String id) {
        return inventoryService.register(new Bicycle(id, "Cykel " + id));
    }
}
