package se.chasacademy.fritidslagret.service;

import org.junit.jupiter.api.Test;
import se.chasacademy.fritidslagret.domain.equipment.Bicycle;
import se.chasacademy.fritidslagret.domain.member.Member;
import se.chasacademy.fritidslagret.exception.DuplicateIdException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InventoryAndMemberServiceTest {
    @Test
    void duplicateIdsAreRejectedWithoutReplacingExistingData() {
        ServiceFixture fixture = new ServiceFixture();
        fixture.inventoryService.register(new Bicycle("CYK-1", "Original"));
        fixture.memberService.register(new Member("MED-1", "Originalmedlem"));

        assertThrows(DuplicateIdException.class,
                () -> fixture.inventoryService.register(new Bicycle("CYK-1", "Ersättare")));
        assertThrows(DuplicateIdException.class,
                () -> fixture.memberService.register(new Member("MED-1", "Ersättare")));
        assertEquals("Original", fixture.inventoryService.getRequired("CYK-1").getName());
        assertEquals("Originalmedlem", fixture.memberService.getRequired("MED-1").getName());
    }
}
