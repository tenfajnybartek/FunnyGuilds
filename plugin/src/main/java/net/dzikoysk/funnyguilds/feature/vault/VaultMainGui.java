package net.dzikoysk.funnyguilds.feature.vault;

import java.util.ArrayList;
import java.util.List;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.message.MessageService;
import net.dzikoysk.funnyguilds.config.sections.GuildVaultConfiguration;
import net.dzikoysk.funnyguilds.feature.eventlog.EventLogManager;
import net.dzikoysk.funnyguilds.feature.eventlog.EventLogType;
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
 * Main vault GUI showing options for items and money.
 */
public class VaultMainGui {

    private final FunnyGuilds plugin;
    private final PluginConfiguration config;
    private final MessageService messageService;
    private final GuildVaultManager vaultManager;
    private final EventLogManager eventLogManager;
    private final Guild guild;
    private final User user;
    private final Player player;

    public VaultMainGui(FunnyGuilds plugin, PluginConfiguration config, MessageService messageService,
                        GuildVaultManager vaultManager, EventLogManager eventLogManager,
                        Guild guild, User user, Player player) {
        this.plugin = plugin;
        this.config = config;
        this.messageService = messageService;
        this.vaultManager = vaultManager;
        this.eventLogManager = eventLogManager;
        this.guild = guild;
        this.user = user;
        this.player = player;
    }

    public void open() {
        GuildVaultConfiguration vaultConfig = this.config.guildVault;
        GuildVault vault = this.vaultManager.getVault(this.guild);

        String title = new FunnyFormatter()
                .register("{TAG}", this.guild.getTag())
                .register("{GUILD}", this.guild.getName())
                .replace(vaultConfig.title.getValue());
        title = ChatUtils.colored(title);

        GuiWindow gui = new GuiWindow(title, vaultConfig.rows);

        // Fill empty slots
        if (this.config.guildPanel.fillItem.enabled) {
            ItemStack fillItem = new ItemBuilder(this.config.guildPanel.fillItem.material)
                    .setName(this.config.guildPanel.fillItem.name.getValue(), true)
                    .getItem();
            gui.fillEmpty(fillItem);
        }

        // Items menu item
        GuildVaultConfiguration.ItemsMenuItem itemsMenuItem = vaultConfig.itemsMenuItem;
        FunnyFormatter itemsFormatter = new FunnyFormatter()
                .register("{ITEM-COUNT}", vault.getItemCount());

        List<String> itemsLore = new ArrayList<>();
        for (var line : itemsMenuItem.lore) {
            itemsLore.add(itemsFormatter.replace(line.getValue()));
        }

        ItemStack itemsItem = new ItemBuilder(itemsMenuItem.material)
                .setName(itemsMenuItem.name.getValue(), true)
                .setLore(itemsLore, true)
                .getItem();

        gui.setItem(itemsMenuItem.slot, itemsItem, event -> {
            event.setCancelled(true);
            new VaultItemsGui(this.plugin, this.config, this.messageService, this.vaultManager, 
                    this.eventLogManager, this.guild, this.user, this.player, 0).open();
        });

        // Money menu item
        GuildVaultConfiguration.MoneyMenuItem moneyMenuItem = vaultConfig.moneyMenuItem;
        FunnyFormatter moneyFormatter = new FunnyFormatter()
                .register("{BALANCE}", String.format("%.2f", vault.getBalance()));

        List<String> moneyLore = new ArrayList<>();
        for (var line : moneyMenuItem.lore) {
            moneyLore.add(moneyFormatter.replace(line.getValue()));
        }

        ItemStack moneyItem = new ItemBuilder(moneyMenuItem.material)
                .setName(moneyMenuItem.name.getValue(), true)
                .setLore(moneyLore, true)
                .getItem();

        gui.setItem(moneyMenuItem.slot, moneyItem, event -> {
            event.setCancelled(true);
            new VaultMoneyGui(this.plugin, this.config, this.messageService, this.vaultManager,
                    this.eventLogManager, this.guild, this.user, this.player).open();
        });

        // Back button
        if (vaultConfig.backItem.enabled) {
            GuildVaultConfiguration.BackItem backItem = vaultConfig.backItem;
            List<String> backLore = new ArrayList<>();
            for (var line : backItem.lore) {
                backLore.add(line.getValue());
            }

            ItemStack backItemStack = new ItemBuilder(backItem.material)
                    .setName(backItem.name.getValue(), true)
                    .setLore(backLore, true)
                    .getItem();

            gui.setItem(backItem.slot, backItemStack, event -> {
                event.setCancelled(true);
                this.player.closeInventory();
                this.player.performCommand(this.config.commands.panel.name);
            });
        }

        gui.open(this.player);
    }
}
