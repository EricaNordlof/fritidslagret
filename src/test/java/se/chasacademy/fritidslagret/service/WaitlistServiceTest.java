package se.chasacademy.fritidslagret.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.chasacademy.fritidslagret.exception.AlreadyQueuedException;
import se.chasacademy.fritidslagret.exception.LoanRuleException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WaitlistServiceTest {
    private ServiceFixture fixture;

    @BeforeEach
    void setUp() {
        fixture = new ServiceFixture();
        fixture.addMember("MED-1");
        fixture.addMember("MED-2");
        fixture.addBicycle("CYK-1");
    }

    @Test
    void memberCannotQueueForAvailableEquipment() {
        assertThrows(LoanRuleException.class,
                () -> fixture.waitlistService.join("MED-2", "CYK-1"));
    }

    @Test
    void duplicateQueuePositionIsRejectedAndOriginalPositionRemains() {
        fixture.loanService.borrow("MED-1", "CYK-1");
        fixture.waitlistService.join("MED-2", "CYK-1");

        assertThrows(AlreadyQueuedException.class,
                () -> fixture.waitlistService.join("MED-2", "CYK-1"));
        assertEquals(List.of("MED-2"), fixture.waitlistService.getQueue("CYK-1"));
    }
}
