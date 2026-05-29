package com.tractorapp.service;

import com.tractorapp.model.Client;
import com.tractorapp.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientService {

    private final ClientRepository clientRepository;

    public Client createClient(Client client) {
        client.setOutstandingBalance(BigDecimal.ZERO);
        return clientRepository.save(client);
    }

    public Client updateClient(Long id, Client updated) {
        Client existing = getClientById(id);
        existing.setName(updated.getName());
        existing.setPhone(updated.getPhone());
        existing.setVillage(updated.getVillage());
        existing.setAddress(updated.getAddress());
        return clientRepository.save(existing);
    }

    @Transactional(readOnly = true)
    public Client getClientById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Client> searchClients(String name) {
        return clientRepository.findByNameContainingIgnoreCase(name);
    }

    @Transactional(readOnly = true)
    public List<Client> getClientsWithOutstanding() {
        return clientRepository.findClientsWithOutstandingBalance();
    }

    public void addToClientBalance(Long clientId, BigDecimal amount) {
        Client client = getClientById(clientId);
        client.setOutstandingBalance(client.getOutstandingBalance().add(amount));
        clientRepository.save(client);
    }

    public void recordPayment(Long clientId, BigDecimal amount) {
        Client client = getClientById(clientId);
        BigDecimal newBalance = client.getOutstandingBalance().subtract(amount);
        client.setOutstandingBalance(newBalance.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : newBalance);
        clientRepository.save(client);
    }

    public void deleteClient(Long id) {
        clientRepository.deleteById(id);
    }
}
