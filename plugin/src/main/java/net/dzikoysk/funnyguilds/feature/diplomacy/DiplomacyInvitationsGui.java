package net.dzikoysk.funnyguilds.feature.diplomacy;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.message.MessageService;
import net.dzikoysk.funnyguilds.config.sections.DiplomacyConfiguration;
import net.dzikoysk.funnyguilds.feature.eventlog.EventLogManager;
import net.dzikoysk.funnyguilds.feature.eventlog.EventLogType;
import net.dzikoysk.funnyguilds.feature.gui.GuiWindow;
import net.dzikoysk.funnyguilds.feature.invitation.ally.AllyInvitation;
import net.dzikoysk.funnyguilds.feature.invitation.ally.AllyInvitationList;
import net.dzikoysk.funnyguilds.feature.scoreboard.ScoreboardGlobalUpdateUserSyncTask;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.ItemBuilder;
import net.dzikoysk.funnyguilds.shared.formatter.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

/**
 * GUI for viewing and managing ally invitations.
 */
public class DiplomacyInvitationsGui {

    private static final int ROWS = 6;

    private final FunnyGuilds plugin;
    private final PluginConfiguration config;
    private final MessageService messageService;
    private final EventLogManager eventLogManager;
    private final AllyInvitationList allyInvitationList;
    private final Guild guild;
    private final User user;
    private final Player player;

    public DiplomacyInvitationsGui(FunnyGuilds plugin, PluginConfiguration config, MessageService messageService,
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
        DiplomacyConfiguration.InvitationsListConfig invitationsConfig = diplomacyConfig.invitationsList;

        String title = new FunnyFormatter()
                .register("{TAG}", this.guild.getTag())
                .register("{GUILD}", this.guild.getName())
                .replace(invitationsConfig.title.getValue());
        title = ChatUtils.colored(title);

        GuiWindow gui = new GuiWindow(title, ROWS);

        // Display invitations
        Set<AllyInvitation> invitations = this.allyInvitationList.getInvitationsFor(this.guild);
        int slot = 0;
        for (AllyInvitation invitation : invitations) {
            if (slot >= 45) break; // Leave space for navigation

            Guild inviter = invitation.getFrom();

            FunnyFormatter formatter = new FunnyFormatter()
                    .register("{INVITER-TAG}", inviter.getTag())
                    .register("{INVITER-NAME}", inviter.getName())
                    .register("{INVITER-MEMBERS}", inviter.getMembers().size());

            List<String> lore = new ArrayList<>();
            for (var line : invitationsConfig.invitationItemLore) {
                lore.add(formatter.replace(line.getValue()));
            }

            ItemStack item = new ItemBuilder(invitationsConfig.invitationMaterial)
                    .setName(formatter.replace(invitationsConfig.invitationItemName.getValue()), true)
                    .setLore(lore, true)
                    .getItem();

            gui.setItem(slot, item, event -> {
                event.setCancelled(true);

                // Check if user is leader or deputy
                if (!this.guild.isOwner(this.user) && !this.guild.isDeputy(this.user)) {
                    this.messageService.getMessage(cfg -> cfg.diplomacyNotLeaderOrDeputy)
                            .receiver(this.player)
                            .send();
                    return;
                }

                if (event.getClick() == ClickType.LEFT) {
                    // Accept invitation
                    this.allyInvitationList.expireInvitation(inviter, this.guild);

                    this.guild.addAlly(inviter);
                    inviter.addAlly(this.guild);

                    this.eventLogManager.logEvent(this.guild, EventLogType.ALLY_REQUEST_ACCEPTED,
                            this.user, inviter.getTag(), null);

                    this.messageService.getMessage(cfg -> cfg.diplomacyInvitationAccepted)
                            .receiver(this.player)
                            .with("{TAG}", inviter.getTag())
                            .send();

                    // Update scoreboards
                    this.plugin.getIndividualNameTagManager().peek(manager -> {
                        this.guild.getMembers().forEach(member -> 
                                this.plugin.scheduleFunnyTasks(new ScoreboardGlobalUpdateUserSyncTask(manager, member)));
                        inviter.getMembers().forEach(member -> 
                                this.plugin.scheduleFunnyTasks(new ScoreboardGlobalUpdateUserSyncTask(manager, member)));
                    });

                    // Refresh GUI
                    this.open();
                } else if (event.getClick() == ClickType.RIGHT) {
                    // Reject invitation
                    this.allyInvitationList.expireInvitation(inviter, this.guild);

                    this.eventLogManager.logEvent(this.guild, EventLogType.ALLY_REQUEST_REJECTED,
                            this.user, inviter.getTag(), null);

                    this.messageService.getMessage(cfg -> cfg.diplomacyInvitationRejected)
                            .receiver(this.player)
                            .with("{TAG}", inviter.getTag())
                            .send();

                    // Refresh GUI
                    this.open();
                }
            });

            slot++;
        }

        // Back button
        ItemStack backItem = new ItemBuilder(diplomacyConfig.backMaterial)
                .setName(diplomacyConfig.backName.getValue(), true)
                .getItem();

        gui.setItem(diplomacyConfig.backSlot, backItem, event -> {
            event.setCancelled(true);
            new DiplomacyMainGui(this.plugin, this.config, this.messageService, this.eventLogManager,
                    this.allyInvitationList, this.guild, this.user, this.player).open();
        });

        gui.open(this.player);
    }
}
