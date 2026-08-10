package com.cosmico.finance.application;

import com.cosmico.finance.domain.model.Transaction;
import com.cosmico.finance.domain.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public final class FinanceService {
    private final TransactionRepository repository;

    public FinanceService(TransactionRepository repository) {
        this.repository = repository;
    }

    public Transaction register(String description, BigDecimal amount, TransactionType type, LocalDate date) {
        return repository.save(new Transaction(0, description, amount, type, date));
    }

    public List<Transaction> listTransactions() {
        return repository.findAll();
    }

    public BigDecimal calculateBalance() {
        return repository.findAll().stream()
                .map(t -> t.getType() == TransactionType.INCOME ? t.getAmount() : t.getAmount().negate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
