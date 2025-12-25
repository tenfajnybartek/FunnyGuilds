package net.dzikoysk.funnyguilds.config.file;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import eu.okaeri.validator.annotation.Min;
import eu.okaeri.validator.annotation.PositiveOrZero;
import java.time.Duration;

/**
 * Diplomacy configuration file (diplomacy.yml)
 * Contains ally/enemy mechanics and relation limits.
 */
@Header("~-~-~-~-~-~-~-~-~-~-~-~~-~-~-~~ #")
@Header("                                #")
@Header("      FunnyGuilds Diplomacy     #")
@Header("                                #")
@Header("~-~-~-~-~-~-~-~-~-~-~-~~-~-~-~~ #")
@Header("Konfiguracja systemu dyplomacji - sojusze i wojny")
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class DiplomacyConfig extends OkaeriConfig {

    @Min(0)
    @Comment("")
    @Comment("Maksymalna liczba sojuszy między gildiami")
    public int maxAllies = 15;

    @Min(0)
    @Comment("")
    @Comment("Maksymalna liczba wojen między gildiami")
    public int maxEnemies = 15;

}
