package com.zenith.zenith_app.transaction;

import com.zenith.zenith_app.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.zenith.zenith_app.config.ZenithConstants.TRANSACTION_DISMISSED_XP;
import static com.zenith.zenith_app.config.ZenithConstants.TRANSACTION_IMPULSE_XP;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    private final UserRepository userRepository;

    public TransactionDTO createTransaction(CreateTransactionRequest request, String username){
        if (request.transactionType() == TransactionType.DISMISSED) {
            throw new IllegalArgumentException("New transactions cannot be created as 'Dismissed'.\nThat would be too easy!");
        }

        Transaction transaction = Transaction.builder()
                .transactionName(request.transactionName())
                .amount(request.amount())
                .transactionCreated(LocalDateTime.now())
                .lastUpdated(LocalDateTime.now())
                .transactionType(request.transactionType())
                .transactionCategory(request.transactionCategory())
                .user(userRepository.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("Username not found.")))
                .build();

        if (transaction.getTransactionType() == TransactionType.IMPULSE) {
            setImpulseXPPenaltyUponCreation(transaction);
        }

        return TransactionDTO.fromTransaction(transactionRepository.save(transaction));
    }

    public TransactionDTO updateTransaction(UpdateTransactionRequest request, Long id, String username){
        Transaction transaction = getSecureTransaction(id, username);

        if (transaction.getTransactionType() != TransactionType.POTENTIAL) {
            throw new RuntimeException("Cannot edit a completed transaction!");
        }

        if (request.transactionName() != null) {
            transaction.setTransactionName(request.transactionName());
        }

        if (request.transactionCategory() != null) {
            transaction.setTransactionCategory(request.transactionCategory());
        }

        if (request.amount() != null){
            transaction.setAmount(request.amount());
        }

        transaction.setLastUpdated(LocalDateTime.now());

        transaction.setTransactionType(request.transactionType());

        if (transaction.getTransactionType() == TransactionType.DISMISSED){
            setDismissalXPBonus(transaction);
        } else if (transaction.getTransactionType() == TransactionType.IMPULSE) {
            setImpulseXPPenaltyUponUpdating(transaction);
        }

        return TransactionDTO.fromTransaction(transactionRepository.save(transaction));
    }

    public TransactionDTO viewTransactionById(Long id, String username){
        Transaction transaction = getSecureTransaction(id, username);
        return TransactionDTO.fromTransaction(transaction);
    }

    public List<TransactionDTO> viewTransactionByName(String transactionName, String username) {
        return transactionRepository.findByTransactionNameAndUser_Username(transactionName, username)
                .stream()
                .map(TransactionDTO::fromTransaction)
                .collect(Collectors.toList());
    }

    public List<TransactionDTO> filterTransactionByAmount(BigDecimal lower, BigDecimal higher, String username) {
        return transactionRepository.findByAmountBetweenAndUser_Username(lower, higher, username)
                .stream()
                .map(TransactionDTO::fromTransaction)
                .collect(Collectors.toList());
    }

    public List<TransactionDTO> filterTransactionByCreatedBetween(LocalDateTime earlier, LocalDateTime later, String username) {
        return transactionRepository.findByTransactionCreatedBetweenAndUser_Username(earlier, later, username)
                .stream()
                .map(TransactionDTO::fromTransaction)
                .collect(Collectors.toList());
    }

    public List<TransactionDTO> filterTransactionByType(TransactionType transactionType, String username) {
        return transactionRepository.findByTransactionTypeAndUser_Username(transactionType, username)
                .stream()
                .map(TransactionDTO::fromTransaction)
                .collect(Collectors.toList());
    }

    public List<TransactionDTO> filterTransactionByCategory(TransactionCategory transactionCategory, String username) {
        return transactionRepository.findByTransactionCategoryAndUser_Username(transactionCategory, username)
                .stream()
                .map(TransactionDTO::fromTransaction)
                .collect(Collectors.toList());
    }

    public List<TransactionDTO> viewAllTransactions(String username){
        return transactionRepository.findByUser_Username(username)
                .stream()
                .map(TransactionDTO::fromTransaction)
                .collect(Collectors.toList());
    }

    public void deleteTransactionById(Long id, String username){
        Transaction transaction = getSecureTransaction(id, username);
        deletingPotentialTransactionOnly(transaction);
    }

    public void deleteTransactionByName(String transactionName, String username){
        List<Transaction> transactions = transactionRepository.findByTransactionNameAndUser_Username(transactionName, username);
        if (transactions.isEmpty()){
            throw new RuntimeException("Transaction with this name does not exist.");
        } else {
            transactions.forEach(this::deletingPotentialTransactionOnly);
        }
    }

    private void setDismissalXPBonus(Transaction transaction){
        if (!transaction.isXpAwarded()){
            int userXP = transaction.getUser().getXp();

            // Adding 30XP as a reward
            transaction.getUser().setXp(userXP + TRANSACTION_DISMISSED_XP);

            transaction.setXpAwarded(true);
            userRepository.save(transaction.getUser());
        }
    }

    private void setImpulseXPPenaltyUponUpdating(Transaction transaction){
        if (!transaction.isXpAwarded()){
            int userXP = transaction.getUser().getXp();

            // So that XP does not go below 0 when deducting 40XP
            transaction.getUser().setXp(Math.max(0, userXP - TRANSACTION_IMPULSE_XP));

            transaction.setXpAwarded(true);
            userRepository.save(transaction.getUser());
        }
    }

    private void setImpulseXPPenaltyUponCreation(Transaction transaction){
        if (!transaction.isXpAwarded()){
            int userXP = transaction.getUser().getXp();

            // So that XP does not go below 0 when deducting 20XP.
            // 20 instead of 40 because they were being honest :)
            transaction.getUser().setXp(Math.max(0, userXP - (TRANSACTION_IMPULSE_XP - 20)));

            transaction.setXpAwarded(true);
            userRepository.save(transaction.getUser());
        }
    }

    private void deletingPotentialTransactionOnly(Transaction transaction){
        if (transaction.getTransactionType() == TransactionType.POTENTIAL){
            transactionRepository.delete(transaction);
        } else {
            throw new RuntimeException("That's cheating...");
        }
    }

    private Transaction getSecureTransaction(Long id, String username) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction does not exist."));

        if (!transaction.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorised access!");
        }
        return transaction;
    }
}
