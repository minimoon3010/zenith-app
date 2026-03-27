package com.zenith.zenith_app.transaction;

import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionDTO(
        Long id,
        String transactionName,
        BigDecimal amount,
        LocalDateTime transactionCreated,
        LocalDateTime lastUpdated,
        TransactionType transactionType,
        TransactionCategory transactionCategory
) {
    public static TransactionDTO fromTransaction(Transaction transaction){
        return new TransactionDTO(
                transaction.getId(),
                transaction.getTransactionName(),
                transaction.getAmount(),
                transaction.getTransactionCreated(),
                transaction.getLastUpdated(),
                transaction.getTransactionType(),
                transaction.getTransactionCategory()
        );
    }
}
