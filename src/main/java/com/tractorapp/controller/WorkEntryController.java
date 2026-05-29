package com.tractorapp.controller;

import com.tractorapp.model.Client;
import com.tractorapp.model.WorkEntry;
import com.tractorapp.service.WorkEntryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/work")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WorkEntryController {

    private final WorkEntryService workEntryService;

    /**
     * Log new field work - auto-calculates amount and adds to client balance.
     * POST /api/work
     * Body: { clientId, workDate, workType, fieldDescription, hoursWorked, ratePerHour, notes }
     */
    @PostMapping
    public ResponseEntity<WorkEntry> logWork(@RequestBody Map<String, Object> body) {
        WorkEntry entry = new WorkEntry();

        // Set client reference
        Client client = new Client();
        client.setId(Long.valueOf(body.get("clientId").toString()));
        entry.setClient(client);

        entry.setWorkDate(LocalDate.parse(body.get("workDate").toString()));
        entry.setWorkType(body.getOrDefault("workType", "GENERAL").toString());
        entry.setFieldDescription(body.getOrDefault("fieldDescription", "").toString());
        entry.setHoursWorked(new java.math.BigDecimal(body.get("hoursWorked").toString()));
        entry.setRatePerHour(new java.math.BigDecimal(body.get("ratePerHour").toString()));
        if (body.containsKey("notes")) {
            entry.setNotes(body.get("notes").toString());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(workEntryService.logWork(entry));
    }

    @GetMapping("/today")
    public ResponseEntity<List<WorkEntry>> getTodaysWork() {
        return ResponseEntity.ok(workEntryService.getTodaysWork());
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<WorkEntry>> getClientWork(@PathVariable Long clientId) {
        return ResponseEntity.ok(workEntryService.getEntriesByClient(clientId));
    }

    /**
     * Monthly bill for a client.
     * GET /api/work/client/{id}/monthly-bill?year=2024&month=6
     */
    @GetMapping("/client/{clientId}/monthly-bill")
    public ResponseEntity<WorkEntryService.MonthlyBillSummary> getMonthlyBill(
            @PathVariable Long clientId,
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(workEntryService.getMonthlyBill(clientId, year, month));
    }

    @GetMapping("/range")
    public ResponseEntity<List<WorkEntry>> getByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(workEntryService.getEntriesForDateRange(from, to));
    }

    @PostMapping("/{id}/mark-paid")
    public ResponseEntity<WorkEntry> markAsPaid(@PathVariable Long id) {
        return ResponseEntity.ok(workEntryService.markAsPaid(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEntry(@PathVariable Long id) {
        workEntryService.deleteEntry(id);
        return ResponseEntity.noContent().build();
    }
}
