package se.chasacademy.fritidslagret.app;

import se.chasacademy.fritidslagret.report.ConsoleReportWriter;
import se.chasacademy.fritidslagret.report.OperationalReportService;
import se.chasacademy.fritidslagret.repository.EquipmentRepository;
import se.chasacademy.fritidslagret.repository.LoanRepository;
import se.chasacademy.fritidslagret.repository.MemberRepository;
import se.chasacademy.fritidslagret.repository.WaitingListRepository;
import se.chasacademy.fritidslagret.repository.memory.InMemoryEquipmentRepository;
import se.chasacademy.fritidslagret.repository.memory.InMemoryLoanRepository;
import se.chasacademy.fritidslagret.repository.memory.InMemoryMemberRepository;
import se.chasacademy.fritidslagret.repository.memory.InMemoryWaitingListRepository;
import se.chasacademy.fritidslagret.service.InventoryService;
import se.chasacademy.fritidslagret.service.LoanService;
import se.chasacademy.fritidslagret.service.MemberService;
import se.chasacademy.fritidslagret.service.StatisticsService;
import se.chasacademy.fritidslagret.service.WaitlistService;
import se.chasacademy.fritidslagret.ui.ConsoleApplication;
import se.chasacademy.fritidslagret.ui.ScannerConsoleIO;
import se.chasacademy.fritidslagret.util.SequentialLoanIdGenerator;

import java.time.Clock;
import java.util.Arrays;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        EquipmentRepository equipmentRepository = new InMemoryEquipmentRepository();
        MemberRepository memberRepository = new InMemoryMemberRepository();
        LoanRepository loanRepository = new InMemoryLoanRepository();
        WaitingListRepository waitingListRepository = new InMemoryWaitingListRepository();
        Clock clock = Clock.systemDefaultZone();

        InventoryService inventoryService = new InventoryService(equipmentRepository);
        MemberService memberService = new MemberService(memberRepository);
        WaitlistService waitlistService = new WaitlistService(
                waitingListRepository, memberRepository, equipmentRepository, loanRepository);
        LoanService loanService = new LoanService(
                memberRepository,
                equipmentRepository,
                loanRepository,
                waitlistService,
                new SequentialLoanIdGenerator(),
                clock);
        StatisticsService statisticsService = new StatisticsService(
                equipmentRepository, memberRepository, loanRepository);
        OperationalReportService reportService = new OperationalReportService(
                equipmentRepository,
                memberRepository,
                loanRepository,
                waitingListRepository,
                clock,
                new ConsoleReportWriter(System.out));

        if (Arrays.asList(args).contains("--demo")) {
            DemoData.seed(inventoryService, memberService, loanService, waitlistService);
            System.out.println("[INFO] Demodata laddades.");
        }

        ConsoleApplication application = new ConsoleApplication(
                new ScannerConsoleIO(System.in, System.out),
                inventoryService,
                memberService,
                loanService,
                waitlistService,
                statisticsService,
                reportService);
        application.run();
    }
}
