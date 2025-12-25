package net.dzikoysk.funnyguilds.feature.eventlog;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.message.MessageService;
import net.dzikoysk.funnyguilds.config.sections.EventLogConfiguration;
import net.dzikoysk.funnyguilds.feature.gui.GuiWindow;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.ItemBuilder;
import net.dzikoysk.funnyguilds.shared.formatter.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * GUI for viewing guild event log.
 */
public class EventLogGui {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final FunnyGuilds plugin;
    private final PluginConfiguration config;
    private final MessageService messageService;
    private final EventLogManager eventLogManager;
    private final Guild guild;
    private final User user;
    private final Player player;
    private final int page;

    public EventLogGui(FunnyGuilds plugin, PluginConfiguration config, MessageService messageService,
                       EventLogManager eventLogManager, Guild guild, User user, Player player, int page) {
        this.plugin = plugin;
        this.config = config;
        this.messageService = messageService;
        this.eventLogManager = eventLogManager;
        this.guild = guild;
        this.user = user;
        this.player = player;
        this.page = page;
    }

    public void open() {
        EventLogConfiguration logConfig = this.config.eventLog;
        int entriesPerPage = logConfig.entriesPerPage;
        int totalEntries = this.eventLogManager.getLogCount(this.guild);
        int maxPages = Math.max(1, (int) Math.ceil((double) totalEntries / entriesPerPage));
        int currentPage = Math.min(this.page, maxPages - 1);

        String title = new FunnyFormatter()
                .register("{TAG}", this.guild.getTag())
                .register("{GUILD}", this.guild.getName())
                .register("{PAGE}", currentPage + 1)
                .register("{MAX-PAGES}", maxPages)
                .replace(logConfig.title.getValue());
        title = ChatUtils.colored(title);

        GuiWindow gui = new GuiWindow(title, logConfig.rows);

        // Get log entries for current page
        List<EventLogEntry> entries = this.eventLogManager.getLogs(this.guild, currentPage, entriesPerPage);

        // Display entries
        for (int i = 0; i < entries.size(); i++) {
            EventLogEntry entry = entries.get(i);
            Material material = getMaterialForEventType(entry.getType(), logConfig.eventMaterials);

            String date = DATE_FORMATTER.format(entry.getTimestamp().atZone(ZoneId.systemDefault()));
            String time = TIME_FORMATTER.format(entry.getTimestamp().atZone(ZoneId.systemDefault()));

            FunnyFormatter formatter = new FunnyFormatter()
                    .register("{TYPE}", entry.getType().getDisplayName())
                    .register("{ACTOR}", entry.getActor() != null ? entry.getActor() : "-")
                    .register("{TARGET}", entry.getTarget() != null ? entry.getTarget() : "-")
                    .register("{DETAILS}", entry.getDetails() != null ? entry.getDetails() : "-")
                    .register("{DATE}", date)
                    .register("{TIME}", time);

            List<String> lore = new ArrayList<>();
            for (var line : logConfig.entryLore) {
                lore.add(formatter.replace(line.getValue()));
            }

            ItemStack item = new ItemBuilder(material)
                    .setName(formatter.replace(logConfig.entryFormat.getValue()), true)
                    .setLore(lore, true)
                    .getItem();

            gui.setItem(i, item);
        }

        // Previous page button
        if (currentPage > 0) {
            ItemStack prevItem = new ItemBuilder(logConfig.prevPageMaterial)
                    .setName(logConfig.prevPageName.getValue(), true)
                    .getItem();

            gui.setItem(logConfig.prevPageSlot, prevItem, event -> {
                event.setCancelled(true);
                new EventLogGui(this.plugin, this.config, this.messageService, this.eventLogManager,
                        this.guild, this.user, this.player, currentPage - 1).open();
            });
        }

        // Next page button
        if (currentPage < maxPages - 1) {
            ItemStack nextItem = new ItemBuilder(logConfig.nextPageMaterial)
                    .setName(logConfig.nextPageName.getValue(), true)
                    .getItem();

            gui.setItem(logConfig.nextPageSlot, nextItem, event -> {
                event.setCancelled(true);
                new EventLogGui(this.plugin, this.config, this.messageService, this.eventLogManager,
                        this.guild, this.user, this.player, currentPage + 1).open();
            });
        }

        // Back button
        ItemStack backItem = new ItemBuilder(logConfig.backMaterial)
                .setName(logConfig.backName.getValue(), true)
                .getItem();

        gui.setItem(logConfig.backSlot, backItem, event -> {
            event.setCancelled(true);
            this.player.closeInventory();
            this.player.performCommand(this.config.commands.panel.name);
        });

        gui.open(this.player);
    }

    private Material getMaterialForEventType(EventLogType type, EventLogConfiguration.EventMaterials materials) {
        return switch (type) {
            case MEMBER_JOIN, MEMBER_LEAVE, MEMBER_KICK, MEMBER_INVITE -> materials.memberEvents;
            case DEPUTY_SET, DEPUTY_REMOVE, LEADER_CHANGE, PERMISSION_CHANGE -> materials.permissionEvents;
            case VAULT_DEPOSIT_MONEY, VAULT_WITHDRAW_MONEY, VAULT_DEPOSIT_ITEM, VAULT_WITHDRAW_ITEM -> materials.vaultEvents;
            case ALLY_REQUEST_SENT, ALLY_REQUEST_ACCEPTED, ALLY_REQUEST_REJECTED, ALLY_BROKEN,
                 WAR_DECLARED, WAR_ENDED, WAR_WON, WAR_LOST -> materials.diplomacyEvents;
            case GUILD_CREATED, GUILD_EXTENDED, GUILD_ENLARGED, PVP_TOGGLED, ALLY_PVP_TOGGLED, BASE_MOVED -> materials.managementEvents;
            default -> materials.defaultMaterial;
        };
    }
}
