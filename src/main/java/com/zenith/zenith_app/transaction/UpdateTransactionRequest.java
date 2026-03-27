package com.zenith.zenith_app.transaction;

import java.math.BigDecimal;

public record UpdateTransactionRequest (
        String transactionName,
        BigDecimal amount,
        TransactionType transactionType,
        TransactionCategory transactionCategory){
}
