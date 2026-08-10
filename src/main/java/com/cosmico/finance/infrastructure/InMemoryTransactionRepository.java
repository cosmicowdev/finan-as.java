package com.cosmico.finance.infrastructure;

import com.cosmico.finance.application.TransactionRepository;
import com.cosmico.finance.domain.model.Transaction;

import java.util.ArrayList;
import java.util.List;

public final class InMemoryTransactionRepository implements TransactionRepository {
    private final List<Transaction> transactions = new ArrayList<>();
    private long nextId = 1;

    @Override
    public Transaction save(Transaction transaction) {
        Transaction stored = new Transaction(nextId++, transaction.getDescription(), transaction.getAmount(), transaction.getType(), transaction.getDate());
        transactions.add(stored);
        return stored;
    }

    @Override
    public List<Transaction> findAll() {
        return List.copyOf(transactions);
    }
}
