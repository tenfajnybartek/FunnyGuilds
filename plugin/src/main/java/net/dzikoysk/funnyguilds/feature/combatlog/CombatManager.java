package net.dzikoysk.funnyguilds.feature.combatlog;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.dzikoysk.funnyguilds.user.User;

/**
 * Manager for combat states
 */
public class CombatManager {

    private final Map<UUID, CombatState> combatStates = new ConcurrentHashMap<>();

    /**
     * Gets or creates a combat state for a player
     *
     * @param playerUUID The player UUID
     * @return The combat state
     */
    public CombatState getCombatState(UUID playerUUID) {
        return this.combatStates.computeIfAbsent(playerUUID, CombatState::new);
    }

    /**
     * Enters combat for a player
     *
     * @param player The player user
     * @param attacker The attacking user
     * @param duration The combat duration
     */
    public void enterCombat(User player, User attacker, Duration duration) {
        CombatState state = this.getCombatState(player.getUUID());
        
        if (state.isInCombat()) {
            state.refreshCombat(attacker, duration);
        } else {
            state.enterCombat(attacker, duration);
        }
    }

    /**
     * Exits combat for a player
     *
     * @param playerUUID The player UUID
     */
    public void exitCombat(UUID playerUUID) {
        CombatState state = this.combatStates.get(playerUUID);
        if (state != null) {
            state.exitCombat();
        }
    }

    /**
     * Clears combat state for a player
     *
     * @param playerUUID The player UUID
     */
    public void clearCombatState(UUID playerUUID) {
        this.combatStates.remove(playerUUID);
    }

    /**
     * Checks if player is in combat
     *
     * @param playerUUID The player UUID
     * @return true if in combat
     */
    public boolean isInCombat(UUID playerUUID) {
        CombatState state = this.combatStates.get(playerUUID);
        return state != null && state.isInCombat();
    }

}
