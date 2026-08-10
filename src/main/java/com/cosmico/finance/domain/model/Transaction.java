package com.cosmico.finance.domain.model;

import com.cosmico.finance.domain.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public final class Transaction {
    private final UUID id;
    private final UUID accountId;
    private final BigDecimal amount;
    private final TransactionType type;
    private final Category category;
    private final String description;
    private final LocalDateTime createdAt;

    public Transaction(UUID accountId, BigDecimal amount, TransactionType type, Category category, String description) {
        this(UUID.randomUUID(), accountId, amount, type, category, description, LocalDateTime.now());
    }

    public Transaction(UUID id, UUID accountId, BigDecimal amount, TransactionType type, Category category, String description, LocalDateTime createdAt) {
        if (id == null || accountId == null) throw new IllegalArgumentException("Transaction identifiers are required");
        if (amount == null || amount.signum() <= 0) throw new IllegalArgumentException("Amount must be greater than zero");
        if (type == null) throw new IllegalArgumentException("Transaction type is required");
        if (category == null) throw new IllegalArgumentException("Category is required");
        this.id = id;
        this.accountId = accountId;
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.description = description == null ? "" : description.trim();
        this.createdAt = createdAt == null ? LocalDateTime.now() : createdAt;
    }

    public UUID getId() { return id; }
    public UUID getAccountId() { return accountId; }
    public BigDecimal getAmount() { return amount; }
    public TransactionType getType() { return type; }
    public Category getCategory() { return category; }
    public String getDescription() { return description; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
