package com.tractorapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "work_entries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @NotNull
    @Column(nullable = false)
    private LocalDate workDate;

    @Column
    private String fieldDescription;  // e.g., "North field - ploughing"

    @Column
    private String workType;          // e.g., PLOUGHING, TILLING, HARVESTING, LEVELLING

    @Positive
    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal hoursWorked;

    @Positive
    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal ratePerHour;   // INR per hour

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;   // hoursWorked * ratePerHour

    @Column
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    @PreUpdate
    public void calculateTotal() {
        if (hoursWorked != null && ratePerHour != null) {
            this.totalAmount = hoursWorked.multiply(ratePerHour);
        }
    }

    public enum PaymentStatus {
        PENDING, PAID, PARTIAL
    }
}
