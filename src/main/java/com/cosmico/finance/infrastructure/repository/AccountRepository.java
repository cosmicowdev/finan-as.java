package com.cosmico.finance.infrastructure.repository;

import com.cosmico.finance.domain.model.Account;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository {
    void save(Account account);
    Optional<Account> findById(UUID id);
    List<Account> findAll();
}
