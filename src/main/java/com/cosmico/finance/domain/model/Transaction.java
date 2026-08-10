package com.cosmico.finance.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public final class Transaction {
    private final long id;
    private final String description;
    private final BigDecimal amount;
    private final TransactionType type;
    private final LocalDate date;

    public Transaction(long id, String description, BigDecimal amount, TransactionType type, LocalDate date) {
        if (id < 0) throw new IllegalArgumentException("ID cannot be negative");
        if (description == null || description.isBlank()) throw new IllegalArgumentException("Description is required");
        if (amount == null || amount.signum() <= 0) throw new IllegalArgumentException("Amount must be positive");
        this.id = id;
        this.description = description.trim();
        this.amount = amount;
        this.type = Objects.requireNonNull(type);
        this.date = Objects.requireNonNull(date);
    }

    public long getId() { return id; }
    public String getDescription() { return description; }
    public BigDecimal getAmount() { return amount; }
    public TransactionType getType() { return type; }
    public LocalDate getDate() { return date; }
}
