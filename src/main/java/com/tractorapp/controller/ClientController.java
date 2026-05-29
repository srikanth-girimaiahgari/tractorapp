package com.tractorapp.controller;

import com.tractorapp.model.Client;
import com.tractorapp.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ClientController {

    private final ClientService clientService;

    @PostMapping
    public ResponseEntity<Client> createClient(@Valid @RequestBody Client client) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clientService.createClient(client));
    }

    @GetMapping
    public ResponseEntity<List<Client>> getAllClients(
            @RequestParam(required = false) String search) {
        if (search != null && !search.isBlank()) {
            return ResponseEntity.ok(clientService.searchClients(search));
        }
        return ResponseEntity.ok(clientService.getAllClients());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Client> getClient(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.getClientById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Client> updateClient(@PathVariable Long id,
                                               @Valid @RequestBody Client client) {
        return ResponseEntity.ok(clientService.updateClient(id, client));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/outstanding")
    public ResponseEntity<List<Client>> getClientsWithOutstanding() {
        return ResponseEntity.ok(clientService.getClientsWithOutstanding());
    }

    @PostMapping("/{id}/payment")
    public ResponseEntity<Map<String, String>> recordPayment(
            @PathVariable Long id,
            @RequestBody Map<String, BigDecimal> body) {
        clientService.recordPayment(id, body.get("amount"));
        return ResponseEntity.ok(Map.of("message", "Payment recorded successfully"));
    }
}
