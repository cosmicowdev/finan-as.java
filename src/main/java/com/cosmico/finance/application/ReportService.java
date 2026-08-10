package com.cosmico.finance.application;

import com.cosmico.finance.domain.enums.TransactionType;
import com.cosmico.finance.domain.model.Transaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public final class ReportService {
    private final TransactionService transactionService;

    public ReportService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public BigDecimal totalIncome(UUID accountId) {
        return total(accountId, TransactionType.INCOME);
    }

    public BigDecimal totalExpenses(UUID accountId) {
        return total(accountId, TransactionType.EXPENSE);
    }

    public BigDecimal balance(UUID accountId) {
        return totalIncome(accountId).subtract(totalExpenses(accountId));
    }

    private BigDecimal total(UUID accountId, TransactionType type) {
        return transactionService.history(accountId).stream()
                .filter(transaction -> transaction.getType() == type)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
