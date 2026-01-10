package net.dzikoysk.funnyguilds.guild.permission;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.KeyPattern;

/**
 * Represents a permission that can be assigned to a guild member.
 * @param <T> the type of the permission value
 */
public interface GuildPermission<T> {
    
    /**
     * Gets the plugin namespace for permission keys.
     * Retrieves from plugin instance name, converted to lowercase.
     * Returns "funnyguilds" as fallback when plugin instance is not available (e.g., during tests).
     * 
     * @return the plugin namespace (e.g., "funnyguilds")
     */
    static String getPluginNamespace() {
        FunnyGuilds instance = FunnyGuilds.getInstance();
        if (instance == null || instance.getName() == null) {
            return "funnyguilds"; // Fallback for tests
        }
        return instance.getName().toLowerCase();
    }
    
    /**
     * @return the unique key of the permission
     */
    Key key();
    
    /**
     * @return the type of the permission value
     */
    Class<T> getValueType();

    /**
     * @return a new guild permission with the given key and value type
     */
    static <T> GuildPermission<T> permission(@KeyPattern String key, Class<T> valueType) {
        return new SimpleGuildPermission<>(Key.key(getPluginNamespace(), key), valueType);
    }
    
    static GuildPermission<Boolean> booleanPermission(@KeyPattern String key) {
        return permission(key, Boolean.class);
    }
}
