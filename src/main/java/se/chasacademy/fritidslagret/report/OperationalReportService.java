package se.chasacademy.fritidslagret.report;

import se.chasacademy.fritidslagret.domain.loan.Loan;
import se.chasacademy.fritidslagret.domain.waitlist.WaitingList;
import se.chasacademy.fritidslagret.repository.EquipmentRepository;
import se.chasacademy.fritidslagret.repository.LoanRepository;
import se.chasacademy.fritidslagret.repository.MemberRepository;
import se.chasacademy.fritidslagret.repository.WaitingListRepository;

import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public final class OperationalReportService {
    private final EquipmentRepository equipmentRepository;
    private final MemberRepository memberRepository;
    private final LoanRepository loanRepository;
    private final WaitingListRepository waitingListRepository;
    private final Clock clock;
    private final ReportWriter reportWriter;

    public OperationalReportService(
            EquipmentRepository equipmentRepository,
            MemberRepository memberRepository,
            LoanRepository loanRepository,
            WaitingListRepository waitingListRepository,
            Clock clock,
            ReportWriter reportWriter) {
        this.equipmentRepository = Objects.requireNonNull(equipmentRepository);
        this.memberRepository = Objects.requireNonNull(memberRepository);
        this.loanRepository = Objects.requireNonNull(loanRepository);
        this.waitingListRepository = Objects.requireNonNull(waitingListRepository);
        this.clock = Objects.requireNonNull(clock);
        this.reportWriter = Objects.requireNonNull(reportWriter);
    }

    public String generate() {
        LocalDate today = LocalDate.now(clock);
        long available = equipmentRepository.findAll().stream()
                .filter(equipment -> equipment.isAvailable())
                .count();
        long queued = waitingListRepository.findAll().stream()
                .mapToLong(WaitingList::size)
                .sum();

        StringBuilder report = new StringBuilder();
        report.append("========================================\n")
                .append(" FRITIDSLAGRET – VERKSAMHETSRAPPORT\n")
                .append(" Datum: ").append(today).append('\n')
                .append("========================================\n")
                .append("Medlemmar: ").append(memberRepository.findAll().size()).append('\n')
                .append("Utrustningsobjekt: ").append(equipmentRepository.findAll().size()).append('\n')
                .append("Tillgängliga objekt: ").append(available).append('\n')
                .append("Aktiva lån: ").append(loanRepository.findActive().size()).append('\n')
                .append("Köplatser i väntelistor: ").append(queued).append("\n\n")
                .append("FÖRSENADE AKTIVA LÅN\n");

        int overdueActive = 0;
        for (Loan loan : loanRepository.findActive()) {
            if (loan.isOverdue(today)) {
                overdueActive++;
                long daysLate = ChronoUnit.DAYS.between(loan.getPeriod().getDueDate(), today);
                report.append("- ").append(loan.getId())
                        .append(" | medlem ").append(loan.getBorrower().getId())
                        .append(" | ").append(loan.getEquipment().getId())
                        .append(" | ").append(daysLate).append(" dagar sent\n");
            }
        }
        if (overdueActive == 0) {
            report.append("Inga försenade aktiva lån.\n");
        }

        report.append("\nSENA ÅTERLÄMNINGAR I HISTORIKEN\n");
        int lateReturns = 0;
        for (Loan loan : loanRepository.findAll()) {
            if (!loan.isActive() && loan.isOverdue(today)) {
                lateReturns++;
                long daysLate = ChronoUnit.DAYS.between(
                        loan.getPeriod().getDueDate(), loan.getReturnedOn());
                report.append("- ").append(loan.getId())
                        .append(" | återlämnad ").append(loan.getReturnedOn())
                        .append(" | ").append(daysLate).append(" dagar sent\n");
            }
        }
        if (lateReturns == 0) {
            report.append("Inga sena återlämningar registrerade.\n");
        }

        return report.toString();
    }

    public void writeReport() {
        reportWriter.write(generate());
    }
}
