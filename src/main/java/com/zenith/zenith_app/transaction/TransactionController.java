package com.zenith.zenith_app.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transaction")
@RequiredArgsConstructor
public class TransactionController {

  private final TransactionService transactionService;

  @PostMapping("/new")
  public TransactionDTO createTransaction(
      @RequestBody CreateTransactionRequest transactionRequest,
      @AuthenticationPrincipal UserDetails userDetails) {
    return transactionService.createTransaction(transactionRequest, userDetails.getUsername());
  }

  @PutMapping("/update/{id}")
  public TransactionDTO updateTransaction(
      @RequestBody UpdateTransactionRequest updateTransactionRequest,
      @PathVariable Long id,
      @AuthenticationPrincipal UserDetails userDetails) {
    return transactionService.updateTransaction(
        updateTransactionRequest, id, userDetails.getUsername());
  }

  @GetMapping("/view/all")
  public List<TransactionDTO> viewAllTransactions(
      @AuthenticationPrincipal UserDetails userDetails) {
    return transactionService.viewAllTransactions(userDetails.getUsername());
  }

  @GetMapping("/view/id/{id}")
  public TransactionDTO viewTransactionById(
      @PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
    return transactionService.viewTransactionById(id, userDetails.getUsername());
  }

  @GetMapping("/view/name/{transactionName}")
  public List<TransactionDTO> viewTransactionByName(
      @PathVariable String transactionName, @AuthenticationPrincipal UserDetails userDetails) {
    return transactionService.viewTransactionByName(transactionName, userDetails.getUsername());
  }

  @GetMapping("/filter/amount")
  public List<TransactionDTO> filterTransactionByAmount(
      @RequestParam BigDecimal lower,
      @RequestParam BigDecimal higher,
      @AuthenticationPrincipal UserDetails userDetails)
      throws BadRequestException {
    return transactionService.filterTransactionByAmount(lower, higher, userDetails.getUsername());
  }

  @GetMapping("/filter/time")
  public List<TransactionDTO> filterTransactionByCreatedBetween(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime earlier,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime later,
      @AuthenticationPrincipal UserDetails userDetails) {
    return transactionService.filterTransactionByCreatedBetween(
        earlier, later, userDetails.getUsername());
  }

  @GetMapping("/filter/type/{type}")
  public List<TransactionDTO> filterTransactionByType(
      @PathVariable TransactionType type, @AuthenticationPrincipal UserDetails userDetails) {
    return transactionService.filterTransactionByType(type, userDetails.getUsername());
  }

  @GetMapping("/filter/category/{category}")
  public List<TransactionDTO> filterTransactionByCategory(
      @PathVariable TransactionCategory category,
      @AuthenticationPrincipal UserDetails userDetails) {
    return transactionService.filterTransactionByCategory(category, userDetails.getUsername());
  }

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/delete/id/{transactionId}")
  public void deleteTransactionById(
      @PathVariable Long transactionId, @AuthenticationPrincipal UserDetails userDetails) {
    transactionService.deleteTransactionById(transactionId, userDetails.getUsername());
  }

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/delete/name/{transactionName}")
  public void deleteTransactionByName(
      @PathVariable String transactionName, @AuthenticationPrincipal UserDetails userDetails) {
    transactionService.deleteTransactionByName(transactionName, userDetails.getUsername());
  }
}
