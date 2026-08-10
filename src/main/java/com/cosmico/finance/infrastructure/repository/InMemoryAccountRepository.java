package com.cosmico.finance.infrastructure.repository;

import com.cosmico.finance.domain.model.Account;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class InMemoryAccountRepository implements AccountRepository {
    private final List<Account> accounts = new ArrayList<>();

    @Override
    public void save(Account account) {
        accounts.removeIf(existing -> existing.getId().equals(account.getId()));
        accounts.add(account);
    }

    @Override
    public Optional<Account> findById(UUID id) {
        return accounts.stream().filter(account -> account.getId().equals(id)).findFirst();
    }

    @Override
    public List<Account> findAll() {
        return List.copyOf(accounts);
    }
}
