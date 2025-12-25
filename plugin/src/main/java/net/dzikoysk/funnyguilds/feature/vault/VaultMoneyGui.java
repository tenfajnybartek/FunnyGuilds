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
import net.dzikoysk.funnyguilds.feature.hooks.HookManager;
import net.dzikoysk.funnyguilds.feature.hooks.vault.VaultHook;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.ItemBuilder;
import net.dzikoysk.funnyguilds.shared.formatter.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * GUI for managing money in the vault.
 */
public class VaultMoneyGui {

    private static final int ROWS = 5;

    private final FunnyGuilds plugin;
    private final PluginConfiguration config;
    private final MessageService messageService;
    private final GuildVaultManager vaultManager;
    private final EventLogManager eventLogManager;
    private final Guild guild;
    private final User user;
    private final Player player;

    public VaultMoneyGui(FunnyGuilds plugin, PluginConfiguration config, MessageService messageService,
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
        GuildVaultConfiguration.MoneyVaultConfig moneyConfig = vaultConfig.moneyVault;
        GuildVault vault = this.vaultManager.getVault(this.guild);

        String title = new FunnyFormatter()
                .register("{TAG}", this.guild.getTag())
                .register("{GUILD}", this.guild.getName())
                .register("{BALANCE}", String.format("%.2f", vault.getBalance()))
                .replace(moneyConfig.title.getValue());
        title = ChatUtils.colored(title);

        GuiWindow gui = new GuiWindow(title, ROWS);

        // Fill empty slots
        if (this.config.guildPanel.fillItem.enabled) {
            ItemStack fillItem = new ItemBuilder(this.config.guildPanel.fillItem.material)
                    .setName(this.config.guildPanel.fillItem.name.getValue(), true)
                    .getItem();
            gui.fillEmpty(fillItem);
        }

        // Info item
        FunnyFormatter infoFormatter = new FunnyFormatter()
                .register("{BALANCE}", String.format("%.2f", vault.getBalance()));

        List<String> infoLore = new ArrayList<>();
        for (var line : moneyConfig.infoLore) {
            infoLore.add(infoFormatter.replace(line.getValue()));
        }

        ItemStack infoItem = new ItemBuilder(moneyConfig.infoMaterial)
                .setName(infoFormatter.replace(moneyConfig.infoName.getValue()), true)
                .setLore(infoLore, true)
                .getItem();

        gui.setItem(moneyConfig.infoSlot, infoItem);

        // Deposit buttons
        List<Double> amounts = moneyConfig.amounts;
        for (int i = 0; i < amounts.size() && i < 5; i++) {
            double amount = amounts.get(i);
            int slot = moneyConfig.depositStartSlot + i;

            FunnyFormatter depositFormatter = new FunnyFormatter()
                    .register("{AMOUNT}", String.format("%.0f", amount));

            ItemStack depositItem = new ItemBuilder(moneyConfig.depositMaterial)
                    .setName(depositFormatter.replace(moneyConfig.depositName.getValue()), true)
                    .getItem();

            gui.setItem(slot, depositItem, event -> {
                event.setCancelled(true);
                handleDeposit(amount, vault);
            });
        }

        // Withdraw buttons
        for (int i = 0; i < amounts.size() && i < 5; i++) {
            double amount = amounts.get(i);
            int slot = moneyConfig.withdrawStartSlot + i;

            FunnyFormatter withdrawFormatter = new FunnyFormatter()
                    .register("{AMOUNT}", String.format("%.0f", amount));

            ItemStack withdrawItem = new ItemBuilder(moneyConfig.withdrawMaterial)
                    .setName(withdrawFormatter.replace(moneyConfig.withdrawName.getValue()), true)
                    .getItem();

            gui.setItem(slot, withdrawItem, event -> {
                event.setCancelled(true);
                handleWithdraw(amount, vault);
            });
        }

        // Back button
        ItemStack backItem = new ItemBuilder(moneyConfig.backMaterial)
                .setName(moneyConfig.backName.getValue(), true)
                .getItem();

        gui.setItem(moneyConfig.backSlot, backItem, event -> {
            event.setCancelled(true);
            new VaultMainGui(this.plugin, this.config, this.messageService, this.vaultManager,
                    this.eventLogManager, this.guild, this.user, this.player).open();
        });

        gui.open(this.player);
    }

    private void handleDeposit(double amount, GuildVault vault) {
        // Check permission
        if (!canDeposit()) {
            this.messageService.getMessage(cfg -> cfg.vaultNoPermissionDeposit)
                    .receiver(this.player)
                    .send();
            return;
        }

        // Check if Vault economy is available
        if (!HookManager.VAULT.isPresent() || !VaultHook.isEconomyHooked()) {
            this.messageService.getMessage(cfg -> cfg.panelVaultNotAvailable)
                    .receiver(this.player)
                    .send();
            return;
        }

        // Check if player has enough money
        if (!VaultHook.canAfford(this.player, amount)) {
            this.messageService.getMessage(cfg -> cfg.vaultNotEnoughMoney)
                    .receiver(this.player)
                    .send();
            return;
        }

        // Withdraw from player and deposit to vault
        VaultHook.withdrawFromPlayerBank(this.player, amount);
        vault.deposit(amount);
        
        // Mark guild as changed for persistence
        this.guild.markChanged();

        // Log event
        this.eventLogManager.logEvent(this.guild, EventLogType.VAULT_DEPOSIT_MONEY,
                this.user, null, String.format("%.2f$", amount));

        this.messageService.getMessage(cfg -> cfg.vaultDepositMoney)
                .receiver(this.player)
                .with("{AMOUNT}", String.format("%.2f", amount))
                .send();

        // Refresh GUI
        this.open();
    }

    private void handleWithdraw(double amount, GuildVault vault) {
        // Check permission
        if (!canWithdraw()) {
            this.messageService.getMessage(cfg -> cfg.vaultNoPermissionWithdraw)
                    .receiver(this.player)
                    .send();
            return;
        }

        // Check if Vault economy is available
        if (!HookManager.VAULT.isPresent() || !VaultHook.isEconomyHooked()) {
            this.messageService.getMessage(cfg -> cfg.panelVaultNotAvailable)
                    .receiver(this.player)
                    .send();
            return;
        }

        // Check if vault has enough money
        if (!vault.canAfford(amount)) {
            this.messageService.getMessage(cfg -> cfg.vaultNotEnoughInVault)
                    .receiver(this.player)
                    .send();
            return;
        }

        // Withdraw from vault and deposit to player
        vault.withdraw(amount);
        VaultHook.depositToPlayerBank(this.player, amount);
        
        // Mark guild as changed for persistence
        this.guild.markChanged();

        // Log event
        this.eventLogManager.logEvent(this.guild, EventLogType.VAULT_WITHDRAW_MONEY,
                this.user, null, String.format("%.2f$", amount));

        this.messageService.getMessage(cfg -> cfg.vaultWithdrawMoney)
                .receiver(this.player)
                .with("{AMOUNT}", String.format("%.2f", amount))
                .send();

        // Refresh GUI
        this.open();
    }

    private boolean canDeposit() {
        GuildVaultConfiguration.VaultPermissions perms = this.config.guildVault.permissions;

        if (this.guild.isOwner(this.user)) {
            return true;
        }

        if (this.guild.isDeputy(this.user) && perms.deputyFullAccess) {
            return true;
        }

        return perms.memberCanDepositMoney;
    }

    private boolean canWithdraw() {
        GuildVaultConfiguration.VaultPermissions perms = this.config.guildVault.permissions;

        if (this.guild.isOwner(this.user)) {
            return true;
        }

        if (this.guild.isDeputy(this.user) && perms.deputyFullAccess) {
            return true;
        }

        return perms.memberCanWithdrawMoney;
    }
}
