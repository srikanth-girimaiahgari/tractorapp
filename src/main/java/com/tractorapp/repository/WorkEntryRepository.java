package com.tractorapp.repository;

import com.tractorapp.model.WorkEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface WorkEntryRepository extends JpaRepository<WorkEntry, Long> {

    List<WorkEntry> findByClientIdOrderByWorkDateDesc(Long clientId);

    List<WorkEntry> findByWorkDateBetweenOrderByWorkDateDesc(LocalDate start, LocalDate end);

    List<WorkEntry> findByClientIdAndWorkDateBetweenOrderByWorkDateDesc(
            Long clientId, LocalDate start, LocalDate end);

    // Monthly total for a client
    @Query("""
        SELECT COALESCE(SUM(w.totalAmount), 0)
        FROM WorkEntry w
        WHERE w.client.id = :clientId
          AND YEAR(w.workDate) = :year
          AND MONTH(w.workDate) = :month
    """)
    BigDecimal sumAmountByClientAndMonth(
            @Param("clientId") Long clientId,
            @Param("year") int year,
            @Param("month") int month);

    // Monthly total for all clients (for dashboard)
    @Query("""
        SELECT COALESCE(SUM(w.totalAmount), 0)
        FROM WorkEntry w
        WHERE YEAR(w.workDate) = :year
          AND MONTH(w.workDate) = :month
    """)
    BigDecimal sumAllAmountsByMonth(@Param("year") int year, @Param("month") int month);

    // Total hours for a client in a month
    @Query("""
        SELECT COALESCE(SUM(w.hoursWorked), 0)
        FROM WorkEntry w
        WHERE w.client.id = :clientId
          AND YEAR(w.workDate) = :year
          AND MONTH(w.workDate) = :month
    """)
    BigDecimal sumHoursByClientAndMonth(
            @Param("clientId") Long clientId,
            @Param("year") int year,
            @Param("month") int month);

    // All entries for today
    List<WorkEntry> findByWorkDateOrderByCreatedAtDesc(LocalDate date);

    // Pending payments for a client
    List<WorkEntry> findByClientIdAndPaymentStatusOrderByWorkDateDesc(
            Long clientId, WorkEntry.PaymentStatus status);

    long countByClientId(Long clientId);
}
