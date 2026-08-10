package com.cosmico.finance.domain.model;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public final class Account {
    private final UUID id;
    private final String name;
    private BigDecimal balance;

    public Account(String name) {
        this(UUID.randomUUID(), name, BigDecimal.ZERO);
    }

    public Account(UUID id, String name, BigDecimal balance) {
        if (id == null) throw new IllegalArgumentException("Account id is required");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Account name cannot be empty");
        if (balance == null) throw new IllegalArgumentException("Balance is required");
        this.id = id;
        this.name = name.trim();
        this.balance = balance;
    }

    public void credit(BigDecimal amount) {
        validateAmount(amount);
        balance = balance.add(amount);
    }

    public void debit(BigDecimal amount) {
        validateAmount(amount);
        if (balance.compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient balance");
        }
        balance = balance.subtract(amount);
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public BigDecimal getBalance() { return balance; }

    @Override
    public boolean equals(Object other) {
        return other instanceof Account account && id.equals(account.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
