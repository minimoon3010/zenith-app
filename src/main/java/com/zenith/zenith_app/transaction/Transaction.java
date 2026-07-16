package com.zenith.zenith_app.transaction;

import com.zenith.zenith_app.user.User;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "z_transaction")
public class Transaction {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  private String transactionName;

  private BigDecimal amount;

  private LocalDateTime transactionCreated;

  private LocalDateTime lastUpdated;

  @Enumerated(EnumType.STRING)
  private TransactionType transactionType;

  @Enumerated(EnumType.STRING)
  private TransactionCategory transactionCategory;

  private boolean xpAwarded = false;
}
