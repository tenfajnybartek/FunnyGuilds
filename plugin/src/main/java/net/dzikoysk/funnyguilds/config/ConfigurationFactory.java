package net.dzikoysk.funnyguilds.config;

import dev.peri.yetanothermessageslibrary.config.serdes.SerdesMessages;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.serdes.commons.SerdesCommons;
import eu.okaeri.configs.validator.okaeri.OkaeriValidator;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import java.io.File;
import java.util.logging.Logger;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.file.CommandsConfig;
import net.dzikoysk.funnyguilds.config.file.CoreConfig;
import net.dzikoysk.funnyguilds.config.file.DatabaseConfig;
import net.dzikoysk.funnyguilds.config.file.DiplomacyConfig;
import net.dzikoysk.funnyguilds.config.file.DisplayConfig;
import net.dzikoysk.funnyguilds.config.file.EventsConfig;
import net.dzikoysk.funnyguilds.config.file.GameplayConfig;
import net.dzikoysk.funnyguilds.config.file.PanelConfig;
import net.dzikoysk.funnyguilds.config.file.RankSystemConfig;
import net.dzikoysk.funnyguilds.config.file.SecurityConfig;
import net.dzikoysk.funnyguilds.config.message.MessageConfiguration;
import net.dzikoysk.funnyguilds.config.serdes.ColorSerializer;
import net.dzikoysk.funnyguilds.config.serdes.DecolorTransformer;
import net.dzikoysk.funnyguilds.config.serdes.EntityTypeTransformer;
import net.dzikoysk.funnyguilds.config.serdes.FunnyPatternTransformer;
import net.dzikoysk.funnyguilds.config.serdes.FunnyTimeFormatterTransformer;
import net.dzikoysk.funnyguilds.config.serdes.FunnyTimeTransformer;
import net.dzikoysk.funnyguilds.config.serdes.ItemStackTransformer;
import net.dzikoysk.funnyguilds.config.serdes.MaterialTransformer;
import net.dzikoysk.funnyguilds.config.serdes.NumberRangeTransformer;
import net.dzikoysk.funnyguilds.config.serdes.RangeFormattingTransformer;
import net.dzikoysk.funnyguilds.config.serdes.RawStringTransformer;
import net.dzikoysk.funnyguilds.config.serdes.SkinTextureSerializer;
import net.dzikoysk.funnyguilds.config.serdes.VectorSerializer;
import net.dzikoysk.funnyguilds.config.tablist.TablistConfiguration;
import net.dzikoysk.funnyguilds.config.tablist.TablistPageSerializer;

public final class ConfigurationFactory {

    private ConfigurationFactory() {
    }

    public static MessageConfiguration createMessageConfiguration(File messageConfigurationFile) {
        return ConfigManager.create(MessageConfiguration.class, (it) -> {
            it.withConfigurer(new YamlBukkitConfigurer());
            it.withSerdesPack(registry -> {
                registry.register(new DecolorTransformer());
                registry.register(new FunnyTimeFormatterTransformer());
                registry.register(new SerdesMessages());
            });

            it.withBindFile(messageConfigurationFile);
            it.saveDefaults();
            it.load(true);
        });
    }

    public static PluginConfiguration createPluginConfiguration(File pluginConfigurationFile) {
        return ConfigManager.create(PluginConfiguration.class, (it) -> {
            it.withConfigurer(new OkaeriValidator(new YamlBukkitConfigurer(), true), new SerdesCommons());
            it.withSerdesPack(registry -> {
                registry.register(new RawStringTransformer());
                registry.register(new ColorSerializer());
                registry.register(new MaterialTransformer());
                registry.register(new ItemStackTransformer());
                registry.register(new EntityTypeTransformer());
                registry.register(new VectorSerializer());
                registry.register(new FunnyTimeTransformer());
                registry.register(new FunnyPatternTransformer());
                registry.register(new RangeFormattingTransformer());
            });

            it.withBindFile(pluginConfigurationFile);
            it.withLogger(FunnyGuilds.getInstance().getLogger());
            it.saveDefaults();
            it.load(true);
        });
    }

    public static TablistConfiguration createTablistConfiguration(File tablistConfigurationFile) {
        return ConfigManager.create(TablistConfiguration.class, (it) -> {
            it.withConfigurer(new OkaeriValidator(new YamlBukkitConfigurer(), true), new SerdesCommons());
            it.withSerdesPack(registry -> {
                registry.register(new NumberRangeTransformer());
                registry.register(new TablistPageSerializer());
                registry.register(new SkinTextureSerializer());
            });

            it.withBindFile(tablistConfigurationFile);
            it.saveDefaults();
            it.load(true);
        });
    }

    /**
     * Creates a PluginConfigurationContainer with all separate configuration files loaded from the config directory.
     *
     * @param configDir The directory containing all configuration files (plugins/FunnyGuilds/config/)
     * @param logger The logger for configuration loading
     * @return A container with all configuration objects
     */
    public static PluginConfigurationContainer createConfigurationContainer(File configDir, Logger logger) {
        // Ensure config directory exists
        if (!configDir.exists()) {
            configDir.mkdirs();
        }

        // Create individual configuration files
        CoreConfig core = createConfig(CoreConfig.class, new File(configDir, "config.yml"), logger);
        DatabaseConfig database = createConfig(DatabaseConfig.class, new File(configDir, "database.yml"), logger);
        PanelConfig panel = createConfig(PanelConfig.class, new File(configDir, "panel.yml"), logger);
        GameplayConfig gameplay = createConfig(GameplayConfig.class, new File(configDir, "gameplay.yml"), logger);
        DiplomacyConfig diplomacy = createConfig(DiplomacyConfig.class, new File(configDir, "diplomacy.yml"), logger);
        SecurityConfig security = createConfig(SecurityConfig.class, new File(configDir, "security.yml"), logger);
        DisplayConfig display = createConfig(DisplayConfig.class, new File(configDir, "display.yml"), logger);
        CommandsConfig commands = createConfig(CommandsConfig.class, new File(configDir, "commands.yml"), logger);
        EventsConfig events = createConfig(EventsConfig.class, new File(configDir, "events.yml"), logger);
        RankSystemConfig rankSystem = createConfig(RankSystemConfig.class, new File(configDir, "rank-system.yml"), logger);

        return new PluginConfigurationContainer(
                core,
                database,
                panel,
                gameplay,
                diplomacy,
                security,
                display,
                commands,
                events,
                rankSystem
        );
    }

    /**
     * Creates a configuration object of the specified type.
     *
     * @param configClass The class of the configuration
     * @param configFile The file to load/save the configuration
     * @param logger The logger for configuration loading
     * @param <T> The type of configuration
     * @return The loaded configuration object
     */
    private static <T extends OkaeriConfig> T createConfig(Class<T> configClass, File configFile, Logger logger) {
        return ConfigManager.create(configClass, (it) -> {
            it.withConfigurer(new OkaeriValidator(new YamlBukkitConfigurer(), true), new SerdesCommons());
            it.withSerdesPack(registry -> {
                registry.register(new RawStringTransformer());
                registry.register(new ColorSerializer());
                registry.register(new MaterialTransformer());
                registry.register(new ItemStackTransformer());
                registry.register(new EntityTypeTransformer());
                registry.register(new VectorSerializer());
                registry.register(new FunnyTimeTransformer());
                registry.register(new FunnyPatternTransformer());
                registry.register(new RangeFormattingTransformer());
                registry.register(new NumberRangeTransformer());
            });

            it.withBindFile(configFile);
            it.withLogger(logger);
            it.saveDefaults();
            it.load(true);
        });
    }

}
