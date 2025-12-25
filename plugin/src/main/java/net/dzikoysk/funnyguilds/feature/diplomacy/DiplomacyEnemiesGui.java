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
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.ItemBuilder;
import net.dzikoysk.funnyguilds.shared.formatter.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * GUI for viewing and managing enemies.
 */
public class DiplomacyEnemiesGui {

    private static final int ROWS = 6;

    private final FunnyGuilds plugin;
    private final PluginConfiguration config;
    private final MessageService messageService;
    private final EventLogManager eventLogManager;
    private final Guild guild;
    private final User user;
    private final Player player;

    public DiplomacyEnemiesGui(FunnyGuilds plugin, PluginConfiguration config, MessageService messageService,
                              EventLogManager eventLogManager, Guild guild, User user, Player player) {
        this.plugin = plugin;
        this.config = config;
        this.messageService = messageService;
        this.eventLogManager = eventLogManager;
        this.guild = guild;
        this.user = user;
        this.player = player;
    }

    public void open() {
        DiplomacyConfiguration diplomacyConfig = this.config.diplomacy;
        DiplomacyConfiguration.EnemiesListConfig enemiesConfig = diplomacyConfig.enemiesList;

        String title = new FunnyFormatter()
                .register("{TAG}", this.guild.getTag())
                .register("{GUILD}", this.guild.getName())
                .replace(enemiesConfig.title.getValue());
        title = ChatUtils.colored(title);

        GuiWindow gui = new GuiWindow(title, ROWS);

        // Display enemies
        Set<Guild> enemies = this.guild.getEnemies();
        int slot = 0;
        for (Guild enemy : enemies) {
            if (slot >= 45) break; // Leave space for navigation

            FunnyFormatter formatter = new FunnyFormatter()
                    .register("{ENEMY-TAG}", enemy.getTag())
                    .register("{ENEMY-NAME}", enemy.getName())
                    .register("{ENEMY-MEMBERS}", enemy.getMembers().size())
                    .register("{ENEMY-LIVES}", enemy.getLives());

            List<String> lore = new ArrayList<>();
            for (var line : enemiesConfig.enemyItemLore) {
                lore.add(formatter.replace(line.getValue()));
            }

            ItemStack item = new ItemBuilder(enemiesConfig.enemyMaterial)
                    .setName(formatter.replace(enemiesConfig.enemyItemName.getValue()), true)
                    .setLore(lore, true)
                    .getItem();

            Guild targetEnemy = enemy;
            gui.setItem(slot, item, event -> {
                event.setCancelled(true);

                // Check if user is leader or deputy
                if (!this.guild.isOwner(this.user) && !this.guild.isDeputy(this.user)) {
                    this.messageService.getMessage(cfg -> cfg.diplomacyNotLeaderOrDeputy)
                            .receiver(this.player)
                            .send();
                    return;
                }

                // End war
                this.guild.removeEnemy(targetEnemy);
                
                this.eventLogManager.logEvent(this.guild, EventLogType.WAR_ENDED,
                        this.user, targetEnemy.getTag(), null);

                this.messageService.getMessage(cfg -> cfg.diplomacyWarEnded)
                        .receiver(this.player)
                        .with("{TAG}", targetEnemy.getTag())
                        .send();

                // Refresh GUI
                this.open();
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
                    this.plugin.getAllyInvitationList(), this.guild, this.user, this.player).open();
        });

        gui.open(this.player);
    }
}
