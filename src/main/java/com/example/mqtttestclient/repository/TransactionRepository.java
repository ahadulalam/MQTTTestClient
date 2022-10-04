package com.example.mqtttestclient.repository;

import com.example.mqtttestclient.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
