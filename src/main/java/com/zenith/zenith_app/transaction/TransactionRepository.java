package com.zenith.zenith_app.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

  List<Transaction> findByTransactionNameAndUser_Username(String transactionName, String username);

  List<Transaction> findByAmountBetweenAndUser_Username(
      BigDecimal lowerAmount, BigDecimal higherAmount, String username);

  List<Transaction> findByTransactionCreatedBetweenAndUser_Username(
      LocalDateTime earlierTime, LocalDateTime laterTime, String username);

  List<Transaction> findByTransactionTypeAndUser_Username(
      TransactionType transactionType, String username);

  List<Transaction> findByTransactionCategoryAndUser_Username(
      TransactionCategory transactionCategory, String username);

  List<Transaction> findByUser_Username(String username);
}
