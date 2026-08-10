package com.cosmico.finance.application;

import com.cosmico.finance.domain.model.Account;
import com.cosmico.finance.infrastructure.repository.AccountRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public final class AccountService {
    private final AccountRepository repository;

    public AccountService(AccountRepository repository) {
        this.repository = repository;
    }

    public Account create(String name) {
        Account account = new Account(name);
        repository.save(account);
        return account;
    }

    public void deposit(UUID accountId, BigDecimal amount) {
        Account account = get(accountId);
        account.credit(amount);
        repository.save(account);
    }

    public Account get(UUID accountId) {
        return repository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
    }

    public List<Account> list() {
        return repository.findAll();
    }
}
