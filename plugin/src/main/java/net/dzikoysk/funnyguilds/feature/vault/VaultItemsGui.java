package net.dzikoysk.funnyguilds.feature.vault;

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
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
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

        int configMaxPages = vaultConfig.maxItemPages;
        int currentPage = Math.min(this.page, configMaxPages - 1);

        String title = new FunnyFormatter()
                .register("{TAG}", this.guild.getTag())
                .register("{GUILD}", this.guild.getName())
                .register("{PAGE}", currentPage + 1)
                .register("{MAX-PAGES}", configMaxPages)
                .replace(itemsConfig.title.getValue());
        title = ChatUtils.colored(title);

        GuiWindow gui = new GuiWindow(title, ROWS);

        // Set shift-click handler for depositing items from player inventory
        gui.setShiftClickHandler(event -> {
            handleShiftClickDeposit(event, currentPage);
        });

        // Display items from vault
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
                Map<Integer, ItemStack> notAdded = this.player.getInventory().addItem(item.clone());
                if (!notAdded.isEmpty()) {
                    this.messageService.getMessage(cfg -> cfg.vaultInventoryFull)
                            .receiver(this.player)
                            .send();
                    return;
                }

                // Remove from vault
                vault.removeItem(itemIndex);
                
                // Mark guild as changed for persistence
                this.guild.markChanged();
                
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

        // Add click handlers for empty slots (for depositing items)
        // Calculate the number of items displayed on this page
        int itemsOnPage = Math.max(0, endIndex - startIndex);
        for (int slot = itemsOnPage; slot < ITEMS_PER_PAGE; slot++) {
            gui.setItem(slot, null, event -> {
                event.setCancelled(true);
                // Try multiple sources for the cursor item (Paper/Spigot compatibility)
                ItemStack cursor = event.getCursor();
                if (cursor == null || cursor.getType().isAir()) {
                    cursor = event.getView().getCursor();
                }
                if (cursor != null && !cursor.getType().isAir()) {
                    handleDeposit(cursor, currentPage, event);
                }
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

        // Next page button - show if there are more pages available (even if empty)
        if (currentPage < configMaxPages - 1) {
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

    private void handleDeposit(ItemStack cursorItem, int currentPage, InventoryClickEvent event) {
        if (cursorItem == null || cursorItem.getType() == Material.AIR) {
            return;
        }

        // Check permission to deposit
        if (!canDeposit()) {
            this.messageService.getMessage(cfg -> cfg.vaultNoPermissionDeposit)
                    .receiver(this.player)
                    .send();
            return;
        }

        GuildVault vault = this.vaultManager.getVault(this.guild);
        
        // Check vault capacity
        int maxItems = this.config.guildVault.maxItemPages * ITEMS_PER_PAGE;
        if (vault.getItemCount() >= maxItems) {
            this.messageService.getMessage(cfg -> cfg.vaultFull)
                    .receiver(this.player)
                    .send();
            return;
        }

        // Clone the item before any changes
        ItemStack itemToDeposit = cursorItem.clone();
        
        // Add item to vault
        vault.addItem(itemToDeposit);
        
        // Mark guild as changed for persistence
        this.guild.markChanged();
        
        // Log event
        this.eventLogManager.logEvent(this.guild, EventLogType.VAULT_DEPOSIT_ITEM,
                this.user, itemToDeposit.getType().name(), 
                itemToDeposit.getAmount() + "x " + itemToDeposit.getType().name());

        this.messageService.getMessage(cfg -> cfg.vaultDepositItem)
                .receiver(this.player)
                .send();

        // Clear cursor directly through event to avoid sync issues
        event.getView().setCursor(null);
        
        // Schedule GUI refresh on next tick
        this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
            new VaultItemsGui(this.plugin, this.config, this.messageService, this.vaultManager,
                    this.eventLogManager, this.guild, this.user, this.player, currentPage).open();
        });
    }

    private void handleShiftClickDeposit(InventoryClickEvent event, int currentPage) {
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        // Cancel the default shift-click behavior
        event.setCancelled(true);

        // Check permission to deposit
        if (!canDeposit()) {
            this.messageService.getMessage(cfg -> cfg.vaultNoPermissionDeposit)
                    .receiver(this.player)
                    .send();
            return;
        }

        GuildVault vault = this.vaultManager.getVault(this.guild);
        
        // Check vault capacity
        int maxItems = this.config.guildVault.maxItemPages * ITEMS_PER_PAGE;
        if (vault.getItemCount() >= maxItems) {
            this.messageService.getMessage(cfg -> cfg.vaultFull)
                    .receiver(this.player)
                    .send();
            return;
        }

        // Clone the item before any changes
        ItemStack itemToDeposit = clickedItem.clone();
        
        // Add item to vault
        vault.addItem(itemToDeposit);
        
        // Remove item from player's inventory
        event.setCurrentItem(null);
        
        // Mark guild as changed for persistence
        this.guild.markChanged();
        
        // Log event
        this.eventLogManager.logEvent(this.guild, EventLogType.VAULT_DEPOSIT_ITEM,
                this.user, itemToDeposit.getType().name(), 
                itemToDeposit.getAmount() + "x " + itemToDeposit.getType().name());

        this.messageService.getMessage(cfg -> cfg.vaultDepositItem)
                .receiver(this.player)
                .send();

        // Schedule GUI refresh on next tick
        this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
            new VaultItemsGui(this.plugin, this.config, this.messageService, this.vaultManager,
                    this.eventLogManager, this.guild, this.user, this.player, currentPage).open();
        });
    }

    private boolean canDeposit() {
        GuildVaultConfiguration.VaultPermissions perms = this.config.guildVault.permissions;
        
        if (this.guild.isOwner(this.user)) {
            return true;
        }
        
        if (this.guild.isDeputy(this.user) && perms.deputyFullAccess) {
            return true;
        }
        
        return perms.memberCanDepositItems;
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
