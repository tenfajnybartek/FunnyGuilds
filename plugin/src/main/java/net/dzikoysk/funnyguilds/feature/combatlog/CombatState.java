package net.dzikoysk.funnyguilds.feature.combatlog;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import net.dzikoysk.funnyguilds.user.User;
import panda.std.Option;

/**
 * Represents the combat state of a player
 */
public class CombatState {

    private final UUID playerUUID;
    private Instant combatStart;
    private Instant combatEnd;
    private User attacker;
    private Duration combatDuration;

    public CombatState(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    /**
     * Enters combat with an attacker
     *
     * @param attacker The attacking user
     * @param duration The combat duration
     */
    public void enterCombat(User attacker, Duration duration) {
        this.combatStart = Instant.now();
        this.combatDuration = duration;
        this.combatEnd = this.combatStart.plus(duration);
        this.attacker = attacker;
    }

    /**
     * Updates combat timer (refreshes combat end time)
     *
     * @param attacker The attacking user
     * @param duration The combat duration
     */
    public void refreshCombat(User attacker, Duration duration) {
        Instant now = Instant.now();
        this.combatEnd = now.plus(duration);
        this.combatDuration = duration;
        
        // Update attacker only if different
        if (this.attacker == null || !this.attacker.equals(attacker)) {
            this.attacker = attacker;
        }
    }

    /**
     * Exits combat
     */
    public void exitCombat() {
        this.combatStart = null;
        this.combatEnd = null;
        this.attacker = null;
        this.combatDuration = null;
    }

    /**
     * Checks if player is in combat
     *
     * @return true if in combat
     */
    public boolean isInCombat() {
        if (this.combatEnd == null) {
            return false;
        }
        return Instant.now().isBefore(this.combatEnd);
    }

    /**
     * Gets remaining combat time
     *
     * @return Optional containing remaining duration if in combat
     */
    public Option<Duration> getRemainingTime() {
        if (!this.isInCombat()) {
            return Option.none();
        }
        Duration remaining = Duration.between(Instant.now(), this.combatEnd);
        return Option.of(remaining.isNegative() ? Duration.ZERO : remaining);
    }

    /**
     * Gets the attacker
     *
     * @return Optional containing the attacker user
     */
    public Option<User> getAttacker() {
        return Option.ofNullable(this.attacker);
    }

    /**
     * Gets the player UUID
     *
     * @return The player UUID
     */
    public UUID getPlayerUUID() {
        return this.playerUUID;
    }

    /**
     * Gets combat duration
     *
     * @return Optional containing combat duration if in combat
     */
    public Option<Duration> getCombatDuration() {
        return Option.ofNullable(this.combatDuration);
    }

    /**
     * Gets combat start time
     *
     * @return Optional containing combat start instant
     */
    public Option<Instant> getCombatStart() {
        return Option.ofNullable(this.combatStart);
    }

}
