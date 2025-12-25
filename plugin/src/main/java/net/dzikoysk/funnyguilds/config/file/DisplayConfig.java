package net.dzikoysk.funnyguilds.config.file;

import com.google.common.collect.ImmutableMap;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import eu.okaeri.validator.annotation.NotBlank;
import eu.okaeri.validator.annotation.PositiveOrZero;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import net.dzikoysk.funnyguilds.config.RangeFormatting;
import net.dzikoysk.funnyguilds.config.RawString;
import net.dzikoysk.funnyguilds.config.sections.ScoreboardConfiguration;
import net.dzikoysk.funnyguilds.config.sections.TopConfiguration;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.formatter.FunnyFormatter;
import org.bukkit.Color;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

/**
 * Display configuration file (display.yml)
 * Contains top rankings, scoreboard settings, holograms, tab list, name tags, chat formatting.
 */
@Header("~-~-~-~-~-~-~-~-~-~-~-~~-~-~-~~ #")
@Header("                                #")
@Header("       FunnyGuilds Display      #")
@Header("                                #")
@Header("~-~-~-~-~-~-~-~-~-~-~-~~-~-~-~~ #")
@Header("Konfiguracja wyświetlania - scoreboard, hologramy, top, chat")
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class DisplayConfig extends OkaeriConfig {

    // ===== Top Rankings =====

    @Comment("")
    @Comment("Konfiguracja topek")
    public TopConfiguration top = new TopConfiguration();

    // ===== Points Formatting =====

    @Comment("")
    @Comment("Wygląd znacznika {POINTS-FORMAT} i {G-POINTS-FORMAT} w zależności od wartości punktów")
    public List<RangeFormatting> pointsFormat = Arrays.asList(
            new RangeFormatting(0, 749, "&4{POINTS}"),
            new RangeFormatting(750, 999, "&c{POINTS}"),
            new RangeFormatting(1000, 1499, "&a{POINTS}"),
            new RangeFormatting(1500, Integer.MAX_VALUE, "&6&l{POINTS}")
    );

    @Comment("")
    @Comment("Znacznik z punktami dodawany do zmiennej {PTOP-x}")
    public RawString ptopPoints = new RawString(" &7[{POINTS}&7]");

    @Comment("")
    @Comment("Znacznik z punktami dodawany do zmiennej {GTOP-x}")
    public RawString gtopPoints = new RawString(" &7[&b{POINTS-FORMAT}&7]");

    @Comment("")
    @Comment("Wygląd znacznika {MINUS-FORMATTED} i {PLUS-FORMATTED}, w zależności od wartości zmiany w rankingu")
    public List<RangeFormatting> killPointsChangeFormat = Arrays.asList(
            new RangeFormatting(Integer.MIN_VALUE, -1, "&c-{CHANGE}"),
            new RangeFormatting(0, 0, "&7{CHANGE}"),
            new RangeFormatting(1, Integer.MAX_VALUE, "&a+{CHANGE}")
    );

    @Comment("")
    @Comment("Wygląd znacznika {PING-FORMAT} w zależności od wartości pingu")
    public List<RangeFormatting> pingFormat = Arrays.asList(
            new RangeFormatting(0, 75, "&a{PING}"),
            new RangeFormatting(76, 150, "&e{PING}"),
            new RangeFormatting(151, 300, "&c{PING}"),
            new RangeFormatting(301, Integer.MAX_VALUE, "&c{PING}")
    );

    // ===== Top Online/Offline Status =====

    @Comment("")
    @Comment("Czy ptop-online/ptop-offline mają uznawać graczy na vanishu za graczy offline")
    public boolean ptopRespectVanish = true;

    @Comment("")
    @Comment("Kolory dodawane przed nickiem gracza online przy zamianie zmiennej {PTOP-x}")
    public RawString ptopOnline = new RawString("&a");

    @Comment("")
    @Comment("Kolory dodawane przed nickiem gracza offline przy zamianie zmiennej {PTOP-x}")
    public RawString ptopOffline = new RawString("&c");

    @Comment("")
    @Comment("Czy gtop-online/gtop-offline mają uznawać graczy na vanishu za graczy offline")
    public boolean gtopRespectVanish = true;

    @Comment("")
    @Comment("Kolory dodawane przed tagiem gildii online przy zamianie zmiennej {GTOP-x}")
    public RawString gtopOnline = new RawString("&a");

    @Comment("")
    @Comment("Kolory dodawane przed tagiem gildii offline przy zamianie zmiennej {GTOP-x}")
    public RawString gtopOffline = new RawString("&c");

    // ===== Scoreboard =====

    @Comment("")
    @Comment("Konfiguracja scoreboardu")
    public ScoreboardConfiguration scoreboard = new ScoreboardConfiguration();

    // ===== Lives Symbol =====

    @Comment("")
    public LivesRepeatingSymbol livesRepeatingSymbol = new LivesRepeatingSymbol();

    public static class LivesRepeatingSymbol extends OkaeriConfig {

        @Comment("Symbol (lub słowo), który ma być powtarzany przy użyciu placeholdera LIVES-SYMBOL lub LIVES-SYMBOL-ALL")
        public RawString full = new RawString("&c\u2764");

        @Comment("")
        @Comment("Symbol (lub słowo), który ma być powtarzany przy użyciu placeholdera LIVES-SYMBOL")
        public RawString empty = new RawString("&8\u2764");

        @Comment("")
        @Comment("Symbol (lub słowo), który ma być pokazywany na końcu placeholdera LIVES-SYMBOL, kiedy gildia posiada więcej żyć niz podstawowe (war-lives)")
        public RawString more = new RawString("&a+");

    }

    // ===== Chat Formatting =====

    @Comment("")
    @Comment("Wygląd znacznika {POS} wstawionego w format chatu")
    public RawString chatPosition = new RawString("&b{POS} ");

    @Comment("")
    @Comment("Znacznik dla lidera gildii")
    @CustomKey("chat-position-leader")
    public RawString chatPositionLeader = new RawString("**");

    @Comment("")
    @Comment("Znacznik dla zastępcy gildii")
    @CustomKey("chat-position-deputy")
    public RawString chatPositionDeputy = new RawString("*");

    @Comment("")
    @Comment("Znacznik dla członka gildii")
    @CustomKey("chat-position-member")
    public RawString chatPositionMember = new RawString("");

    @Comment("")
    @Comment("Wygląd znacznika {TAG} wstawionego w format chatu")
    public RawString chatGuild = new RawString("&b{TAG} ");

    @Comment("")
    @Comment("Wygląd znacznika {RANK} wstawionego w format chatu")
    public RawString chatRank = new RawString("&b{RANK} ");

    @Comment("")
    @Comment("Wygląd znacznika {POINTS} wstawionego w format chatu")
    public RawString chatPoints = new RawString("&b{POINTS} ");

    // ===== Guild Chat =====

    @NotBlank
    @Comment("")
    @Comment("Symbol, od którego zaczyna się wiadomość do gildii")
    @CustomKey("chat-priv")
    public String chatPriv = "!";

    @NotBlank
    @Comment("")
    @Comment("Symbol od którego zaczyna się wiadomość do sojuszników gildii")
    @CustomKey("chat-ally")
    public String chatAlly = "!!";

    @NotBlank
    @Comment("")
    @Comment("Symbol od którego zaczyna się wiadomość do wszystkich gildii")
    @CustomKey("chat-global")
    public String chatGlobal = "!!!";

    @Comment("")
    @Comment("Wygląd wiadomości wysyłanej na czacie gildii")
    public RawString chatPrivDesign = new RawString("&8[&aChat gildii&8] &7{POS}{PLAYER}&8:&f {MESSAGE}");

    @Comment("")
    @Comment("Wygląd wiadomości wysyłanej na czacie dla sojuszników")
    public RawString chatAllyDesign = new RawString("&8[&6Chat sojuszniczy&8] &8{TAG} &7{POS}{PLAYER}&8:&f {MESSAGE}");

    @Comment("")
    @Comment("Wygląd wiadomości wysyłanej na czacie globalnym gildii")
    public RawString chatGlobalDesign = new RawString("&8[&cChat globalny gildii&8] &8{TAG} &7{POS}{PLAYER}&8:&f {MESSAGE}");

    @Comment("")
    @Comment("Wygląd wiadomoci wysyłanej na czacie gildyjnym/sojuszniczym/globalnym gildii, dla osób z włączonym /ga spy")
    public RawString chatSpyDesign = new RawString("&8[&6Spy&8] &7{PLAYER}&8:&f {MESSAGE}");

    @Comment("")
    @Comment("Czy wiadomości z chatów gildyjnych powinny być wyświetlane w logach serwera")
    @CustomKey("log-guild-chat")
    public boolean logGuildChat = false;

    // ===== Relational Tags =====

    @Comment("")
    public RelationalTag relationalTag = new RelationalTag();

    public static class RelationalTag extends OkaeriConfig {

        @Comment("Wygląd tagu osób w tej samej gildii")
        public RawString our = new RawString("&a{TAG}&f");

        @Comment("")
        @Comment("Wygląd tagu gildii sojuszniczej")
        public RawString allies = new RawString("&6{TAG}&f");

        @Comment("")
        @Comment("Wygląd tagu wrogiej gildii")
        public RawString enemies = new RawString("&c{TAG}&f");

        @Comment("")
        @Comment("Wygląd tagu gildii neutralnej, widziany również przez graczy bez gildii")
        public RawString other = new RawString("&7{TAG}&f");

        public String chooseTag(@Nullable Guild guild, @Nullable Guild targetGuild) {
            if (targetGuild == null) {
                return "";
            }

            if (guild == null) {
                return this.other.getValue();
            }

            if (guild.equals(targetGuild)) {
                return this.our.getValue();
            }

            if (guild.isAlly(targetGuild)) {
                return this.allies.getValue();
            }

            if (guild.isEnemy(targetGuild) || targetGuild.isEnemy(guild)) {
                return this.enemies.getValue();
            }

            return this.other.getValue();
        }

        public String chooseAndPrepareTag(@Nullable Guild guild, @Nullable Guild targetGuild) {
            if (targetGuild == null) {
                return "";
            }

            return FunnyFormatter.of("{TAG}", targetGuild.getTag())
                    .replace(this.chooseTag(guild, targetGuild));
        }

    }

    // ===== Player Info =====

    @Comment("")
    @Comment("Czy pokazywać informacje przy kliknięciu PPM na gracza")
    @CustomKey("info-player-enabled")
    public boolean infoPlayerEnabled = true;

    @Comment("")
    @Comment("Czy pokazać informacje z komendy /gracz przy kliknięciu PPM")
    @CustomKey("info-player-command")
    public boolean infoPlayerCommand = true;

    @PositiveOrZero
    @Comment("")
    @Comment("Cooldown pomiędzy pokazywaniem informacji przez PPM")
    @CustomKey("info-player-cooldown")
    public Duration infoPlayerCooldown = Duration.ofSeconds(5);

    @Comment("")
    @Comment("Czy trzeba kucać, aby przy kliknięciu PPM na gracza wyświetliło informacje o nim")
    @CustomKey("info-player-sneaking")
    public boolean infoPlayerSneaking = true;

    // ===== Notification Settings =====

    @Comment("")
    @Comment("Czy powiadomienie o zabójstwie gracza powinno się wyświetlać dla zabójcy")
    public boolean displayNotificationForKiller = true;

    @Comment("")
    @Comment("Czy powiadomienie o śmierci powinno się wyświetlać dla ofiary")
    public boolean displayNotificationForVictim = true;

    @Comment("")
    @Comment("Czy powiadomienie o asyście powinno się wyświetlać dla asystujących graczy")
    public boolean displayNotificationForAssist = true;

    @Comment("")
    @Comment("Czy powiadomienia o wejściu na teren gildii członka gildii powinny byc wyświetlane")
    @CustomKey("notification-guild-member-display")
    public boolean regionEnterNotificationGuildMember = false;

    // ===== Material Translation =====

    @Comment("")
    @Comment("Czy włączyć tłumaczenie nazw przedmiotów?")
    @CustomKey("translated-materials-enable")
    public boolean translatedMaterialsEnable = true;

    @Comment("")
    @Comment("Czy do tłumaczenia nazw przedmiotów plugin ma używać tzw. TranslatableComponents")
    public boolean useTranslatableComponentsForMaterials = false;

    @Comment("")
    @Comment("Tłumaczenia nazw przedmiotów dla znaczników {ITEM}, {ITEMS}, {ITEM-NO-AMOUNT}, {WEAPON}")
    @CustomKey("translated-materials-name")
    public Map<Material, String> translatedMaterials = ImmutableMap.<Material, String>builder()
            .put(Material.DIAMOND_SWORD, "&3diamentowy miecz")
            .put(Material.IRON_SWORD, "&7zelazny miecz")
            .put(Material.GOLD_INGOT, "&ezloto")
            .build();

    @Comment("")
    @Comment("Wygląd znaczników {ITEM} i {ITEMS} za liczbą przedmiotu")
    public RawString itemAmountSuffix = new RawString("x ");

    // ===== Dynmap Hook =====

    @Comment("")
    @Comment("Konfiguracja hooku do pluginy 'dynmap'")
    public DynmapHook dynmapHook = new DynmapHook();

    public static class DynmapHook extends OkaeriConfig {

        @Comment("")
        @Comment("Co ile ticków etykiety znaczników mają być odświeżane (20 ticków = 1 sekunda)")
        public long updateInterval = 60 * 20;

        @Comment("")
        @Comment("Oznaczenie listy gildii na mapie")
        public String guildSetLabel = "Gildie";

        @Comment("")
        public Center center = new Center();

        public static class Center extends OkaeriConfig {

            @Comment("Czy wskaźnik środka gildii ma być widoczny na mapie")
            public boolean enabled = true;

            @Comment("Czy wskaźnik środka powinien używać współrzędnych Y najwyższego punktu zamiast rzeczywistej wysokości")
            public boolean hideCenterY = false;

            @Comment("")
            @Comment("Etykieta środka gildii")
            public String label = "{NAME} ({TAG})";

            @Comment("")
            @Comment("Ikona oznaczenia środka gildii")
            public String icon = "shield";

        }

        @Comment("")
        public Area area = new Area();

        public static class Area extends OkaeriConfig {

            @Comment("Czy obszar gildii ma być widoczny na mapie")
            public boolean enabled = true;

            @Comment("")
            @Comment("Etykieta obszaru gildii")
            public String label = "<b>{NAME} ({TAG})</b><br><br>" +
                    "<b>Właściciel:</b> {OWNER}<br>" +
                    "<b>Gracze (ONL/ALL):</b> {MEMBERS-ONLINE}/{MEMBERS-ALL}<br><br>" +
                    "<b>Punkty (Średnio):</b> {AVG-POINTS}";

            @Comment("")
            @Comment("Konfiguracja wyglądu wypełnienia obszaru gildii")
            public Fill fill = new Fill();

            public static class Fill extends OkaeriConfig {

                @Comment("Przezroczystość wypełnienia:")
                @Comment(" > 0 - całkowicie przezroczyste")
                @Comment(" > 1 - całkowicie wypełnione")
                public double opacity = 0.6;

                @Comment("")
                @Comment("Kolor wypełnienia")
                public Color color = Color.fromRGB(25, 25, 25);

            }

            @Comment("")
            @Comment("Konfiguracja linii (granic) obszaru gildii")
            public Line line = new Line();

            public static class Line extends OkaeriConfig {

                @Comment("Przezroczystość linii:")
                @Comment(" > 0 - całkowicie przezroczyste")
                @Comment(" > 1 - całkowicie wypełnione")
                public double opacity = 1;

                @Comment("")
                @Comment("Grubość linii (w pikselach)")
                public int weight = 3;

                @Comment("")
                @Comment("Kolor linii")
                public Color color = Color.fromRGB(10, 10, 10);

            }

        }

    }

}
