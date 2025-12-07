package net.dzikoysk.funnyguilds.feature.command.user;

import java.time.Duration;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.feature.combatlog.CombatManager;
import net.dzikoysk.funnyguilds.feature.combatlog.CombatState;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.shared.formatter.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.entity.Player;
import org.panda_lang.utilities.inject.annotations.Inject;
import panda.std.Option;

@FunnyComponent
public final class CombatTimeCommand extends AbstractFunnyCommand {

    @Inject
    private CombatManager combatManager;

    @FunnyCommand(
            name = "${user.combattime.name}",
            description = "${user.combattime.description}",
            aliases = "${user.combattime.aliases}",
            permission = "funnyguilds.combattime",
            playerOnly = true
    )
    public void execute(Player player, User user) {
        CombatState state = this.combatManager.getCombatState(player.getUniqueId());

        if (!state.isInCombat()) {
            this.messageService.getMessage(config -> config.combatLogNotInCombat)
                .receiver(player)
                .send();
            return;
        }

        Option<Duration> remainingOption = state.getRemainingTime();
        if (remainingOption.isEmpty()) {
            this.messageService.getMessage(config -> config.combatLogNotInCombat)
                .receiver(player)
                .send();
            return;
        }

        Duration remaining = remainingOption.get();
        String timeString = this.formatTime(remaining);

        Option<User> attackerOption = state.getAttacker();
        String attackerName = attackerOption.map(User::getName).orElseGet(() -> "Unknown");

        FunnyFormatter formatter = new FunnyFormatter()
            .register("{TIME}", timeString)
            .register("{ATTACKER}", attackerName);

        this.messageService.getMessage(config -> config.combatLogTimeRemaining)
            .with(formatter)
            .receiver(player)
            .send();
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
