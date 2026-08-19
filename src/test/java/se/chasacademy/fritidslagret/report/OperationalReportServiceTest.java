package se.chasacademy.fritidslagret.report;

import org.junit.jupiter.api.Test;
import se.chasacademy.fritidslagret.domain.equipment.SkiEquipment;
import se.chasacademy.fritidslagret.domain.member.Member;
import se.chasacademy.fritidslagret.repository.EquipmentRepository;
import se.chasacademy.fritidslagret.repository.LoanRepository;
import se.chasacademy.fritidslagret.repository.MemberRepository;
import se.chasacademy.fritidslagret.repository.WaitingListRepository;
import se.chasacademy.fritidslagret.repository.memory.InMemoryEquipmentRepository;
import se.chasacademy.fritidslagret.repository.memory.InMemoryLoanRepository;
import se.chasacademy.fritidslagret.repository.memory.InMemoryMemberRepository;
import se.chasacademy.fritidslagret.repository.memory.InMemoryWaitingListRepository;
import se.chasacademy.fritidslagret.service.LoanService;
import se.chasacademy.fritidslagret.service.WaitlistService;
import se.chasacademy.fritidslagret.util.SequentialLoanIdGenerator;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OperationalReportServiceTest {
    @Test
    void overdueActiveAndLateReturnAreWrittenThroughInjectableOutputPort() {
        EquipmentRepository equipmentRepository = new InMemoryEquipmentRepository();
        MemberRepository memberRepository = new InMemoryMemberRepository();
        LoanRepository loanRepository = new InMemoryLoanRepository();
        WaitingListRepository waitingListRepository = new InMemoryWaitingListRepository();
        memberRepository.save(new Member("MED-1", "Amina"));
        equipmentRepository.save(new SkiEquipment("SKI-1", "Skidor"));

        Clock loanClock = Clock.fixed(Instant.parse("2026-08-19T10:00:00Z"), ZoneOffset.UTC);
        WaitlistService waitlistService = new WaitlistService(
                waitingListRepository, memberRepository, equipmentRepository, loanRepository);
        LoanService loanService = new LoanService(
                memberRepository,
                equipmentRepository,
                loanRepository,
                waitlistService,
                new SequentialLoanIdGenerator(),
                loanClock);
        loanService.borrow("MED-1", "SKI-1");

        CapturingReportWriter writer = new CapturingReportWriter();
        Clock reportClock = Clock.fixed(Instant.parse("2026-08-24T10:00:00Z"), ZoneOffset.UTC);
        OperationalReportService reportService = new OperationalReportService(
                equipmentRepository,
                memberRepository,
                loanRepository,
                waitingListRepository,
                reportClock,
                writer);

        reportService.writeReport();

        assertEquals(1, writer.callCount);
        assertTrue(writer.report.contains("FÖRSENADE AKTIVA LÅN"));
        assertTrue(writer.report.contains("LAN-0001"));
        assertTrue(writer.report.contains("2 dagar sent"));

        LoanService returnService = new LoanService(
                memberRepository,
                equipmentRepository,
                loanRepository,
                waitlistService,
                new SequentialLoanIdGenerator(),
                reportClock);
        returnService.returnEquipment("LAN-0001", "MED-1");
        reportService.writeReport();

        assertEquals(2, writer.callCount);
        assertTrue(writer.report.contains("SENA ÅTERLÄMNINGAR I HISTORIKEN"));
        assertTrue(writer.report.contains("återlämnad 2026-08-24"));
        assertTrue(writer.report.contains("2 dagar sent"));
    }

    private static final class CapturingReportWriter implements ReportWriter {
        private String report;
        private int callCount;

        @Override
        public void write(String report) {
            this.report = report;
            this.callCount++;
        }
    }
}
