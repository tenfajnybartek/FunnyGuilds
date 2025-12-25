package net.dzikoysk.funnyguilds.feature.diplomacy;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.message.MessageService;
import net.dzikoysk.funnyguilds.config.sections.DiplomacyConfiguration;
import net.dzikoysk.funnyguilds.feature.eventlog.EventLogManager;
import net.dzikoysk.funnyguilds.feature.gui.GuiWindow;
import net.dzikoysk.funnyguilds.feature.invitation.ally.AllyInvitation;
import net.dzikoysk.funnyguilds.feature.invitation.ally.AllyInvitationList;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.ItemBuilder;
import net.dzikoysk.funnyguilds.shared.formatter.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Main diplomacy GUI showing allies, enemies, and invitations.
 */
public class DiplomacyMainGui {

    private final FunnyGuilds plugin;
    private final PluginConfiguration config;
    private final MessageService messageService;
    private final EventLogManager eventLogManager;
    private final AllyInvitationList allyInvitationList;
    private final Guild guild;
    private final User user;
    private final Player player;

    public DiplomacyMainGui(FunnyGuilds plugin, PluginConfiguration config, MessageService messageService,
                           EventLogManager eventLogManager, AllyInvitationList allyInvitationList,
                           Guild guild, User user, Player player) {
        this.plugin = plugin;
        this.config = config;
        this.messageService = messageService;
        this.eventLogManager = eventLogManager;
        this.allyInvitationList = allyInvitationList;
        this.guild = guild;
        this.user = user;
        this.player = player;
    }

    public void open() {
        DiplomacyConfiguration diplomacyConfig = this.config.diplomacy;

        String title = new FunnyFormatter()
                .register("{TAG}", this.guild.getTag())
                .register("{GUILD}", this.guild.getName())
                .replace(diplomacyConfig.title.getValue());
        title = ChatUtils.colored(title);

        GuiWindow gui = new GuiWindow(title, diplomacyConfig.rows);

        // Fill empty slots
        if (this.config.guildPanel.fillItem.enabled) {
            ItemStack fillItem = new ItemBuilder(this.config.guildPanel.fillItem.material)
                    .setName(this.config.guildPanel.fillItem.name.getValue(), true)
                    .getItem();
            gui.fillEmpty(fillItem);
        }

        // Allies menu item
        DiplomacyConfiguration.AlliesMenuItem alliesMenuItem = diplomacyConfig.alliesMenuItem;
        FunnyFormatter alliesFormatter = new FunnyFormatter()
                .register("{ALLIES-COUNT}", this.guild.getAllies().size())
                .register("{MAX-ALLIES}", this.config.maxAlliesBetweenGuilds);

        List<String> alliesLore = new ArrayList<>();
        for (var line : alliesMenuItem.lore) {
            alliesLore.add(alliesFormatter.replace(line.getValue()));
        }

        ItemStack alliesItem = new ItemBuilder(alliesMenuItem.material)
                .setName(alliesMenuItem.name.getValue(), true)
                .setLore(alliesLore, true)
                .getItem();

        gui.setItem(alliesMenuItem.slot, alliesItem, event -> {
            event.setCancelled(true);
            new DiplomacyAlliesGui(this.plugin, this.config, this.messageService, this.eventLogManager,
                    this.allyInvitationList, this.guild, this.user, this.player).open();
        });

        // Enemies menu item
        DiplomacyConfiguration.EnemiesMenuItem enemiesMenuItem = diplomacyConfig.enemiesMenuItem;
        FunnyFormatter enemiesFormatter = new FunnyFormatter()
                .register("{ENEMIES-COUNT}", this.guild.getEnemies().size())
                .register("{MAX-ENEMIES}", this.config.maxEnemiesBetweenGuilds);

        List<String> enemiesLore = new ArrayList<>();
        for (var line : enemiesMenuItem.lore) {
            enemiesLore.add(enemiesFormatter.replace(line.getValue()));
        }

        ItemStack enemiesItem = new ItemBuilder(enemiesMenuItem.material)
                .setName(enemiesMenuItem.name.getValue(), true)
                .setLore(enemiesLore, true)
                .getItem();

        gui.setItem(enemiesMenuItem.slot, enemiesItem, event -> {
            event.setCancelled(true);
            new DiplomacyEnemiesGui(this.plugin, this.config, this.messageService, this.eventLogManager,
                    this.guild, this.user, this.player).open();
        });

        // Invitations menu item
        DiplomacyConfiguration.InvitationsMenuItem invitationsMenuItem = diplomacyConfig.invitationsMenuItem;
        Set<AllyInvitation> invitations = this.allyInvitationList.getInvitationsFor(this.guild);
        FunnyFormatter invitationsFormatter = new FunnyFormatter()
                .register("{INVITATIONS-COUNT}", invitations.size());

        List<String> invitationsLore = new ArrayList<>();
        for (var line : invitationsMenuItem.lore) {
            invitationsLore.add(invitationsFormatter.replace(line.getValue()));
        }

        ItemStack invitationsItem = new ItemBuilder(invitationsMenuItem.material)
                .setName(invitationsMenuItem.name.getValue(), true)
                .setLore(invitationsLore, true)
                .getItem();

        gui.setItem(invitationsMenuItem.slot, invitationsItem, event -> {
            event.setCancelled(true);
            new DiplomacyInvitationsGui(this.plugin, this.config, this.messageService, this.eventLogManager,
                    this.allyInvitationList, this.guild, this.user, this.player).open();
        });

        // Back button
        ItemStack backItem = new ItemBuilder(diplomacyConfig.backMaterial)
                .setName(diplomacyConfig.backName.getValue(), true)
                .getItem();

        gui.setItem(diplomacyConfig.backSlot, backItem, event -> {
            event.setCancelled(true);
            this.player.closeInventory();
            this.player.performCommand(this.config.commands.panel.name);
        });

        gui.open(this.player);
    }
}
