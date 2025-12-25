package net.dzikoysk.funnyguilds.data.database.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.database.element.SQLBasicUtils;
import net.dzikoysk.funnyguilds.data.database.element.SQLNamedStatement;
import net.dzikoysk.funnyguilds.data.database.element.SQLTable;
import net.dzikoysk.funnyguilds.feature.vault.GuildVault;
import net.dzikoysk.funnyguilds.feature.vault.GuildVaultManager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

/**
 * Serializer for guild vaults to/from SQL database.
 */
public final class DatabaseVaultSerializer {

    private DatabaseVaultSerializer() {
    }

    /**
     * Load all vaults from the database.
     */
    public static void loadAllVaults(SQLTable table) {
        GuildVaultManager vaultManager = FunnyGuilds.getInstance().getGuildVaultManager();
        if (vaultManager == null || !vaultManager.isEnabled()) {
            return;
        }

        SQLBasicUtils.getSelectAll(table).executeQuery(result -> {
            int loaded = 0;
            while (result.next()) {
                try {
                    String guildUuidStr = result.getString("guild_uuid");
                    double balance = result.getDouble("balance");
                    String itemsData = result.getString("items");

                    UUID guildUuid = UUID.fromString(guildUuidStr);
                    List<ItemStack> items = deserializeItems(itemsData);

                    GuildVault vault = new GuildVault(guildUuid, balance, items);
                    vault.markClean();
                    vaultManager.loadVault(vault);
                    loaded++;
                } catch (Exception e) {
                    FunnyGuilds.getPluginLogger().error("Failed to deserialize vault", e);
                }
            }

            FunnyGuilds.getPluginLogger().debug("Loaded " + loaded + " guild vaults from database");
        });
    }

    /**
     * Save all dirty vaults to the database.
     */
    public static void saveAllVaults(SQLTable table, boolean ignoreNotChanged) {
        GuildVaultManager vaultManager = FunnyGuilds.getInstance().getGuildVaultManager();
        if (vaultManager == null || !vaultManager.isEnabled()) {
            return;
        }

        for (GuildVault vault : vaultManager.getAllVaults()) {
            if (ignoreNotChanged && !vault.isDirty()) {
                continue;
            }
            saveVault(table, vault);
        }
    }

    /**
     * Save a single vault to the database.
     */
    public static void saveVault(SQLTable table, GuildVault vault) {
        try {
            SQLNamedStatement statement = SQLBasicUtils.getInsert(table);
            statement.set("guild_uuid", vault.getGuildUuid().toString());
            statement.set("balance", vault.getBalance());
            statement.set("items", serializeItems(vault.getItems()));
            statement.executeUpdate();
            vault.markClean();
        } catch (Exception e) {
            FunnyGuilds.getPluginLogger().error("Failed to save vault for guild " + vault.getGuildUuid(), e);
        }
    }

    /**
     * Delete vault for a guild.
     */
    public static void deleteVault(SQLTable table, UUID guildUuid) {
        try {
            SQLNamedStatement statement = SQLBasicUtils.getDelete(table);
            statement.set("guild_uuid", guildUuid.toString());
            statement.executeUpdate();
        } catch (Exception e) {
            FunnyGuilds.getPluginLogger().error("Failed to delete vault for guild " + guildUuid, e);
        }
    }

    /**
     * Serialize items list to Base64-encoded string (items separated by ;).
     */
    private static String serializeItems(List<ItemStack> items) {
        if (items == null || items.isEmpty()) {
            return "";
        }

        List<String> serializedItems = new ArrayList<>();
        for (ItemStack item : items) {
            if (item != null && !item.getType().isAir()) {
                try {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
                    dataOutput.writeObject(item);
                    dataOutput.close();
                    serializedItems.add(Base64.getEncoder().encodeToString(outputStream.toByteArray()));
                } catch (IOException e) {
                    FunnyGuilds.getPluginLogger().error("Failed to serialize vault item: " + e.getMessage());
                }
            }
        }

        return String.join(";", serializedItems);
    }

    /**
     * Deserialize items from Base64-encoded string (items separated by ;).
     */
    private static List<ItemStack> deserializeItems(String itemsData) {
        List<ItemStack> items = new ArrayList<>();

        if (itemsData == null || itemsData.isEmpty()) {
            return items;
        }

        String[] parts = itemsData.split(";");
        for (String serialized : parts) {
            if (serialized.isEmpty()) {
                continue;
            }

            try {
                byte[] data = Base64.getDecoder().decode(serialized);
                ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
                BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
                ItemStack item = (ItemStack) dataInput.readObject();
                dataInput.close();
                if (item != null) {
                    items.add(item);
                }
            } catch (IOException | ClassNotFoundException e) {
                FunnyGuilds.getPluginLogger().error("Failed to deserialize vault item: " + e.getMessage());
            }
        }

        return items;
    }
}
