package com.cosmico.finance.application;

import com.cosmico.finance.domain.model.Transaction;

import java.util.List;

public interface TransactionRepository {
    Transaction save(Transaction transaction);
    List<Transaction> findAll();
}
