package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.GuildCommandPermission;
import net.dzikoysk.funnyguilds.feature.command.HasGuildPermission;
import net.dzikoysk.funnyguilds.feature.diplomacy.DiplomacyMainGui;
import net.dzikoysk.funnyguilds.feature.eventlog.EventLogManager;
import net.dzikoysk.funnyguilds.feature.invitation.ally.AllyInvitationList;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.entity.Player;
import org.panda_lang.utilities.inject.annotations.Inject;

@FunnyComponent
public class DiplomacyCommand extends AbstractFunnyCommand {

    @Inject
    public EventLogManager eventLogManager;
    
    @Inject
    public AllyInvitationList allyInvitationList;

    @FunnyCommand(
            name = "${user.diplomacy.name}",
            description = "${user.diplomacy.description}",
            aliases = "${user.diplomacy.aliases}",
            permission = "funnyguilds.diplomacy",
            acceptsExceeded = true,
            playerOnly = true
    )
    public void execute(Player player, @HasGuildPermission(GuildCommandPermission.DIPLOMACY) User user, Guild guild) {
        if (!this.config.diplomacy.enabled) {
            this.messageService.getMessage(config -> config.diplomacyDisabled)
                    .receiver(player)
                    .send();
            return;
        }

        new DiplomacyMainGui(this.plugin, this.config, this.messageService, this.eventLogManager,
                this.allyInvitationList, guild, user, player).open();
    }
}
