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
 * GUI for viewing and managing allies.
 */
public class DiplomacyAlliesGui {

    private static final int ROWS = 6;

    private final FunnyGuilds plugin;
    private final PluginConfiguration config;
    private final MessageService messageService;
    private final EventLogManager eventLogManager;
    private final AllyInvitationList allyInvitationList;
    private final Guild guild;
    private final User user;
    private final Player player;

    public DiplomacyAlliesGui(FunnyGuilds plugin, PluginConfiguration config, MessageService messageService,
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
        DiplomacyConfiguration.AlliesListConfig alliesConfig = diplomacyConfig.alliesList;

        String title = new FunnyFormatter()
                .register("{TAG}", this.guild.getTag())
                .register("{GUILD}", this.guild.getName())
                .replace(alliesConfig.title.getValue());
        title = ChatUtils.colored(title);

        GuiWindow gui = new GuiWindow(title, ROWS);

        // Display allies
        Set<Guild> allies = this.guild.getAllies();
        int slot = 0;
        for (Guild ally : allies) {
            if (slot >= 45) break; // Leave space for navigation

            // Use message service for status strings
            String pvpStatus = this.guild.hasAllyPvPEnabled(ally) ? "&cWŁĄCZONE" : "&aWYŁĄCZONE";

            FunnyFormatter formatter = new FunnyFormatter()
                    .register("{ALLY-TAG}", ally.getTag())
                    .register("{ALLY-NAME}", ally.getName())
                    .register("{ALLY-MEMBERS}", ally.getMembers().size())
                    .register("{PVP-STATUS}", pvpStatus);

            List<String> lore = new ArrayList<>();
            for (var line : alliesConfig.allyItemLore) {
                lore.add(formatter.replace(line.getValue()));
            }

            ItemStack item = new ItemBuilder(alliesConfig.allyMaterial)
                    .setName(formatter.replace(alliesConfig.allyItemName.getValue()), true)
                    .setLore(lore, true)
                    .getItem();

            Guild targetAlly = ally;
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
                    // Toggle ally PvP
                    if (!this.config.allyPvP) {
                        this.messageService.getMessage(cfg -> cfg.generalAllyPvpDisabled)
                                .receiver(this.player)
                                .send();
                        return;
                    }

                    boolean enabled = this.guild.toggleAllyPvP(targetAlly);
                    
                    this.eventLogManager.logEvent(this.guild, EventLogType.ALLY_PVP_TOGGLED,
                            this.user, targetAlly.getTag(), enabled ? "włączone" : "wyłączone");

                    if (enabled) {
                        this.messageService.getMessage(cfg -> cfg.diplomacyAllyPvpEnabled)
                                .receiver(this.player)
                                .with("{TAG}", targetAlly.getTag())
                                .send();
                    } else {
                        this.messageService.getMessage(cfg -> cfg.diplomacyAllyPvpDisabled)
                                .receiver(this.player)
                                .with("{TAG}", targetAlly.getTag())
                                .send();
                    }

                    // Refresh GUI
                    this.open();
                } else if (event.getClick() == ClickType.RIGHT) {
                    // Break alliance
                    this.guild.removeAlly(targetAlly);
                    targetAlly.removeAlly(this.guild);

                    this.eventLogManager.logEvent(this.guild, EventLogType.ALLY_BROKEN,
                            this.user, targetAlly.getTag(), null);

                    this.messageService.getMessage(cfg -> cfg.diplomacyAllyBroken)
                            .receiver(this.player)
                            .with("{TAG}", targetAlly.getTag())
                            .send();

                    // Update scoreboards
                    this.plugin.getIndividualNameTagManager().peek(manager -> {
                        this.guild.getMembers().forEach(member -> 
                                this.plugin.scheduleFunnyTasks(new ScoreboardGlobalUpdateUserSyncTask(manager, member)));
                        targetAlly.getMembers().forEach(member -> 
                                this.plugin.scheduleFunnyTasks(new ScoreboardGlobalUpdateUserSyncTask(manager, member)));
                    });

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
