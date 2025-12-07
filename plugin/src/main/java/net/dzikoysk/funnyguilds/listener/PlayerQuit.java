package net.dzikoysk.funnyguilds.listener;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.dzikoysk.funnyguilds.damage.DamageManager;
import net.dzikoysk.funnyguilds.damage.DamageState;
import net.dzikoysk.funnyguilds.event.FunnyEvent;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.rank.LogoutsChangeEvent;
import net.dzikoysk.funnyguilds.event.rank.PointsChangeEvent;
import net.dzikoysk.funnyguilds.feature.combatlog.CombatManager;
import net.dzikoysk.funnyguilds.feature.combatlog.CombatState;
import net.dzikoysk.funnyguilds.feature.scoreboard.ScoreboardGlobalUpdateUserSyncTask;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserCache;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.panda_lang.utilities.inject.annotations.Inject;
import panda.std.Option;

public class PlayerQuit extends AbstractFunnyListener {

    // Track players who are dying due to combat logout
    private static final Set<UUID> COMBAT_LOGOUT_DEATHS = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @Inject
    private DamageManager damageManager;

    @Inject
    private CombatManager combatManager;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onKick(PlayerKickEvent event) {
        this.handleQuit(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        this.handleQuit(event.getPlayer());
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuitEarly(PlayerQuitEvent event) {
        this.handleCombatLogout(event.getPlayer());
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onKickEarly(PlayerKickEvent event) {
        this.handleCombatLogout(event.getPlayer());
    }
    
    private void handleCombatLogout(Player player) {
        if (!this.config.combatLog.enabled) {
            return;
        }
        
        this.userManager.findByUuid(player.getUniqueId()).peek(user -> {
            CombatState combatState = this.combatManager.getCombatState(user.getUUID());
            
            if (combatState.isInCombat()) {
                // Mark this death as a combat logout death
                COMBAT_LOGOUT_DEATHS.add(player.getUniqueId());
                
                // Kill the player before they disconnect
                // This will trigger PlayerDeathEvent which handles death messages, drops, etc.
                player.setHealth(0.0);
            }
        });
    }
    
    /**
     * Checks if a death was caused by combat logout
     * 
     * @param playerUUID The player UUID
     * @return true if the player is dying due to combat logout
     */
    public static boolean isCombatLogoutDeath(UUID playerUUID) {
        return COMBAT_LOGOUT_DEATHS.remove(playerUUID);
    }

    private void handleQuit(Player player) {
        this.userManager.findByUuid(player.getUniqueId()).peek(user -> {
            UserCache cache = user.getCache();
            DamageState damageState = damageManager.getDamageState(user.getUUID());

            // Check for combat log logout (for stats tracking only, death is handled in handleCombatLogout)
            CombatState combatState = this.combatManager.getCombatState(user.getUUID());
            boolean inCombat = combatState.isInCombat();

            if (inCombat) {
                // Apply logout penalty
                LogoutsChangeEvent logoutsChangeEvent = new LogoutsChangeEvent(FunnyEvent.EventCause.USER, user, user, 1);

                if (SimpleEventHandler.handle(logoutsChangeEvent)) {
                    user.getRank().updateLogouts(currentValue -> currentValue + logoutsChangeEvent.getLogoutsChange());
                }

                // Apply additional points penalty if configured
                if (this.config.combatLog.enabled && this.config.combatLog.logoutPenalty) {
                    int penalty = -this.config.combatLog.logoutPenaltyPoints;
                    PointsChangeEvent penaltyEvent = new PointsChangeEvent(FunnyEvent.EventCause.USER, user, user, penalty);
                    
                    if (SimpleEventHandler.handle(penaltyEvent)) {
                        user.getRank().updatePoints(currentValue -> Math.max(0, currentValue + penaltyEvent.getPointsChange()));
                    }
                }

                // Award points to killer if configured
                if (this.config.combatLog.enabled && this.config.combatLog.killerRewardOnLogout) {
                    Option<User> attackerOption = combatState.getAttacker();
                    if (attackerOption.isPresent()) {
                        User attacker = attackerOption.get();
                        int reward = this.config.combatLog.logoutPenaltyPoints;
                        PointsChangeEvent rewardEvent = new PointsChangeEvent(FunnyEvent.EventCause.COMBAT, user, attacker, reward);
                        
                        if (SimpleEventHandler.handle(rewardEvent)) {
                            attacker.getRank().updatePoints(currentValue -> currentValue + rewardEvent.getPointsChange());
                        }
                    }
                }
            }
            
            // Clear combat state
            this.combatManager.exitCombat(user.getUUID());

            this.plugin.getIndividualNameTagManager()
                    .map(manager -> new ScoreboardGlobalUpdateUserSyncTask(manager, user))
                    .peek(this.plugin::scheduleFunnyTasks);
            this.plugin.getDummyManager()
                    .map(manager -> new ScoreboardGlobalUpdateUserSyncTask(manager, user))
                    .peek(this.plugin::scheduleFunnyTasks);

            cache.setIndividualNameTag(null);
            cache.setScoreboard(null);
            cache.setDummy(null);
            cache.setPlayerList(null);
            damageState.clear();
        });

        this.messageService.playerQuit(player);
    }

}
