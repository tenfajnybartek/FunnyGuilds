package net.dzikoysk.funnyguilds.config.file;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import net.dzikoysk.funnyguilds.config.sections.SecuritySystemConfiguration;

/**
 * Security configuration file (security.yml)
 * Contains security system settings and protection configuration.
 */
@Header("~-~-~-~-~-~-~-~-~-~-~-~~-~-~-~~ #")
@Header("                                #")
@Header("      FunnyGuilds Security      #")
@Header("                                #")
@Header("~-~-~-~-~-~-~-~-~-~-~-~~-~-~-~~ #")
@Header("Konfiguracja systemu bezpieczeństwa")
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class SecurityConfig extends OkaeriConfig {

    @Comment("")
    @Comment("System bezpieczeństwa (prosty anti-cheat)")
    public SecuritySystemConfiguration securitySystem = new SecuritySystemConfiguration();

}
