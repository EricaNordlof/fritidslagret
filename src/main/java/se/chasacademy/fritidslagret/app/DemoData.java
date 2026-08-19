package se.chasacademy.fritidslagret.app;

import se.chasacademy.fritidslagret.domain.equipment.Bicycle;
import se.chasacademy.fritidslagret.domain.equipment.SkiEquipment;
import se.chasacademy.fritidslagret.domain.equipment.Tent;
import se.chasacademy.fritidslagret.domain.member.Member;
import se.chasacademy.fritidslagret.domain.member.MemberStatus;
import se.chasacademy.fritidslagret.service.InventoryService;
import se.chasacademy.fritidslagret.service.LoanService;
import se.chasacademy.fritidslagret.service.MemberService;
import se.chasacademy.fritidslagret.service.WaitlistService;

public final class DemoData {
    private DemoData() {
    }

    public static void seed(
            InventoryService inventoryService,
            MemberService memberService,
            LoanService loanService,
            WaitlistService waitlistService) {
        inventoryService.register(new Bicycle("CYK-001", "Stadscykel"));
        inventoryService.register(new Bicycle("CYK-002", "Mountainbike"));
        inventoryService.register(new Tent("TALT-001", "Familjetält"));
        inventoryService.register(new Tent("TALT-002", "Tvåmannatält"));
        inventoryService.register(new SkiEquipment("SKI-001", "Längdskidpaket"));
        inventoryService.register(new SkiEquipment("SKI-002", "Alpint skidpaket"));

        memberService.register(new Member("MED-001", "Amina Hassan"));
        memberService.register(new Member("MED-002", "Leo Berg"));
        memberService.register(new Member("MED-003", "Sara Lind"));
        memberService.register(new Member("MED-004", "Noor Ali"));
        memberService.changeStatus("MED-003", MemberStatus.INACTIVE);

        var historicalLoan = loanService.borrow("MED-001", "CYK-001");
        loanService.returnEquipment(historicalLoan.getId(), "MED-001");
        loanService.borrow("MED-002", "TALT-001");
        loanService.borrow("MED-001", "SKI-001");
        waitlistService.join("MED-004", "TALT-001");
    }
}
