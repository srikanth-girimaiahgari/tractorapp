package com.tractorapp;

import com.tractorapp.model.Client;
import com.tractorapp.model.WorkEntry;
import com.tractorapp.service.ClientService;
import com.tractorapp.service.WorkEntryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final ClientService clientService;
    private final WorkEntryService workEntryService;

    @Override
    public void run(String... args) {
        log.info("Seeding demo data...");

        // Create 3 demo clients
        Client raju = new Client();
        raju.setName("Raju Reddy");
        raju.setPhone("9876543210");
        raju.setVillage("Nalgonda");
        raju = clientService.createClient(raju);

        Client suresh = new Client();
        suresh.setName("Suresh Kumar");
        suresh.setPhone("9988776655");
        suresh.setVillage("Warangal");
        suresh = clientService.createClient(suresh);

        Client lakshmi = new Client();
        lakshmi.setName("Lakshmi Devi");
        lakshmi.setPhone("9090909090");
        lakshmi.setVillage("Karimnagar");
        lakshmi = clientService.createClient(lakshmi);

        // Log work entries - current month
        LocalDate today = LocalDate.now();
        logWork(raju, today.minusDays(3), "North paddy field - ploughing", "PLOUGHING", new BigDecimal("6.5"), new BigDecimal("500"));
        logWork(raju, today.minusDays(1), "South wheat field - tilling", "TILLING", new BigDecimal("4.0"), new BigDecimal("500"));
        logWork(suresh, today.minusDays(5), "East cotton field - levelling", "LEVELLING", new BigDecimal("8.0"), new BigDecimal("450"));
        logWork(suresh, today.minusDays(2), "West sugarcane - harvesting", "HARVESTING", new BigDecimal("10.0"), new BigDecimal("600"));
        logWork(lakshmi, today.minusDays(4), "Main field - ploughing", "PLOUGHING", new BigDecimal("5.5"), new BigDecimal("500"));

        // Last month entries
        logWork(raju, today.minusMonths(1).withDayOfMonth(10), "Paddy field prep", "PLOUGHING", new BigDecimal("7.0"), new BigDecimal("500"));
        logWork(suresh, today.minusMonths(1).withDayOfMonth(15), "Field levelling", "LEVELLING", new BigDecimal("6.0"), new BigDecimal("450"));

        log.info("Demo data seeded successfully!");
    }

    private void logWork(Client client, LocalDate date, String desc, String type,
                         BigDecimal hours, BigDecimal rate) {
        WorkEntry entry = new WorkEntry();
        entry.setClient(client);
        entry.setWorkDate(date);
        entry.setFieldDescription(desc);
        entry.setWorkType(type);
        entry.setHoursWorked(hours);
        entry.setRatePerHour(rate);
        workEntryService.logWork(entry);
    }
}
