package net.dzikoysk.funnyguilds.config.file;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import net.dzikoysk.funnyguilds.config.sections.CommandsConfiguration;

/**
 * Commands configuration file (commands.yml)
 * Contains command settings, aliases, and cooldowns.
 */
@Header("~-~-~-~-~-~-~-~-~-~-~-~~-~-~-~~ #")
@Header("                                #")
@Header("      FunnyGuilds Commands      #")
@Header("                                #")
@Header("~-~-~-~-~-~-~-~-~-~-~-~~-~-~-~~ #")
@Header("Konfiguracja komend")
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class CommandsConfig extends OkaeriConfig {

    @Comment("")
    @Comment("Nazwy komend")
    public CommandsConfiguration commands = new CommandsConfiguration();

}
