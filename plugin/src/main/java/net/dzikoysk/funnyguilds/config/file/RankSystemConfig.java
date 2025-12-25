package net.dzikoysk.funnyguilds.config.file;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
import eu.okaeri.configs.annotation.Exclude;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import eu.okaeri.configs.exception.OkaeriException;
import eu.okaeri.validator.annotation.DecimalMax;
import eu.okaeri.validator.annotation.DecimalMin;
import eu.okaeri.validator.annotation.Min;
import eu.okaeri.validator.annotation.Positive;
import eu.okaeri.validator.annotation.PositiveOrZero;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.NumberRange;
import net.dzikoysk.funnyguilds.config.RawString;
import net.dzikoysk.funnyguilds.rank.RankSystem;
import net.dzikoysk.funnyguilds.shared.bukkit.ItemUtils;
import org.bukkit.inventory.ItemStack;
import panda.std.Option;

/**
 * Rank system configuration file (rank-system.yml)
 * Contains point calculation, rank decay, ELO settings, and ranking configuration.
 */
@Header("~-~-~-~-~-~-~-~-~-~-~-~~-~-~-~~ #")
@Header("                                #")
@Header("     FunnyGuilds Rank System    #")
@Header("                                #")
@Header("~-~-~-~-~-~-~-~-~-~-~-~~-~-~-~~ #")
@Header("Konfiguracja systemu rankingowego")
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class RankSystemConfig extends OkaeriConfig {

    // ===== Basic Settings =====

    @Comment("")
    @Comment("Ranking, od którego rozpoczyna gracz")
    public int rankStart = 1000;

    @Min(1)
    @Comment("")
    @Comment("Co ile ticków ranking graczy oraz gildii powinien być odświeżany (20 ticków = 1 sekunda)")
    public int rankingUpdateInterval = 40;

    // ===== Farming Protection =====

    @Comment("")
    @Comment("Czy blokada nabijania rankingu na tych samych osobach powinna byc włączona")
    public boolean rankFarmingProtect = true;

    @Comment("")
    @Comment("Czy opcja blokady nabijania rankingu powinna działać w obie strony tzn. jeśli gracz nas zabije, a potem zabijemy go my to nie dostaniemy punktów")
    public boolean bidirectionalRankFarmingProtect = false;

    @Comment("")
    @Comment("Czy ostatnia osoba, która zaatakowała gracza, który zginął, ma być uznawana za jego zabójcę")
    @CustomKey("rank-farming-last-attacker-as-killer")
    public boolean considerLastAttackerAsKiller = false;

    @PositiveOrZero
    @Comment("")
    @Comment("Czas przez jaki osoba, która zaatakowała gracza, który zginął, ma być uznawany za jego zabójcę")
    @CustomKey("rank-farming-consideration-timeout")
    public Duration lastAttackerAsKillerConsiderationTimeout = Duration.ofSeconds(30);

    @PositiveOrZero
    @Comment("")
    @Comment("Czas trwania blokady nabijania rankingu po walce dwóch osób")
    public Duration rankFarmingCooldown = Duration.ofHours(2);

    @Comment("")
    @Comment("Czy blokada nabijania rankingu powinna działać również na adres IP gracza zabitego")
    public boolean rankFarmingCooldownIP = true;

    @Comment("")
    @Comment("Czy ma być zablokowana zmiana rankingu, jeśli obie osoby z walki mają taki sam adres IP")
    public boolean rankIPProtect = false;

    @Comment("")
    @Comment("Czy ma być zablokowana zmiana rankingu, jeśli obie osoby z walki są członkami tej samej gildii")
    public boolean rankMemberProtect = false;

    @Comment("")
    @Comment("Czy ma być zablokowana zmiana rankingu, jeśli obie osoby z walki są z sojuszniczych gildii")
    public boolean rankAllyProtect = false;

    @Comment("")
    @Comment("Czy gracze z uprawnieniem 'funnyguilds.ranking.exempt' powinni byc uwzględnieni przy wyznaczaniu pozycji gracza w rankingu")
    @CustomKey("skip-privileged-players-in-rank-positions")
    public boolean skipPrivilegedPlayersInRankPositions = false;

    // ===== Damage Tracking =====

    public DamageTracking damageTracking = new DamageTracking();

    public static class DamageTracking extends OkaeriConfig {

        @Comment("Czas po którym zadane obrażenia, stają się \"przestarzałe\"")
        public Duration expireTime = Duration.ofMinutes(1);

        @Min(-1)
        @Comment("Jak długa ma być historia zadanych obrażeń.")
        @Comment("Wstaw -1 jeśli ma być nieskończona.")
        public int maxTracks = 30;

    }

    // ===== Assist System =====

    @Comment("")
    @Comment("Czy system asyst ma byc włączony")
    @CustomKey("rank-assist-enable")
    public boolean assistEnable = true;

    @Min(-1)
    @Comment("")
    @Comment("Limit asyst, wpisz liczbę ujemną aby wyłączyć")
    @CustomKey("assists-limit")
    public int assistsLimit = -1;

    @DecimalMin("0")
    @DecimalMax("1")
    @Comment("")
    @Comment("Jaka część rankingu za zabicie idzie na konto zabójcy")
    @Comment("1 to cały ranking, 0 to nic")
    @CustomKey("rank-assist-killer-share")
    public double assistKillerShare = 0.5;

    @Comment("")
    @Comment("Czy zabójcy zawsze mają dzielić sie ilością punktów według rank-assist-killer-share, nawet gdy nie ma osób asystujących")
    @CustomKey("rank-assist-victim-always-share")
    public boolean assistKillerAlwaysShare = false;

    @Comment("")
    @Comment("Na jakich regionach ma być ignorowane nadawanie asyst")
    @Comment("UWAGA: wymagany plugin WorldGuard")
    public Set<String> assistsRegionsIgnored = Collections.emptySet();

    // ===== Ranking System Type =====

    @Comment("")
    @Comment("System rankingowy używany przez plugin, do wyboru:")
    @Comment(" ELO - system bazujacy na rankingu szachowym ELO, najlepiej zbalansowany ze wszystkich trzech")
    @Comment(" PERCENT - system, który obu graczom zabiera procent rankingu osoby zabitej")
    @Comment(" STATIC - system, który zawsze zabiera x rankingu zabijającemu i x zabitemu")
    @CustomKey("rank-system")
    public RankSystem.Type rankSystem = RankSystem.Type.ELO;

    // ===== ELO Settings =====

    @Comment("")
    @Comment("Sekcja używana TYLKO jeśli wybranym rank-system jest ELO!")
    @Comment("Lista stałych do obliczeń rankingowych ELO - im mniejsza stała, tym mniejsze zmiany rankingu")
    @CustomKey("elo-constants")
    public List<String> eloConstants_ = Arrays.asList("0-1999 32", "2000-2400 24", "2401-* 16");

    @Exclude
    public Map<NumberRange, Integer> eloConstants;

    @Positive
    @Comment("")
    @Comment("Sekcja używana TYLKO jeśli wybranym rank-system jest ELO!")
    @Comment("Dzielnik obliczeń rankingowych ELO - im mniejszy dzielnik, tym większe zmiany rankingu")
    @CustomKey("elo-divider")
    public double eloDivider = 400.0D;

    @Positive
    @Comment("")
    @Comment("Sekcja używana TYLKO jeśli wybranym rank-system jest ELO!")
    @Comment("Wykładnik potęgi obliczeń rankingowych ELO - im mniejszy wykładnik, tym wieksze zmiany rankingu")
    @CustomKey("elo-exponent")
    public double eloExponent = 10.0D;

    // ===== Percent System Settings =====

    @DecimalMin("0")
    @Comment("")
    @Comment("Sekcja używana TYLKO jeśli wybranym rank-system jest PERCENT!")
    @Comment("Procent rankingu osoby zabitej, o jaki zmienią się rankingi po walce")
    @CustomKey("percent-rank-change")
    public double percentRankChange = 1.0;

    // ===== Static System Settings =====

    @Min(0)
    @Comment("")
    @Comment("Sekcja używana TYLKO jeśli wybranym rank-system jest STATIC!")
    @Comment("Punkty dawane osobie, która wygrała walkę")
    @CustomKey("static-attacker-change")
    public int staticAttackerChange = 15;

    @Min(0)
    @Comment("")
    @Comment("Sekcja używana TYLKO jeśli wybranym rank-system jest STATIC!")
    @Comment("Punkty zabierane osobie, która przegrała walkę")
    @CustomKey("static-victim-change")
    public int staticVictimChange = 10;

    // ===== Reset Items =====

    @Comment("")
    @Comment("Lista przedmiotów wymaganych do resetu rankingu")
    @CustomKey("rank-reset-needed-items")
    public List<ItemStack> rankResetItems = ItemUtils.parseItems("1 diamond");

    @Comment("")
    @Comment("Lista przedmiotów wymaganych do resetu statystyk")
    @CustomKey("stats-reset-needed-items")
    public List<ItemStack> statsResetItems = ItemUtils.parseItems("1 diamond");

    // ===== Player Lookup =====

    @Comment("")
    @Comment("Czy przy szukaniu danych o graczu ma byc pomijana wielkość znaków jego nicku")
    @CustomKey("player-lookup-ignorecase")
    public boolean playerLookupIgnorecase = false;

    @Override
    public OkaeriConfig load() throws OkaeriException {
        super.load();
        this.loadProcessedProperties();
        return this;
    }

    public void loadProcessedProperties() {
        if (this.rankSystem == RankSystem.Type.ELO) {
            this.eloConstants = new HashMap<>();

            NumberRange.parseIntegerRange(this.eloConstants_, false).forEach((key, value) -> {
                int constant = Option.attempt(NumberFormatException.class, () -> Integer.parseInt(value)).orElseGet(() -> {
                    FunnyGuilds.getPluginLogger().parser("\"" + value + "\" is not a valid elo constant!");
                    return 0;
                });

                this.eloConstants.put(key, constant);
            });
        }
    }

}
