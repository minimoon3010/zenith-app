package com.zenith.zenith_app.config;

import com.zenith.zenith_app.constellation.Constellation;
import com.zenith.zenith_app.constellation.ConstellationRepository;
import com.zenith.zenith_app.mood.Mood;
import com.zenith.zenith_app.mood.MoodRepository;
import com.zenith.zenith_app.star.Star;
import com.zenith.zenith_app.star.StarRepository;
import com.zenith.zenith_app.transaction.Transaction;
import com.zenith.zenith_app.transaction.TransactionCategory;
import com.zenith.zenith_app.transaction.TransactionRepository;
import com.zenith.zenith_app.transaction.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecureEntity {

  private final TransactionRepository transactionRepository;
  private final StarRepository starRepository;
  private final ConstellationRepository constellationRepository;
  private final MoodRepository moodRepository;

  public Transaction getSecureTransaction(Long id, String username) {
    Transaction transaction =
        transactionRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Transaction does not exist."));

    if (!transaction.getUser().getUsername().equals(username)) {
      throw new BadCredentialsException("Unauthorised access!");
    }
    return transaction;
  }

  public List<Transaction> getSecureTransactionByName(String transactionName, String username) {
    List<Transaction> transactions =
        transactionRepository.findByTransactionNameAndUser_Username(transactionName, username);

    if (transactions.isEmpty()) {
      throw new ResourceNotFoundException(
          "Transaction with name " + transactionName + " not found!");
    }
    return transactions;
  }

  public List<Transaction> getSecureTransactionByCategory(
      TransactionCategory transactionCategory, String username) {
    List<Transaction> transactions =
        transactionRepository.findByTransactionCategoryAndUser_Username(
            transactionCategory, username);

    if (transactions.isEmpty()) {
      throw new ResourceNotFoundException(
          "Transaction with category " + transactionCategory + " not found!");
    }

    return transactions;
  }

  public List<Transaction> getSecureTransactionByType(
      TransactionType transactionType, String username) {
    List<Transaction> transactions =
        transactionRepository.findByTransactionTypeAndUser_Username(transactionType, username);

    if (transactions.isEmpty()) {
      throw new ResourceNotFoundException(
          "Transaction with type " + transactionType + " not found!");
    }
    return transactions;
  }

  public List<Transaction> getSecureTransactionByAmount(
      BigDecimal lower, BigDecimal higher, String username) throws BadRequestException {
    List<Transaction> transactions =
        transactionRepository.findByAmountBetweenAndUser_Username(lower, higher, username);

    if (higher.compareTo(lower) < 0) {
      throw new BadRequestException("Higher amount can't be less than lower amount.");
    }

    if (transactions.isEmpty()) {
      throw new ResourceNotFoundException(
          "Transaction between £" + lower + " and £" + higher + " not found!");
    }

    return transactions;
  }

  public List<Transaction> getSecureTransactionByTimestamp(
      LocalDateTime earlier, LocalDateTime later, String username) {
    List<Transaction> transactions =
        transactionRepository.findByTransactionCreatedBetweenAndUser_Username(
            earlier, later, username);

    if (transactions.isEmpty()) {
      throw new ResourceNotFoundException(
          "Transaction between " + earlier + " and " + later + " not found!");
    }
    return transactions;
  }

  public Star getSecureStar(Long id, String username) {
    Star star =
        starRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Star does not exist."));

    if (!star.getUser().getUsername().equals(username)) {
      throw new BadCredentialsException("Unauthorised access!");
    }

    return star;
  }

  public Constellation getSecureConstellation(Long id, String username) {
    Constellation constellation =
        constellationRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Constellation does not exist."));

    if (!constellation.getUser().getUsername().equals(username)) {
      throw new BadCredentialsException("Unauthorised access!");
    }
    return constellation;
  }

  public Mood getSecureMood(Long id, String username) {
    Mood mood =
        moodRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Mood does not exist."));

    if (!mood.getUser().getUsername().equals(username)) {
      throw new ResourceNotFoundException("No record found!");
    }
    return mood;
  }
}
