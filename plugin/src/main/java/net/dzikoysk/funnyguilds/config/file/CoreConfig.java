package net.dzikoysk.funnyguilds.config.file;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Core configuration file (config.yml)
 * Contains plugin name, debug mode, locale settings, and basic plugin settings.
 */
@Header("~-~-~-~-~-~-~-~-~-~-~-~~-~-~-~~ #")
@Header("                                #")
@Header("          FunnyGuilds           #")
@Header("         4.13.0 Snowdrop        #")
@Header("                                #")
@Header("~-~-~-~-~-~-~-~-~-~-~-~~-~-~-~~ #")
@Header("Główna konfiguracja pluginu FunnyGuilds")
@Header("FunnyGuilds wspiera PlaceholderAPI, lista dodawanych placeholderów znajduje się tutaj:")
@Header("https://github.com/FunnyGuilds/FunnyGuilds/wiki/%5BPL%5D-PlaceholderAPI")
@Header(" ")
@Header("Jeżeli chcesz, aby dana wiadomość była pusta, zamiast wiadomości umieść: ''")
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class CoreConfig extends OkaeriConfig {

    @Comment("")
    @Comment("Wyświetlana nazwa pluginu")
    public String pluginName = "FunnyGuilds";

    @Comment("")
    @Comment("Czy plugin ma działać w trybie debug - służy on do wysyłania dodatkowych wiadomości, w celu diagnozowania błędów")
    public boolean debugMode = false;

    @Comment("")
    @Comment("Czy informacje o aktualizacji mają być widoczne podczas wejścia na serwer")
    public boolean updateInfo = true;

    @Comment("")
    @Comment("Czy informacje o aktualizacji wersji nightly mają być widoczne podczas wejścia na serwer")
    @Comment("Ta opcja działa tylko wtedy, gdy włączona jest opcja 'update-info'")
    public boolean updateNightlyInfo = true;

    @Comment("")
    @Comment("Domyślny używany język używany przez plugin jeżeli nie można znaleźć języka gracza")
    public Locale defaultLocale = Locale.forLanguageTag("pl");

    @Comment("")
    @Comment("Lista języków używanych przez plugin")
    @Comment("Jeżeli chcesz dodać nowy język dodaj go tutaj - utworzy to nowy plik z domyślnymi wartościami, które możesz później edytować")
    @Comment("Języki gracza są dobierane automatycznie na podstawie ustawiań klienta")
    public Set<Locale> availableLocales = new HashSet<>(Arrays.asList(Locale.forLanguageTag("pl")));

    @Comment("")
    @Comment("Czy ma być włączona możliwość zakładania gildii (można ją zmienić także za pomocą komendy /ga enabled)")
    public boolean guildsEnabled = true;

    @Comment("")
    @Comment("Co ile minut dane są automatycznie zapisywane")
    public int dataInterval = 1;

    @Comment("")
    @Comment("Hooki do pluginów, które powinny zostać wyłączone, opcja ta powinna być stosowania jedynie w awaryjnych sytuacjach!")
    @Comment("Lista hooków, które można wyłączyć: WorldEdit, WorldGuard, Vault, PlaceholderAPI, HolographicDisplays, DecentHolograms, dynmap")
    @Comment("Aby zostawić wszystkie hooki włączone wystarczy wpisać: disabled-hooks: []")
    public Set<String> disabledHooks = new HashSet<>();

}
