package net.dzikoysk.funnyguilds.feature.combatlog;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.message.MessageService;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.dzikoysk.funnyguilds.shared.formatter.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import panda.std.Option;

/**
 * Handles combat notifications for players
 */
public class CombatNotificationHandler {

    private final FunnyGuilds plugin;
    private final PluginConfiguration config;
    private final MessageService messageService;
    private final CombatManager combatManager;
    private final Map<UUID, BossBar> bossBars;
    private BukkitTask notificationTask;

    public CombatNotificationHandler(FunnyGuilds plugin, CombatManager combatManager) {
        this.plugin = plugin;
        this.config = plugin.getPluginConfiguration();
        this.messageService = plugin.getMessageService();
        this.combatManager = combatManager;
        this.bossBars = new HashMap<>();
    }

    /**
     * Starts the notification task
     */
    public void start() {
        if (!this.config.combatLog.enabled) {
            return;
        }

        int interval = this.config.combatLog.notificationUpdateInterval;
        this.notificationTask = Bukkit.getScheduler().runTaskTimer(this.plugin, this::updateNotifications, interval, interval);
    }

    /**
     * Stops the notification task
     */
    public void stop() {
        if (this.notificationTask != null) {
            this.notificationTask.cancel();
            this.notificationTask = null;
        }

        // Remove all boss bars
        for (Map.Entry<UUID, BossBar> entry : this.bossBars.entrySet()) {
            UUID playerUUID = entry.getKey();
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) {
                this.plugin.getAdventure().player(player).hideBossBar(entry.getValue());
            }
        }
        this.bossBars.clear();
    }

    /**
     * Shows combat start notification to player
     *
     * @param player The player
     * @param attacker The attacker
     */
    public void showCombatStart(Player player, User attacker) {
        if (!this.config.combatLog.enabled) {
            return;
        }

        // Play start sound
        if (this.config.combatLog.sounds.enabled) {
            player.playSound(
                player.getLocation(),
                this.config.combatLog.sounds.startSound,
                this.config.combatLog.sounds.startVolume,
                this.config.combatLog.sounds.startPitch
            );
        }

        // Show initial notification
        this.updatePlayerNotification(player);
    }

    /**
     * Shows combat end notification to player
     *
     * @param player The player
     */
    public void showCombatEnd(Player player) {
        if (!this.config.combatLog.enabled) {
            return;
        }

        // Play end sound
        if (this.config.combatLog.sounds.enabled) {
            player.playSound(
                player.getLocation(),
                this.config.combatLog.sounds.endSound,
                this.config.combatLog.sounds.endVolume,
                this.config.combatLog.sounds.endPitch
            );
        }

        // Show end message
        FunnyFormatter formatter = new FunnyFormatter();
        
        for (CombatNotificationType type : this.config.combatLog.notificationTypes) {
            switch (type) {
                case ACTIONBAR:
                    this.messageService.getMessage(config -> config.combatLogEnd)
                        .with(formatter)
                        .receiver(player)
                        .sendActionBar();
                    break;
                case CHAT:
                    this.messageService.getMessage(config -> config.combatLogEnd)
                        .with(formatter)
                        .receiver(player)
                        .send();
                    break;
                case TITLE:
                    Component titleComponent = ChatUtils.deserializeAmpersand(this.messageService.get(config -> config.combatLogEnd));
                    this.plugin.getAdventure().player(player).showTitle(
                        net.kyori.adventure.title.Title.title(
                            Component.empty(),
                            titleComponent
                        )
                    );
                    break;
                case BOSSBAR:
                    // Remove boss bar
                    this.removeBossBar(player.getUniqueId());
                    break;
            }
        }
    }

    /**
     * Updates notifications for all players in combat
     */
    private void updateNotifications() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            CombatState state = this.combatManager.getCombatState(player.getUniqueId());
            
            // Check if combat just expired
            boolean wasInCombat = state.getCombatStart().isPresent();
            boolean isInCombat = state.isInCombat();
            
            if (wasInCombat && !isInCombat) {
                // Combat just ended
                this.showCombatEnd(player);
                this.combatManager.exitCombat(player.getUniqueId());
            } else if (isInCombat) {
                // Still in combat, update notification
                this.updatePlayerNotification(player);
            } else {
                // Not in combat, remove boss bar if present
                this.removeBossBar(player.getUniqueId());
            }
        }
    }

    /**
     * Updates notification for a specific player
     *
     * @param player The player
     */
    private void updatePlayerNotification(Player player) {
        CombatState state = this.combatManager.getCombatState(player.getUniqueId());
        
        if (!state.isInCombat()) {
            return;
        }

        Option<Duration> remainingOption = state.getRemainingTime();
        if (remainingOption.isEmpty()) {
            return;
        }

        Duration remaining = remainingOption.get();
        String timeString = this.formatTime(remaining);
        
        Option<User> attackerOption = state.getAttacker();
        String attackerName = attackerOption.map(User::getName).orElseGet("Unknown");

        FunnyFormatter formatter = new FunnyFormatter()
            .register("{TIME}", timeString)
            .register("{ATTACKER}", attackerName);

        for (CombatNotificationType type : this.config.combatLog.notificationTypes) {
            switch (type) {
                case ACTIONBAR:
                    this.messageService.getMessage(config -> config.combatLogActive)
                        .with(formatter)
                        .receiver(player)
                        .sendActionBar();
                    break;
                case BOSSBAR:
                    this.updateBossBar(player, remaining, state.getCombatDuration().orElseGet(() -> Duration.ofSeconds(30)), formatter);
                    break;
                case CHAT:
                    // Only send chat message once per combat, not every update
                    break;
                case TITLE:
                    // Title updates might be too intrusive
                    break;
            }
        }
    }

    /**
     * Updates or creates boss bar for player
     *
     * @param player The player
     * @param remaining Remaining combat time
     * @param total Total combat duration
     * @param formatter Formatter with replacements
     */
    private void updateBossBar(Player player, Duration remaining, Duration total, FunnyFormatter formatter) {
        BossBar bossBar = this.bossBars.get(player.getUniqueId());
        
        String message = this.messageService.get(config -> config.combatLogActive);
        Component titleComponent = formatter.replace(ChatUtils.deserializeAmpersand(message));
        
        float progress = (float) remaining.getSeconds() / (float) total.getSeconds();
        progress = Math.max(0.0f, Math.min(1.0f, progress));

        if (bossBar == null) {
            // Create new boss bar
            BarColor color = this.getBarColor(progress);
            bossBar = BossBar.bossBar(
                titleComponent,
                progress,
                this.convertBarColor(color),
                this.convertBarStyle(this.config.combatLog.bossBar.style)
            );
            this.bossBars.put(player.getUniqueId(), bossBar);
            this.plugin.getAdventure().player(player).showBossBar(bossBar);
        } else {
            // Update existing boss bar
            bossBar = bossBar.name(titleComponent);
            bossBar = bossBar.progress(progress);
            
            if (this.config.combatLog.bossBar.progressiveColor) {
                BarColor color = this.getBarColor(progress);
                bossBar = bossBar.color(this.convertBarColor(color));
            }
        }
    }

    /**
     * Removes boss bar for player
     *
     * @param playerUUID The player UUID
     */
    private void removeBossBar(UUID playerUUID) {
        BossBar bossBar = this.bossBars.remove(playerUUID);
        if (bossBar != null) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) {
                this.plugin.getAdventure().player(player).hideBossBar(bossBar);
            }
        }
    }

    /**
     * Gets bar color based on progress
     *
     * @param progress Progress (0.0-1.0)
     * @return Bar color
     */
    private BarColor getBarColor(float progress) {
        if (progress > 0.6f) {
            return this.config.combatLog.bossBar.colorHigh;
        } else if (progress > 0.3f) {
            return this.config.combatLog.bossBar.colorMedium;
        } else {
            return this.config.combatLog.bossBar.colorLow;
        }
    }

    /**
     * Converts Bukkit BarColor to Adventure BarColor
     *
     * @param bukkitColor Bukkit bar color
     * @return Adventure bar color
     */
    private BossBar.Color convertBarColor(BarColor bukkitColor) {
        switch (bukkitColor) {
            case PINK: return BossBar.Color.PINK;
            case BLUE: return BossBar.Color.BLUE;
            case RED: return BossBar.Color.RED;
            case GREEN: return BossBar.Color.GREEN;
            case YELLOW: return BossBar.Color.YELLOW;
            case PURPLE: return BossBar.Color.PURPLE;
            case WHITE: return BossBar.Color.WHITE;
            default: return BossBar.Color.RED;
        }
    }

    /**
     * Converts Bukkit BarStyle to Adventure BarOverlay
     *
     * @param bukkitStyle Bukkit bar style
     * @return Adventure bar overlay
     */
    private BossBar.Overlay convertBarStyle(org.bukkit.boss.BarStyle bukkitStyle) {
        switch (bukkitStyle) {
            case SOLID: return BossBar.Overlay.PROGRESS;
            case SEGMENTED_6: return BossBar.Overlay.NOTCHED_6;
            case SEGMENTED_10: return BossBar.Overlay.NOTCHED_10;
            case SEGMENTED_12: return BossBar.Overlay.NOTCHED_12;
            case SEGMENTED_20: return BossBar.Overlay.NOTCHED_20;
            default: return BossBar.Overlay.PROGRESS;
        }
    }

    /**
     * Formats duration to readable time string
     *
     * @param duration The duration
     * @return Formatted time string
     */
    private String formatTime(Duration duration) {
        long totalSeconds = duration.getSeconds();
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        
        if (minutes > 0) {
            return String.format("%d:%02d", minutes, seconds);
        } else {
            return String.format("%ds", seconds);
        }
    }

}
