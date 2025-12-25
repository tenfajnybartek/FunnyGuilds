package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.GuildCommandPermission;
import net.dzikoysk.funnyguilds.feature.command.HasGuildPermission;
import net.dzikoysk.funnyguilds.feature.eventlog.EventLogGui;
import net.dzikoysk.funnyguilds.feature.eventlog.EventLogManager;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.entity.Player;
import org.panda_lang.utilities.inject.annotations.Inject;

@FunnyComponent
public class EventLogCommand extends AbstractFunnyCommand {

    @Inject
    public EventLogManager eventLogManager;

    @FunnyCommand(
            name = "${user.eventlog.name}",
            description = "${user.eventlog.description}",
            aliases = "${user.eventlog.aliases}",
            permission = "funnyguilds.eventlog",
            acceptsExceeded = true,
            playerOnly = true
    )
    public void execute(Player player, @HasGuildPermission(GuildCommandPermission.EVENTLOG) User user, Guild guild) {
        if (!this.config.eventLog.enabled) {
            this.messageService.getMessage(config -> config.eventLogDisabled)
                    .receiver(player)
                    .send();
            return;
        }

        if (this.eventLogManager.getLogCount(guild) == 0) {
            this.messageService.getMessage(config -> config.eventLogEmpty)
                    .receiver(player)
                    .send();
            return;
        }

        new EventLogGui(this.plugin, this.config, this.messageService, this.eventLogManager,
                guild, user, player, 0).open();
    }
}
