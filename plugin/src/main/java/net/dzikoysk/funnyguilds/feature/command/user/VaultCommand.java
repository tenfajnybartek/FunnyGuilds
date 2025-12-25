package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.GuildCommandPermission;
import net.dzikoysk.funnyguilds.feature.command.HasGuildPermission;
import net.dzikoysk.funnyguilds.feature.eventlog.EventLogManager;
import net.dzikoysk.funnyguilds.feature.vault.GuildVaultManager;
import net.dzikoysk.funnyguilds.feature.vault.VaultMainGui;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.entity.Player;
import org.panda_lang.utilities.inject.annotations.Inject;

@FunnyComponent
public class VaultCommand extends AbstractFunnyCommand {

    @Inject
    public GuildVaultManager guildVaultManager;
    
    @Inject
    public EventLogManager eventLogManager;

    @FunnyCommand(
            name = "${user.vault.name}",
            description = "${user.vault.description}",
            aliases = "${user.vault.aliases}",
            permission = "funnyguilds.vault",
            acceptsExceeded = true,
            playerOnly = true
    )
    public void execute(Player player, @HasGuildPermission(GuildCommandPermission.VAULT) User user, Guild guild) {
        if (!this.config.guildVault.enabled) {
            this.messageService.getMessage(config -> config.vaultDisabled)
                    .receiver(player)
                    .send();
            return;
        }

        new VaultMainGui(this.plugin, this.config, this.messageService, this.guildVaultManager,
                this.eventLogManager, guild, user, player).open();
    }
}
