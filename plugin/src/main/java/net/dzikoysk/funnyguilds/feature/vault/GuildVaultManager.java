package net.dzikoysk.funnyguilds.feature.vault;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.guild.Guild;

/**
 * Manages all guild vaults.
 */
public class GuildVaultManager {

    private final PluginConfiguration config;
    private final Map<UUID, GuildVault> vaults = new HashMap<>();

    public GuildVaultManager(PluginConfiguration config) {
        this.config = config;
    }

    /**
     * Gets or creates a vault for a guild.
     */
    public GuildVault getVault(Guild guild) {
        return this.vaults.computeIfAbsent(guild.getUUID(), GuildVault::new);
    }

    /**
     * Gets a vault for a guild by UUID, returns null if not exists.
     */
    public GuildVault getVaultOrNull(UUID guildUuid) {
        return this.vaults.get(guildUuid);
    }

    /**
     * Loads a vault from storage.
     */
    public void loadVault(GuildVault vault) {
        this.vaults.put(vault.getGuildUuid(), vault);
    }

    /**
     * Removes a vault (when guild is deleted).
     */
    public void removeVault(Guild guild) {
        this.vaults.remove(guild.getUUID());
    }

    /**
     * Gets all vaults for persistence.
     */
    public Map<UUID, GuildVault> getAllVaults() {
        return new HashMap<>(this.vaults);
    }

    /**
     * Checks if vault system is enabled.
     */
    public boolean isEnabled() {
        return this.config.guildVault.enabled;
    }

    /**
     * Gets maximum vault pages for items.
     */
    public int getMaxPages() {
        return this.config.guildVault.maxItemPages;
    }

    /**
     * Gets items per page.
     */
    public int getItemsPerPage() {
        return this.config.guildVault.itemsPerPage;
    }
}
