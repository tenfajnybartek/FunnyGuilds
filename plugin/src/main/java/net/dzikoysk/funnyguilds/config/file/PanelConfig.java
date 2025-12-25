package net.dzikoysk.funnyguilds.config.file;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import net.dzikoysk.funnyguilds.config.sections.DiplomacyConfiguration;
import net.dzikoysk.funnyguilds.config.sections.EventLogConfiguration;
import net.dzikoysk.funnyguilds.config.sections.GuildVaultConfiguration;
import net.dzikoysk.funnyguilds.config.sections.PanelConfiguration;
import net.dzikoysk.funnyguilds.config.sections.PermissionsPanelConfiguration;

/**
 * Panel configuration file (panel.yml)
 * Contains guild panel settings, permissions panel, vault, event log, and diplomacy GUI settings.
 */
@Header("~-~-~-~-~-~-~-~-~-~-~-~~-~-~-~~ #")
@Header("                                #")
@Header("       FunnyGuilds Panel        #")
@Header("                                #")
@Header("~-~-~-~-~-~-~-~-~-~-~-~~-~-~-~~ #")
@Header("Konfiguracja paneli zarządzania gildią")
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class PanelConfig extends OkaeriConfig {

    @Comment("")
    @Comment("Konfiguracja panelu zarządzania gildią dla lidera")
    public PanelConfiguration guildPanel = new PanelConfiguration();

    @Comment("")
    @Comment("Konfiguracja panelu uprawnień członków gildii")
    public PermissionsPanelConfiguration permissionsPanel = new PermissionsPanelConfiguration();

    @Comment("")
    @Comment("Konfiguracja skarbca gildii")
    public GuildVaultConfiguration guildVault = new GuildVaultConfiguration();

    @Comment("")
    @Comment("Konfiguracja dziennika zdarzeń gildii")
    public EventLogConfiguration eventLog = new EventLogConfiguration();

    @Comment("")
    @Comment("Konfiguracja panelu dyplomacji gildii")
    public DiplomacyConfiguration diplomacy = new DiplomacyConfiguration();

}
