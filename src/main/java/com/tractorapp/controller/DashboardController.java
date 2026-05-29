package com.tractorapp.controller;

import com.tractorapp.repository.ClientRepository;
import com.tractorapp.repository.WorkEntryRepository;
import com.tractorapp.service.WorkEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DashboardController {

    private final ClientRepository clientRepository;
    private final WorkEntryRepository workEntryRepository;
    private final WorkEntryService workEntryService;

    /**
     * Overview stats for the dashboard.
     * GET /api/dashboard/stats?year=2024&month=6
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats(
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getYear()}") int year,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getMonthValue()}") int month) {

        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();
        int currentMonth = today.getMonthValue();

        long totalClients = clientRepository.count();
        long totalEntries = workEntryRepository.count();
        BigDecimal monthRevenue = workEntryService.getTotalRevenueForMonth(year, month);
        BigDecimal currentMonthRevenue = workEntryService.getTotalRevenueForMonth(currentYear, currentMonth);
        long clientsWithDues = clientRepository.findClientsWithOutstandingBalance().size();
        BigDecimal totalOutstanding = clientRepository.findAll().stream()
                .map(c -> c.getOutstandingBalance())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return ResponseEntity.ok(Map.of(
                "totalClients", totalClients,
                "totalWorkEntries", totalEntries,
                "currentMonthRevenue", currentMonthRevenue,
                "queriedMonthRevenue", monthRevenue,
                "clientsWithOutstandingDues", clientsWithDues,
                "totalOutstandingAmount", totalOutstanding,
                "queriedYear", year,
                "queriedMonth", month
        ));
    }
}
