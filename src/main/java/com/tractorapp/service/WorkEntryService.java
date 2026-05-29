package com.tractorapp.service;

import com.tractorapp.model.Client;
import com.tractorapp.model.WorkEntry;
import com.tractorapp.repository.WorkEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkEntryService {

    private final WorkEntryRepository workEntryRepository;
    private final ClientService clientService;

    /**
     * Log new field work and immediately add amount to client's outstanding balance.
     */
    public WorkEntry logWork(WorkEntry entry) {
        // Ensure total is calculated
        entry.calculateTotal();
        WorkEntry saved = workEntryRepository.save(entry);

        // Add to client's outstanding balance
        clientService.addToClientBalance(entry.getClient().getId(), saved.getTotalAmount());

        return saved;
    }

    @Transactional(readOnly = true)
    public WorkEntry getById(Long id) {
        return workEntryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Work entry not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<WorkEntry> getEntriesByClient(Long clientId) {
        return workEntryRepository.findByClientIdOrderByWorkDateDesc(clientId);
    }

    @Transactional(readOnly = true)
    public List<WorkEntry> getTodaysWork() {
        return workEntryRepository.findByWorkDateOrderByCreatedAtDesc(LocalDate.now());
    }

    @Transactional(readOnly = true)
    public List<WorkEntry> getEntriesForDateRange(LocalDate from, LocalDate to) {
        return workEntryRepository.findByWorkDateBetweenOrderByWorkDateDesc(from, to);
    }

    @Transactional(readOnly = true)
    public List<WorkEntry> getClientEntriesForMonth(Long clientId, int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        return workEntryRepository.findByClientIdAndWorkDateBetweenOrderByWorkDateDesc(
                clientId,
                ym.atDay(1),
                ym.atEndOfMonth()
        );
    }

    /**
     * Monthly bill summary for a client.
     */
    @Transactional(readOnly = true)
    public MonthlyBillSummary getMonthlyBill(Long clientId, int year, int month) {
        Client client = clientService.getClientById(clientId);
        List<WorkEntry> entries = getClientEntriesForMonth(clientId, year, month);

        BigDecimal totalAmount = workEntryRepository.sumAmountByClientAndMonth(clientId, year, month);
        BigDecimal totalHours = workEntryRepository.sumHoursByClientAndMonth(clientId, year, month);

        return new MonthlyBillSummary(client, year, month, entries, totalHours, totalAmount);
    }

    /**
     * Mark a work entry as paid and reduce client balance.
     */
    public WorkEntry markAsPaid(Long entryId) {
        WorkEntry entry = getById(entryId);
        if (entry.getPaymentStatus() != WorkEntry.PaymentStatus.PAID) {
            entry.setPaymentStatus(WorkEntry.PaymentStatus.PAID);
            workEntryRepository.save(entry);
        }
        return entry;
    }

    public void deleteEntry(Long id) {
        WorkEntry entry = getById(id);
        // Subtract from client balance if pending
        if (entry.getPaymentStatus() == WorkEntry.PaymentStatus.PENDING) {
            clientService.addToClientBalance(entry.getClient().getId(), entry.getTotalAmount().negate());
        }
        workEntryRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalRevenueForMonth(int year, int month) {
        return workEntryRepository.sumAllAmountsByMonth(year, month);
    }

    // ---- Inner DTO for monthly bill ----
    public record MonthlyBillSummary(
            Client client,
            int year,
            int month,
            List<WorkEntry> entries,
            BigDecimal totalHours,
            BigDecimal totalAmount
    ) {}
}
