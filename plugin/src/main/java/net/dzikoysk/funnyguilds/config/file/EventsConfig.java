package net.dzikoysk.funnyguilds.config.file;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import net.dzikoysk.funnyguilds.config.PluginConfiguration.DeathMessageReceivers;

/**
 * Events configuration file (events.yml)
 * Contains event logging configuration and broadcast settings.
 */
@Header("~-~-~-~-~-~-~-~-~-~-~-~~-~-~-~~ #")
@Header("                                #")
@Header("       FunnyGuilds Events       #")
@Header("                                #")
@Header("~-~-~-~-~-~-~-~-~-~-~-~~-~-~-~~ #")
@Header("Konfiguracja zdarzeń i powiadomień")
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class EventsConfig extends OkaeriConfig {

    @Comment("")
    @Comment("Komu ma być wyświetlana wiadomość o śmierci gracza")
    @Comment("PARTICIPANTS - tylko uczestnikom walki")
    @Comment("GUILD - wszystkim uczestnikom walki i wszystkim członkom gildii ofiary, zabójcy i asystujących")
    @Comment("WORLD - wszystkim uczestnikom walki i wszystkim graczom w świecie w którym umarła ofiara")
    @Comment("ALL - wszystkim uczestnikom walki i wszystkim graczom na serwerze")
    public DeathMessageReceivers deathMessageReceivers = DeathMessageReceivers.ALL;

    @Comment("")
    @Comment("Czy wyłączyć wyświetlanie domyślnej wiadomości o śmierci gracza")
    public boolean disableDefaultDeathMessage = true;

}
