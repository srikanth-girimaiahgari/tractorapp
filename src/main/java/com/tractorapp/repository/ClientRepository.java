package com.tractorapp.repository;

import com.tractorapp.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    List<Client> findByNameContainingIgnoreCase(String name);

    List<Client> findByVillage(String village);

    @Query("SELECT c FROM Client c WHERE c.outstandingBalance > 0 ORDER BY c.outstandingBalance DESC")
    List<Client> findClientsWithOutstandingBalance();
}
