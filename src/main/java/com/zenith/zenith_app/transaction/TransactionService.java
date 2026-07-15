package com.zenith.zenith_app.transaction;

import com.zenith.zenith_app.config.ResourceNotFoundException;
import com.zenith.zenith_app.config.SecureEntity;
import com.zenith.zenith_app.config.XPService;
import com.zenith.zenith_app.user.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

  private final TransactionRepository transactionRepository;

  private final UserRepository userRepository;

  private final SecureEntity secureEntity;

  private final XPService xpService;

  public TransactionDTO createTransaction(CreateTransactionRequest request, String username) {
    if (request.transactionType() == TransactionType.DISMISSED) {
      throw new IllegalArgumentException(
          "New transactions cannot be created as 'Dismissed'.\nThat would be too easy!");
    }

    Transaction transaction =
        Transaction.builder()
            .transactionName(request.transactionName())
            .amount(request.amount())
            .transactionCreated(LocalDateTime.now())
            .lastUpdated(LocalDateTime.now())
            .transactionType(request.transactionType())
            .transactionCategory(request.transactionCategory())
            .user(
                userRepository
                    .findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Username not found.")))
            .build();

    if (transaction.getTransactionType() == TransactionType.IMPULSE) {
      xpService.setImpulseXPPenaltyUponCreation(transaction);
    }

    return TransactionDTO.fromTransaction(transactionRepository.save(transaction));
  }

  public TransactionDTO updateTransaction(
      UpdateTransactionRequest request, Long id, String username) {
    Transaction transaction = secureEntity.getSecureTransaction(id, username);

    if (transaction.getTransactionType() != TransactionType.POTENTIAL) {
      throw new IllegalArgumentException("Cannot edit a completed transaction!");
    }

    if (request.transactionName() != null) {
      transaction.setTransactionName(request.transactionName());
    }

    if (request.transactionCategory() != null) {
      transaction.setTransactionCategory(request.transactionCategory());
    }

    if (request.amount() != null) {
      transaction.setAmount(request.amount());
    }

    transaction.setLastUpdated(LocalDateTime.now());

    transaction.setTransactionType(request.transactionType());

    if (transaction.getTransactionType() == TransactionType.DISMISSED) {
      xpService.setDismissalXPBonus(transaction);
    } else if (transaction.getTransactionType() == TransactionType.IMPULSE) {
      xpService.setImpulseXPPenaltyUponUpdating(transaction);
    }

    return TransactionDTO.fromTransaction(transactionRepository.save(transaction));
  }

  public TransactionDTO viewTransactionById(Long id, String username) {
    Transaction transaction = secureEntity.getSecureTransaction(id, username);
    return TransactionDTO.fromTransaction(transaction);
  }

  public List<TransactionDTO> viewTransactionByName(String transactionName, String username) {
    return secureEntity.getSecureTransactionByName(transactionName, username).stream()
        .map(TransactionDTO::fromTransaction)
        .toList();
  }

  public List<TransactionDTO> filterTransactionByAmount(
      BigDecimal lower, BigDecimal higher, String username) {
    return transactionRepository
        .findByAmountBetweenAndUser_Username(lower, higher, username)
        .stream()
        .map(TransactionDTO::fromTransaction)
        .toList();
  }

  public List<TransactionDTO> filterTransactionByCreatedBetween(
      LocalDateTime earlier, LocalDateTime later, String username) {
    return transactionRepository
        .findByTransactionCreatedBetweenAndUser_Username(earlier, later, username)
        .stream()
        .map(TransactionDTO::fromTransaction)
        .toList();
  }

  public List<TransactionDTO> filterTransactionByType(
      TransactionType transactionType, String username) {
    return transactionRepository
        .findByTransactionTypeAndUser_Username(transactionType, username)
        .stream()
        .map(TransactionDTO::fromTransaction)
        .toList();
  }

  public List<TransactionDTO> filterTransactionByCategory(
      TransactionCategory transactionCategory, String username) {
    return transactionRepository
        .findByTransactionCategoryAndUser_Username(transactionCategory, username)
        .stream()
        .map(TransactionDTO::fromTransaction)
        .toList();
  }

  public List<TransactionDTO> viewAllTransactions(String username) {
    return transactionRepository.findByUser_Username(username).stream()
        .map(TransactionDTO::fromTransaction)
        .toList();
  }

  public void deleteTransactionById(Long id, String username) {
    Transaction transaction = secureEntity.getSecureTransaction(id, username);
    deletingPotentialTransactionOnly(transaction);
  }

  public void deleteTransactionByName(String transactionName, String username) {
    List<Transaction> transactions =
        transactionRepository.findByTransactionNameAndUser_Username(transactionName, username);
    if (transactions.isEmpty()) {
      throw new ResourceNotFoundException("Transaction with this name does not exist.");
    } else {
      transactions.forEach(this::deletingPotentialTransactionOnly);
    }
  }

  private void deletingPotentialTransactionOnly(Transaction transaction) {
    if (transaction.getTransactionType() == TransactionType.POTENTIAL) {
      transactionRepository.delete(transaction);
    } else if (transaction.getTransactionType() == TransactionType.IMPULSE) {
      throw new IllegalArgumentException("That's cheating...");
    } else {
      log.info("Keeping this transaction for ML predictor model.");
    }
  }
}
