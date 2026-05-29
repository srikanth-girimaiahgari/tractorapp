package com.tractorapp;

import com.tractorapp.model.WorkEntry;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TractorFieldManagerApplicationTests {

    @Test
    void contextLoads() {
        // Verifies the Spring context starts correctly
    }

    @Test
    void workEntryCalculatesAmountCorrectly() {
        WorkEntry entry = new WorkEntry();
        entry.setHoursWorked(new BigDecimal("6.5"));
        entry.setRatePerHour(new BigDecimal("500"));
        entry.calculateTotal();

        assertEquals(new BigDecimal("3250.0"), entry.getTotalAmount());
    }

    @Test
    void workEntryDefaultStatusIsPending() {
        WorkEntry entry = new WorkEntry();
        assertEquals(WorkEntry.PaymentStatus.PENDING, entry.getPaymentStatus());
    }
}
