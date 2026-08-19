package se.chasacademy.fritidslagret.domain;

import org.junit.jupiter.api.Test;
import se.chasacademy.fritidslagret.domain.equipment.Bicycle;
import se.chasacademy.fritidslagret.domain.equipment.Equipment;
import se.chasacademy.fritidslagret.domain.equipment.SkiEquipment;
import se.chasacademy.fritidslagret.domain.equipment.Tent;
import se.chasacademy.fritidslagret.exception.LoanRuleException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EquipmentTest {
    @Test
    void equipmentTypesCalculateDifferentDueDatesPolymorphically() {
        LocalDate loanDate = LocalDate.of(2026, 8, 19);
        List<Equipment> equipment = List.of(
                new Bicycle("CYK-1", "Cykel"),
                new Tent("TALT-1", "Tält"),
                new SkiEquipment("SKI-1", "Skidor"));

        assertEquals(LocalDate.of(2026, 8, 26), equipment.get(0).calculateDueDate(loanDate));
        assertEquals(LocalDate.of(2026, 8, 24), equipment.get(1).calculateDueDate(loanDate));
        assertEquals(LocalDate.of(2026, 8, 22), equipment.get(2).calculateDueDate(loanDate));
    }

    @Test
    void checkoutEncapsulatesAvailabilityAndRejectsSecondCheckout() {
        Equipment bicycle = new Bicycle("CYK-1", "Cykel");

        bicycle.checkOut();

        assertFalse(bicycle.isAvailable());
        assertThrows(LoanRuleException.class, bicycle::checkOut);
        bicycle.checkIn();
        assertTrue(bicycle.isAvailable());
    }
}
