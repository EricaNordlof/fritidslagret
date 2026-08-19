package se.chasacademy.fritidslagret.ui;

import se.chasacademy.fritidslagret.domain.equipment.Bicycle;
import se.chasacademy.fritidslagret.domain.equipment.Equipment;
import se.chasacademy.fritidslagret.domain.equipment.SkiEquipment;
import se.chasacademy.fritidslagret.domain.equipment.Tent;
import se.chasacademy.fritidslagret.domain.loan.Loan;
import se.chasacademy.fritidslagret.domain.member.Member;
import se.chasacademy.fritidslagret.domain.member.MemberStatus;
import se.chasacademy.fritidslagret.domain.waitlist.WaitingList;
import se.chasacademy.fritidslagret.exception.EquipmentUnavailableException;
import se.chasacademy.fritidslagret.exception.FritidslagretException;
import se.chasacademy.fritidslagret.report.OperationalReportService;
import se.chasacademy.fritidslagret.service.EquipmentLoanCount;
import se.chasacademy.fritidslagret.service.InventoryService;
import se.chasacademy.fritidslagret.service.LoanService;
import se.chasacademy.fritidslagret.service.MemberService;
import se.chasacademy.fritidslagret.service.ReturnResult;
import se.chasacademy.fritidslagret.service.StatisticsService;
import se.chasacademy.fritidslagret.service.StatisticsSnapshot;
import se.chasacademy.fritidslagret.service.WaitlistService;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public final class ConsoleApplication {
    private final ConsoleIO io;
    private final InventoryService inventoryService;
    private final MemberService memberService;
    private final LoanService loanService;
    private final WaitlistService waitlistService;
    private final StatisticsService statisticsService;
    private final OperationalReportService reportService;

    public ConsoleApplication(
            ConsoleIO io,
            InventoryService inventoryService,
            MemberService memberService,
            LoanService loanService,
            WaitlistService waitlistService,
            StatisticsService statisticsService,
            OperationalReportService reportService) {
        this.io = Objects.requireNonNull(io);
        this.inventoryService = Objects.requireNonNull(inventoryService);
        this.memberService = Objects.requireNonNull(memberService);
        this.loanService = Objects.requireNonNull(loanService);
        this.waitlistService = Objects.requireNonNull(waitlistService);
        this.statisticsService = Objects.requireNonNull(statisticsService);
        this.reportService = Objects.requireNonNull(reportService);
    }

    public void run() {
        io.println("Välkommen till Fritidslagret!");
        try {
            boolean running = true;
            while (running) {
                printMainMenu();
                int choice = io.readChoice("Välj [0–6]: ", 0, 6);
                switch (choice) {
                    case 1 -> inventoryMenu();
                    case 2 -> memberMenu();
                    case 3 -> loanMenu();
                    case 4 -> waitlistMenu();
                    case 5 -> executeSafely(this::showStatistics);
                    case 6 -> executeSafely(reportService::writeReport);
                    case 0 -> running = false;
                    default -> throw new IllegalStateException("Oväntat menyval.");
                }
            }
        } catch (InputClosedException exception) {
            io.println("\n[INFO] Konsolinmatningen avslutades.");
        }
        io.println("Tack för idag!");
    }

    private void inventoryMenu() {
        boolean running = true;
        while (running) {
            io.println("""

                    INVENTARIE
                    1. Visa all utrustning
                    2. Registrera cykel
                    3. Registrera tält
                    4. Registrera skidutrustning
                    5. Sök utrustning via ID
                    0. Tillbaka""");
            int choice = io.readChoice("Välj [0–5]: ", 0, 5);
            switch (choice) {
                case 1 -> executeSafely(this::showAllEquipment);
                case 2 -> executeSafely(() -> registerEquipment("CYKEL"));
                case 3 -> executeSafely(() -> registerEquipment("TALT"));
                case 4 -> executeSafely(() -> registerEquipment("SKIDOR"));
                case 5 -> executeSafely(this::findEquipment);
                case 0 -> running = false;
                default -> throw new IllegalStateException("Oväntat menyval.");
            }
        }
    }

    private void memberMenu() {
        boolean running = true;
        while (running) {
            io.println("""

                    MEDLEMMAR
                    1. Visa alla medlemmar
                    2. Registrera medlem
                    3. Sök medlem via ID
                    4. Ändra medlemsstatus
                    0. Tillbaka""");
            int choice = io.readChoice("Välj [0–4]: ", 0, 4);
            switch (choice) {
                case 1 -> executeSafely(this::showAllMembers);
                case 2 -> executeSafely(this::registerMember);
                case 3 -> executeSafely(this::findMember);
                case 4 -> executeSafely(this::changeMemberStatus);
                case 0 -> running = false;
                default -> throw new IllegalStateException("Oväntat menyval.");
            }
        }
    }

    private void loanMenu() {
        boolean running = true;
        while (running) {
            io.println("""

                    UTLÅNING
                    1. Registrera nytt lån
                    2. Återlämna utrustning
                    3. Visa aktiva lån
                    4. Visa lånehistorik
                    0. Tillbaka""");
            int choice = io.readChoice("Välj [0–4]: ", 0, 4);
            switch (choice) {
                case 1 -> executeSafely(this::borrowEquipment);
                case 2 -> executeSafely(this::returnEquipment);
                case 3 -> executeSafely(() -> showLoans(loanService.getActiveLoans()));
                case 4 -> executeSafely(() -> showLoans(loanService.getAllLoans()));
                case 0 -> running = false;
                default -> throw new IllegalStateException("Oväntat menyval.");
            }
        }
    }

    private void waitlistMenu() {
        boolean running = true;
        while (running) {
            io.println("""

                    VÄNTELISTOR
                    1. Ställ medlem i kö
                    2. Visa kö för utrustning
                    3. Visa alla väntelistor
                    4. Lämna väntelista
                    0. Tillbaka""");
            int choice = io.readChoice("Välj [0–4]: ", 0, 4);
            switch (choice) {
                case 1 -> executeSafely(this::joinWaitlist);
                case 2 -> executeSafely(this::showWaitlist);
                case 3 -> executeSafely(this::showAllWaitlists);
                case 4 -> executeSafely(this::leaveWaitlist);
                case 0 -> running = false;
                default -> throw new IllegalStateException("Oväntat menyval.");
            }
        }
    }

    private void registerEquipment(String type) {
        String id = readId("Unikt utrustnings-ID: ");
        String name = io.readNonBlank("Namn: ");
        Equipment equipment = switch (type) {
            case "CYKEL" -> new Bicycle(id, name);
            case "TALT" -> new Tent(id, name);
            case "SKIDOR" -> new SkiEquipment(id, name);
            default -> throw new IllegalArgumentException("Okänd utrustningstyp.");
        };
        inventoryService.register(equipment);
        io.println("[OK] Utrustningen registrerades: " + equipment);
    }

    private void showAllEquipment() {
        List<Equipment> equipment = inventoryService.getAll();
        if (equipment.isEmpty()) {
            io.println("[INFO] Inventariet är tomt.");
            return;
        }
        io.println("ID | typ | namn | max lånetid (dagar) | status");
        equipment.forEach(item -> io.println(item.toString()));
    }

    private void findEquipment() {
        io.println(inventoryService.getRequired(readId("Utrustnings-ID: ")).toString());
    }

    private void registerMember() {
        Member member = new Member(readId("Unikt medlems-ID: "), io.readNonBlank("Namn: "));
        memberService.register(member);
        io.println("[OK] Medlemmen registrerades: " + member);
    }

    private void showAllMembers() {
        List<Member> members = memberService.getAll();
        if (members.isEmpty()) {
            io.println("[INFO] Medlemsregistret är tomt.");
            return;
        }
        io.println("ID | namn | status");
        members.forEach(member -> io.println(member.toString()));
    }

    private void findMember() {
        io.println(memberService.getRequired(readId("Medlems-ID: ")).toString());
    }

    private void changeMemberStatus() {
        String memberId = readId("Medlems-ID: ");
        io.println("1. Aktiv\n2. Inaktiv\n3. Avstängd");
        int statusChoice = io.readChoice("Välj status [1–3]: ", 1, 3);
        MemberStatus status = switch (statusChoice) {
            case 1 -> MemberStatus.ACTIVE;
            case 2 -> MemberStatus.INACTIVE;
            case 3 -> MemberStatus.SUSPENDED;
            default -> throw new IllegalStateException("Oväntat statusval.");
        };
        Member member = memberService.changeStatus(memberId, status);
        io.println("[OK] Ny status: " + member);
    }

    private void borrowEquipment() {
        String memberId = readId("Medlems-ID: ");
        String equipmentId = readId("Utrustnings-ID: ");
        try {
            Loan loan = loanService.borrow(memberId, equipmentId);
            printCreatedLoan(loan, "[OK] Lånet skapades.");
        } catch (EquipmentUnavailableException exception) {
            io.println("[INFO] " + exception.getMessage());
            if (io.readYesNo("Ställa medlemmen i kö? [j/n]: ")) {
                int position = waitlistService.join(memberId, equipmentId);
                io.println("[OK] Medlemmen står nu på plats " + position + " i kön.");
            }
        }
    }

    private void returnEquipment() {
        String loanId = readId("Låne-ID: ");
        String memberId = readId("Låntagarens medlems-ID: ");
        ReturnResult result = loanService.returnEquipment(loanId, memberId);
        io.println("[OK] Lånet " + result.getReturnedLoan().getId() + " avslutades.");
        if (result.isReturnedLate()) {
            io.println("[VARNING] Utrustningen återlämnades efter förfallodatum.");
        }
        if (!result.getSkippedMemberIds().isEmpty()) {
            io.println("[INFO] Ej behöriga kömedlemmar togs bort: "
                    + String.join(", ", result.getSkippedMemberIds()));
        }
        result.getAutomaticLoan().ifPresent(loan ->
                printCreatedLoan(loan, "[INFO] Automatiskt lån skapades åt nästa behöriga i kön."));
    }

    private void showLoans(List<Loan> loans) {
        if (loans.isEmpty()) {
            io.println("[INFO] Inga lån att visa.");
            return;
        }
        io.println("Låne-ID | medlems-ID | utrustnings-ID | period | status | återlämnad");
        loans.forEach(loan -> io.println(loan.toString()));
    }

    private void joinWaitlist() {
        int position = waitlistService.join(
                readId("Medlems-ID: "), readId("Utrustnings-ID: "));
        io.println("[OK] Köplats registrerad. Position: " + position + ".");
    }

    private void showWaitlist() {
        String equipmentId = readId("Utrustnings-ID: ");
        printQueue(equipmentId, waitlistService.getQueue(equipmentId));
    }

    private void showAllWaitlists() {
        List<WaitingList> waitingLists = waitlistService.getAll();
        if (waitingLists.isEmpty()) {
            io.println("[INFO] Alla väntelistor är tomma.");
            return;
        }
        waitingLists.forEach(list -> printQueue(list.getEquipmentId(), list.snapshot()));
    }

    private void leaveWaitlist() {
        String memberId = readId("Medlems-ID: ");
        String equipmentId = readId("Utrustnings-ID: ");
        if (waitlistService.leave(memberId, equipmentId)) {
            io.println("[OK] Medlemmen togs bort ur kön.");
        } else {
            io.println("[INFO] Medlemmen stod inte i den kön.");
        }
    }

    private void showStatistics() {
        StatisticsSnapshot snapshot = statisticsService.getSnapshot();
        io.println("""

                STATISTIK
                Aktiva lån: %d
                Medlemmar: %d
                Tillgängliga utrustningsobjekt: %d""".formatted(
                snapshot.getActiveLoans(),
                snapshot.getTotalMembers(),
                snapshot.getAvailableEquipment()));
        if (snapshot.getMostBorrowedEquipment().isEmpty()) {
            io.println("Mest utlånade utrustning: ingen lånehistorik ännu.");
        } else {
            io.println("Mest utlånade utrustning:");
            for (EquipmentLoanCount count : snapshot.getMostBorrowedEquipment()) {
                io.println("- %s (%s): %d lån".formatted(
                        count.getEquipment().getName(),
                        count.getEquipment().getId(),
                        count.getLoanCount()));
            }
        }
    }

    private void printQueue(String equipmentId, List<String> memberIds) {
        io.println("Väntelista för " + equipmentId + ":");
        if (memberIds.isEmpty()) {
            io.println("[INFO] Kön är tom.");
            return;
        }
        for (int index = 0; index < memberIds.size(); index++) {
            String memberId = memberIds.get(index);
            String name = memberService.findById(memberId).map(Member::getName).orElse("Okänd medlem");
            io.println((index + 1) + ". " + memberId + " – " + name);
        }
    }

    private void printCreatedLoan(Loan loan, String heading) {
        io.println(heading);
        io.println("     Låne-ID: " + loan.getId());
        io.println("     Medlem: " + loan.getBorrower().getId() + " – " + loan.getBorrower().getName());
        io.println("     Utrustning: " + loan.getEquipment().getId() + " – " + loan.getEquipment().getName());
        io.println("     Låneperiod: " + loan.getPeriod().getLoanDate() + " till " + loan.getPeriod().getDueDate());
    }

    private void printMainMenu() {
        io.println("""

                ================================
                         FRITIDSLAGRET
                ================================
                1. Inventarie
                2. Medlemmar
                3. Utlåning och återlämning
                4. Väntelistor
                5. Statistik
                6. Visa verksamhetsrapport
                0. Avsluta""");
    }

    private String readId(String prompt) {
        return io.readNonBlank(prompt).toUpperCase(Locale.ROOT);
    }

    private void executeSafely(Runnable operation) {
        try {
            operation.run();
        } catch (FritidslagretException | IllegalArgumentException exception) {
            io.println("[FEL] " + exception.getMessage());
        }
    }
}
