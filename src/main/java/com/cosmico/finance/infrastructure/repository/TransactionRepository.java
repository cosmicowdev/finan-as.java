package com.cosmico.finance.infrastructure.repository;

import com.cosmico.finance.domain.model.Transaction;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository {
    void save(Transaction transaction);
    List<Transaction> findByAccount(UUID accountId);
    List<Transaction> findAll();
}
