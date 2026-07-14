package com.zenith.zenith_app.config;

import com.zenith.zenith_app.constellation.Constellation;
import com.zenith.zenith_app.constellation.ConstellationRepository;
import com.zenith.zenith_app.star.Star;
import com.zenith.zenith_app.star.StarRepository;
import com.zenith.zenith_app.transaction.Transaction;
import com.zenith.zenith_app.transaction.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecureEntity {

  private final TransactionRepository transactionRepository;
  private final StarRepository starRepository;
  private final ConstellationRepository constellationRepository;

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
}
