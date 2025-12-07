package net.dzikoysk.funnyguilds.feature.combatlog;

import java.time.Duration;
import java.util.Arrays;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.message.MessageService;
import net.dzikoysk.funnyguilds.shared.formatter.FunnyFormatter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import panda.std.Option;

/**
 * Blocks commands during combat
 */
public class CombatCommandBlocker implements Listener {

    private final PluginConfiguration config;
    private final MessageService messageService;
    private final CombatManager combatManager;

    public CombatCommandBlocker(PluginConfiguration config, MessageService messageService, CombatManager combatManager) {
        this.config = config;
        this.messageService = messageService;
        this.combatManager = combatManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        if (!this.config.combatLog.enabled || !this.config.combatLog.commandBlock.enabled) {
            return;
        }

        Player player = event.getPlayer();
        CombatState state = this.combatManager.getCombatState(player.getUniqueId());

        if (!state.isInCombat()) {
            return;
        }

        // Check if player has bypass permission
        if (player.hasPermission("funnyguilds.combatlog.bypass")) {
            return;
        }

        String message = event.getMessage();
        String command = this.extractCommand(message);

        if (this.isCommandBlocked(command)) {
            event.setCancelled(true);

            Option<Duration> remainingOption = state.getRemainingTime();
            String timeString = remainingOption.map(this::formatTime).orElseGet(() -> "0s");

            FunnyFormatter formatter = new FunnyFormatter()
                .register("{COMMAND}", command)
                .register("{TIME}", timeString);

            this.messageService.getMessage(config -> config.combatLogCommandBlocked)
                .with(formatter)
                .receiver(player)
                .send();
        }
    }

    /**
     * Extracts command name from message
     *
     * @param message The command message
     * @return The command name
     */
    private String extractCommand(String message) {
        // Remove leading slash
        String withoutSlash = message.startsWith("/") ? message.substring(1) : message;

        if (this.config.combatLog.commandBlock.checkMainCommandOnly) {
            // Get only the first part (main command)
            return withoutSlash.split(" ")[0].toLowerCase();
        } else {
            // Return full command
            return withoutSlash.toLowerCase();
        }
    }

    /**
     * Checks if command is blocked
     *
     * @param command The command to check
     * @return true if blocked
     */
    private boolean isCommandBlocked(String command) {
        String lowerCommand = command.toLowerCase();
        
        for (String blockedCmd : this.config.combatLog.commandBlock.blockedCommands) {
            String lowerBlocked = blockedCmd.toLowerCase();
            
            if (this.config.combatLog.commandBlock.checkMainCommandOnly) {
                // Check if command starts with blocked command
                if (lowerCommand.equals(lowerBlocked) || lowerCommand.startsWith(lowerBlocked + " ")) {
                    return true;
                }
            } else {
                // Exact match
                if (lowerCommand.equals(lowerBlocked)) {
                    return true;
                }
            }
        }
        
        return false;
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
