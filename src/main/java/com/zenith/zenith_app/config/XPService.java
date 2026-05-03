package com.zenith.zenith_app.config;

import static com.zenith.zenith_app.config.ZenithConstants.*;

import com.zenith.zenith_app.constellation.Constellation;
import com.zenith.zenith_app.constellation.ConstellationStatus;
import com.zenith.zenith_app.star.Star;
import com.zenith.zenith_app.star.StarStatus;
import com.zenith.zenith_app.transaction.Transaction;
import com.zenith.zenith_app.user.User;
import com.zenith.zenith_app.user.UserRepository;
import java.time.LocalDate;
import java.time.Month;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class XPService {

  private final UserRepository userRepository;

  /**
   * XP reward that gets added when a user completes a star. If the constellation in which the star
   * is in has 5 or more stars, then the user earns 15XP per star completed. Otherwise, they earn
   * 10XP per star completed.
   *
   * @param star the star in question
   */
  public void awardStarXP(Star star) {
    if (!star.isXpAwarded() && star.getStatus().equals(StarStatus.COMPLETED)) {
      int userXP = star.getUser().getXp();

      // Add the star XP as a reward for completing it
      star.getUser().setXp(userXP + star.getXp());

      star.setXpAwarded(true);
      checkAndUpdateUserLevel(star.getUser());
    }
  }

  /**
   * XP reward that gets added when a user completes a constellation. //TODO: enhance by adding
   * friend method where if a friend also is in it they get 87XP each.
   *
   * @param constellation the constellation in question
   */
  public void awardConstellationXP(Constellation constellation) {
    if (!constellation.isXpAwarded()
        && constellation.getStatus().equals(ConstellationStatus.COMPLETE)) {
      int userXp = constellation.getUser().getXp();

      // Add the constellation XP as a reward for completing it.
      // TODO: condition to check if friend is there.
      constellation.getUser().setXp(userXp + CONSTELLATION_XP);

      constellation.setXpAwarded(true);
      checkAndUpdateUserLevel(constellation.getUser());
    }
  }

  /**
   * XP reward that gets added when a transaction is classified as DISMISSED
   *
   * @param transaction the transaction, usually by id
   */
  public void setDismissalXPBonus(Transaction transaction) {
    if (!transaction.isXpAwarded()) {
      int userXP = transaction.getUser().getXp();

      // Adding 30XP as a reward
      transaction.getUser().setXp(userXP + TRANSACTION_DISMISSED_XP);

      transaction.setXpAwarded(true);
      checkAndUpdateUserLevel(transaction.getUser());
    }
  }

  /**
   * XP penalty that gets deducted when a transaction is classified as IMPULSE
   *
   * @param transaction the transaction, usually by id
   */
  public void setImpulseXPPenaltyUponUpdating(Transaction transaction) {
    if (!transaction.isXpAwarded()) {
      int userXP = transaction.getUser().getXp();

      // So that XP does not go below 0 when deducting 40XP
      transaction.getUser().setXp(Math.max(0, userXP + TRANSACTION_IMPULSE_XP));

      transaction.setXpAwarded(true);
      checkAndUpdateUserLevel(transaction.getUser());
    }
  }

  /**
   * XP penalty that gets deducted when a transaction is first logged as IMPULSE
   *
   * @param transaction the transaction, usually by id
   */
  public void setImpulseXPPenaltyUponCreation(Transaction transaction) {
    if (!transaction.isXpAwarded()) {
      int userXP = transaction.getUser().getXp();

      // So that XP does not go below 0 when deducting 20XP.
      // 20 instead of 40 because they were being honest :)
      transaction.getUser().setXp(Math.max(0, userXP + TRANSACTION_IMPULSE_XP_HONEST));

      transaction.setXpAwarded(true);
      checkAndUpdateUserLevel(transaction.getUser());
    }
  }

  /**
   * XP reward method for the daily check in method
   *
   * @param userId the user
   */
  public void dailyCheckXP(Long userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new RuntimeException("User does not exist."));

    LocalDate lastActivity = user.getLastActivity();

    if (today().equals(lastActivity)) {
      return;
    }

    if (wasActiveYesterday(lastActivity)) {
      user.setXp(user.getXp() + DAILY_CHECK_IN);
    }

    user.setLastActivity(today());
    checkAndUpdateUserLevel(user);
  }

  /**
   * Private method to check if the user was active the day before, required for the daily check in
   * streak
   *
   * @param userActivity the user's last activity, i.e. when they were last active
   * @return true if the user was active one calendar day before (not 24 hours before!)
   */
  private boolean wasActiveYesterday(LocalDate userActivity) {
    LocalDate yesterday = today().minusDays(1);
    if (userActivity == null) return false;
    return userActivity.isEqual(yesterday);
  }

  /**
   * XP reward method given when it is the user's birthday
   *
   * @param userId the user
   */
  public void orbitXPReward(Long userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new RuntimeException("User does not exist."));

    LocalDate userBirthday = user.getBirthday();

    if (userBirthday == null) {
      throw new IllegalArgumentException("Birthday has not been provided.");
    }

    boolean leapYearHuman =
        (userBirthday.getDayOfMonth() == 29) && (userBirthday.getMonth() == Month.FEBRUARY);

    boolean leapYearBirthDay =
        (today().getDayOfMonth() == 1) && (today().getMonth() == Month.MARCH);

    if (user.getLastBirthdayReward() != null
        && user.getLastBirthdayReward().getYear() == today().getYear()) {
      return;
    }

    if (birthdayToday(userBirthday)) {
      log.info("Happy Birthday! You've just completed an Orbit, so here's 250XP as a gift :)");
      user.setXp(user.getXp() + ORBIT_BIRTHDAY_XP);
    } else if (leapYearHuman && leapYearBirthDay) {
      log.info("You're a special human! Here's 250XP as a userBirthday gift :)");
      user.setXp(user.getXp() + ORBIT_BIRTHDAY_XP);
    }
    user.setLastBirthdayReward(today());
    checkAndUpdateUserLevel(user);
  }

  /**
   * Private method that determines if today is the user's birthday
   *
   * @param birthday the user's birthday
   * @return true if it's the user's birthday 2day!
   */
  private boolean birthdayToday(LocalDate birthday) {
    return (birthday != null)
        && (birthday.getMonth() == today().getMonth())
        && (birthday.getDayOfMonth() == today().getDayOfMonth());
  }

  /**
   * Method to return current date
   *
   * @return today's date
   */
  private LocalDate today() {
    return LocalDate.now();
  }

  /**
   * Method to check that a user needs to level up or down right after their XP has changed
   *
   * @param user the user
   */
  private void checkAndUpdateUserLevel(User user) {

    // check if user needs to level up
    while (user.getXp() >= xpLevelUp(user.getLevel())) {
      user.setLevel(user.getLevel() + 1);
    }

    // check if user needs to level down
    while (user.getXp() < xpLevelDown(user.getLevel())) {
      user.setLevel(Math.max(0, user.getLevel() - 1));
      if (user.getLevel() == 0) {
        break;
      }
    }

    userRepository.save(user);
  }

  /**
   * Private method for calculating level up threshold
   *
   * @param userLevel the user's current level
   * @return threshold required for next level
   */
  private int xpLevelUp(int userLevel) {
    return (int) (100 * (Math.pow(2, userLevel)));
  }

  /**
   * Private method for calculating level down threshold
   *
   * @param userLevel the user's current level
   * @return threshold required for previous level
   */
  private int xpLevelDown(int userLevel) {
    return (int) Math.max(100, 100 * (Math.pow(2, userLevel - 1)));
  }
}
