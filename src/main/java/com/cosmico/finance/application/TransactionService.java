package com.cosmico.finance.application;

import com.cosmico.finance.domain.enums.TransactionType;
import com.cosmico.finance.domain.model.Account;
import com.cosmico.finance.domain.model.Category;
import com.cosmico.finance.domain.model.Transaction;
import com.cosmico.finance.infrastructure.repository.AccountRepository;
import com.cosmico.finance.infrastructure.repository.TransactionRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public final class TransactionService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransactionService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public Transaction register(UUID accountId, BigDecimal amount, TransactionType type,
                                Category category, String description) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        if (type == TransactionType.INCOME) {
            account.credit(amount);
        } else {
            account.debit(amount);
        }

        accountRepository.save(account);
        Transaction transaction = new Transaction(accountId, amount, type, category, description);
        transactionRepository.save(transaction);
        return transaction;
    }

    public List<Transaction> history(UUID accountId) {
        return transactionRepository.findByAccount(accountId);
    }
}
