package net.dzikoysk.funnyguilds.config;

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
     */
    public void reloadAll() {
        this.core.load();
        this.database.load();
        this.panel.load();
        this.gameplay.load();
        this.diplomacy.load();
        this.security.load();
        this.display.load();
        this.commands.load();
        this.events.load();
        this.rankSystem.load();
    }

}
