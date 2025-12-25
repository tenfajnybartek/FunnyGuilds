package net.dzikoysk.funnyguilds.feature.vault;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * GUI for managing items in the vault.
 */
public class VaultItemsGui {

    private static final int ITEMS_PER_PAGE = 45;
    private static final int ROWS = 6;

    private final FunnyGuilds plugin;
    private final PluginConfiguration config;
    private final MessageService messageService;
    private final GuildVaultManager vaultManager;
    private final EventLogManager eventLogManager;
    private final Guild guild;
    private final User user;
    private final Player player;
    private final int page;

    public VaultItemsGui(FunnyGuilds plugin, PluginConfiguration config, MessageService messageService,
                         GuildVaultManager vaultManager, EventLogManager eventLogManager,
                         Guild guild, User user, Player player, int page) {
        this.plugin = plugin;
        this.config = config;
        this.messageService = messageService;
        this.vaultManager = vaultManager;
        this.eventLogManager = eventLogManager;
        this.guild = guild;
        this.user = user;
        this.player = player;
        this.page = page;
    }

    public void open() {
        GuildVaultConfiguration vaultConfig = this.config.guildVault;
        GuildVaultConfiguration.ItemsVaultConfig itemsConfig = vaultConfig.itemsVault;
        GuildVault vault = this.vaultManager.getVault(this.guild);

        int maxPages = Math.max(1, (int) Math.ceil((double) vault.getItemCount() / ITEMS_PER_PAGE));
        int currentPage = Math.min(this.page, maxPages - 1);

        String title = new FunnyFormatter()
                .register("{TAG}", this.guild.getTag())
                .register("{GUILD}", this.guild.getName())
                .register("{PAGE}", currentPage + 1)
                .register("{MAX-PAGES}", maxPages)
                .replace(itemsConfig.title.getValue());
        title = ChatUtils.colored(title);

        GuiWindow gui = new GuiWindow(title, ROWS);

        // Display items
        List<ItemStack> items = vault.getItems();
        int startIndex = currentPage * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, items.size());

        for (int i = startIndex; i < endIndex; i++) {
            int slot = i - startIndex;
            ItemStack item = items.get(i);
            int itemIndex = i;

            gui.setItem(slot, item, event -> {
                event.setCancelled(true);
                
                // Check permission to withdraw
                if (!canWithdraw()) {
                    this.messageService.getMessage(cfg -> cfg.vaultNoPermissionWithdraw)
                            .receiver(this.player)
                            .send();
                    return;
                }

                // Try to give item to player
                HashMap<Integer, ItemStack> notAdded = this.player.getInventory().addItem(item.clone());
                if (!notAdded.isEmpty()) {
                    this.messageService.getMessage(cfg -> cfg.vaultInventoryFull)
                            .receiver(this.player)
                            .send();
                    return;
                }

                // Remove from vault
                vault.removeItem(itemIndex);
                
                // Log event
                this.eventLogManager.logEvent(this.guild, EventLogType.VAULT_WITHDRAW_ITEM,
                        this.user, item.getType().name(), 
                        item.getAmount() + "x " + item.getType().name());

                this.messageService.getMessage(cfg -> cfg.vaultWithdrawItem)
                        .receiver(this.player)
                        .send();

                // Refresh GUI
                new VaultItemsGui(this.plugin, this.config, this.messageService, this.vaultManager,
                        this.eventLogManager, this.guild, this.user, this.player, currentPage).open();
            });
        }

        // Previous page button
        if (currentPage > 0) {
            ItemStack prevItem = new ItemBuilder(itemsConfig.prevPageMaterial)
                    .setName(itemsConfig.prevPageName.getValue(), true)
                    .getItem();

            gui.setItem(itemsConfig.prevPageSlot, prevItem, event -> {
                event.setCancelled(true);
                new VaultItemsGui(this.plugin, this.config, this.messageService, this.vaultManager,
                        this.eventLogManager, this.guild, this.user, this.player, currentPage - 1).open();
            });
        }

        // Next page button
        if (currentPage < maxPages - 1) {
            ItemStack nextItem = new ItemBuilder(itemsConfig.nextPageMaterial)
                    .setName(itemsConfig.nextPageName.getValue(), true)
                    .getItem();

            gui.setItem(itemsConfig.nextPageSlot, nextItem, event -> {
                event.setCancelled(true);
                new VaultItemsGui(this.plugin, this.config, this.messageService, this.vaultManager,
                        this.eventLogManager, this.guild, this.user, this.player, currentPage + 1).open();
            });
        }

        // Back button
        ItemStack backItem = new ItemBuilder(itemsConfig.backMaterial)
                .setName(itemsConfig.backName.getValue(), true)
                .getItem();

        gui.setItem(itemsConfig.backSlot, backItem, event -> {
            event.setCancelled(true);
            new VaultMainGui(this.plugin, this.config, this.messageService, this.vaultManager,
                    this.eventLogManager, this.guild, this.user, this.player).open();
        });

        gui.open(this.player);
    }

    private boolean canWithdraw() {
        GuildVaultConfiguration.VaultPermissions perms = this.config.guildVault.permissions;
        
        if (this.guild.isOwner(this.user)) {
            return true;
        }
        
        if (this.guild.isDeputy(this.user) && perms.deputyFullAccess) {
            return true;
        }
        
        return perms.memberCanWithdrawItems;
    }
}
