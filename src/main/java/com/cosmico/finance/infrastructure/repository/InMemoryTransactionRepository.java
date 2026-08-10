package com.cosmico.finance.infrastructure.repository;

import com.cosmico.finance.domain.model.Transaction;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public final class InMemoryTransactionRepository implements TransactionRepository {
    private final List<Transaction> transactions = new ArrayList<>();

    @Override
    public void save(Transaction transaction) {
        transactions.removeIf(existing -> existing.getId().equals(transaction.getId()));
        transactions.add(transaction);
    }

    @Override
    public List<Transaction> findByAccount(UUID accountId) {
        return transactions.stream()
                .filter(transaction -> transaction.getAccountId().equals(accountId))
                .sorted(Comparator.comparing(Transaction::getCreatedAt).reversed())
                .toList();
    }

    @Override
    public List<Transaction> findAll() {
        return List.copyOf(transactions);
    }
}
