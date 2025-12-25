package net.dzikoysk.funnyguilds.config;

import eu.okaeri.configs.OkaeriConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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

/**
 * Container that holds all plugin configuration files.
 * This replaces the monolithic PluginConfiguration class.
 */
public class PluginConfigurationContainer {

    private final CoreConfig core;
    private final DatabaseConfig database;
    private final PanelConfig panel;
    private final GameplayConfig gameplay;
    private final DiplomacyConfig diplomacy;
    private final SecurityConfig security;
    private final DisplayConfig display;
    private final CommandsConfig commands;
    private final EventsConfig events;
    private final RankSystemConfig rankSystem;

    public PluginConfigurationContainer(
            CoreConfig core,
            DatabaseConfig database,
            PanelConfig panel,
            GameplayConfig gameplay,
            DiplomacyConfig diplomacy,
            SecurityConfig security,
            DisplayConfig display,
            CommandsConfig commands,
            EventsConfig events,
            RankSystemConfig rankSystem
    ) {
        this.core = core;
        this.database = database;
        this.panel = panel;
        this.gameplay = gameplay;
        this.diplomacy = diplomacy;
        this.security = security;
        this.display = display;
        this.commands = commands;
        this.events = events;
        this.rankSystem = rankSystem;
    }

    public CoreConfig core() {
        return this.core;
    }

    public DatabaseConfig database() {
        return this.database;
    }

    public PanelConfig panel() {
        return this.panel;
    }

    public GameplayConfig gameplay() {
        return this.gameplay;
    }

    public DiplomacyConfig diplomacy() {
        return this.diplomacy;
    }

    public SecurityConfig security() {
        return this.security;
    }

    public DisplayConfig display() {
        return this.display;
    }

    public CommandsConfig commands() {
        return this.commands;
    }

    public EventsConfig events() {
        return this.events;
    }

    public RankSystemConfig rankSystem() {
        return this.rankSystem;
    }

    /**
     * Reloads all configuration files.
     * Returns a list of config names that failed to reload.
     *
     * @param logger The logger to use for error messages
     * @return List of configuration names that failed to reload, empty if all succeeded
     */
    public List<String> reloadAll(Logger logger) {
        List<String> failed = new ArrayList<>();
        
        safeReload("core", this.core, logger, failed);
        safeReload("database", this.database, logger, failed);
        safeReload("panel", this.panel, logger, failed);
        safeReload("gameplay", this.gameplay, logger, failed);
        safeReload("diplomacy", this.diplomacy, logger, failed);
        safeReload("security", this.security, logger, failed);
        safeReload("display", this.display, logger, failed);
        safeReload("commands", this.commands, logger, failed);
        safeReload("events", this.events, logger, failed);
        safeReload("rankSystem", this.rankSystem, logger, failed);
        
        return failed;
    }

    private void safeReload(String name, OkaeriConfig config, Logger logger, List<String> failed) {
        try {
            config.load();
        } catch (Exception e) {
            failed.add(name);
            if (logger != null) {
                logger.log(Level.SEVERE, "Failed to reload configuration: " + name, e);
            }
        }
    }

}
