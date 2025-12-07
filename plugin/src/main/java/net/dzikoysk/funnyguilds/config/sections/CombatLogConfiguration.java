package net.dzikoysk.funnyguilds.config.sections;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.dzikoysk.funnyguilds.feature.combatlog.CombatNotificationType;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;

/**
 * Combat log system configuration
 */
public class CombatLogConfiguration extends OkaeriConfig {

    @Comment("Czy system combat log jest włączony")
    public boolean enabled = true;

    @Comment("")
    @Comment("Czas trwania stanu walki (w sekundach)")
    @Comment("Po tym czasie gracz może bezpiecznie się wylogować")
    public Duration duration = Duration.ofSeconds(30);

    @Comment("")
    @Comment("Typy powiadomień które mają być wyświetlane podczas walki")
    @Comment("Możliwe wartości: ACTIONBAR, BOSSBAR, TITLE, CHAT")
    @Comment("Można włączyć wiele naraz dodając je do listy")
    public List<CombatNotificationType> notificationTypes = Arrays.asList(
        CombatNotificationType.ACTIONBAR,
        CombatNotificationType.BOSSBAR
    );

    @Comment("")
    @Comment("Interwał aktualizacji powiadomień (w tickach, 20 ticków = 1 sekunda)")
    @Comment("Im mniejsza wartość tym częstsze aktualizacje ale większe obciążenie")
    public int notificationUpdateInterval = 20;

    @Comment("")
    @Comment("Konfiguracja BossBar")
    public BossBarSettings bossBar = new BossBarSettings();

    public static class BossBarSettings extends OkaeriConfig {

        @Comment("Styl BossBar")
        @Comment("Możliwe wartości: SOLID, SEGMENTED_6, SEGMENTED_10, SEGMENTED_12, SEGMENTED_20")
        public BarStyle style = BarStyle.SOLID;

        @Comment("")
        @Comment("Czy kolor BossBar ma się zmieniać w zależności od pozostałego czasu")
        public boolean progressiveColor = true;

        @Comment("")
        @Comment("Kolor BossBar gdy pozostało powyżej 60% czasu (lub jeśli progressiveColor = false)")
        @Comment("Możliwe wartości: PINK, BLUE, RED, GREEN, YELLOW, PURPLE, WHITE")
        public BarColor colorHigh = BarColor.RED;

        @Comment("")
        @Comment("Kolor BossBar gdy pozostało 30-60% czasu")
        public BarColor colorMedium = BarColor.YELLOW;

        @Comment("")
        @Comment("Kolor BossBar gdy pozostało poniżej 30% czasu")
        public BarColor colorLow = BarColor.GREEN;

    }

    @Comment("")
    @Comment("Konfiguracja dźwięków")
    public SoundSettings sounds = new SoundSettings();

    public static class SoundSettings extends OkaeriConfig {

        @Comment("Czy dźwięki są włączone")
        public boolean enabled = true;

        @Comment("")
        @Comment("Dźwięk przy rozpoczęciu walki")
        @Comment("Lista dźwięków: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Sound.html")
        public Sound startSound = Sound.ENTITY_ENDER_DRAGON_GROWL;

        @Comment("")
        @Comment("Głośność dźwięku rozpoczęcia (0.0 - 1.0)")
        public float startVolume = 1.0f;

        @Comment("")
        @Comment("Wysokość dźwięku rozpoczęcia (0.5 - 2.0)")
        public float startPitch = 1.0f;

        @Comment("")
        @Comment("Dźwięk przy zakończeniu walki")
        public Sound endSound = Sound.ENTITY_PLAYER_LEVELUP;

        @Comment("")
        @Comment("Głośność dźwięku zakończenia (0.0 - 1.0)")
        public float endVolume = 1.0f;

        @Comment("")
        @Comment("Wysokość dźwięku zakończenia (0.5 - 2.0)")
        public float endPitch = 1.0f;

    }

    @Comment("")
    @Comment("Konfiguracja blokady komend podczas walki")
    public CommandBlockSettings commandBlock = new CommandBlockSettings();

    public static class CommandBlockSettings extends OkaeriConfig {

        @Comment("Czy blokada komend jest włączona")
        public boolean enabled = true;

        @Comment("")
        @Comment("Lista komend zablokowanych podczas walki (bez slasha)")
        @Comment("Komendy sprawdzane są bez względu na wielkość liter")
        public List<String> blockedCommands = Arrays.asList(
            "spawn",
            "home",
            "tpa",
            "tpaccept",
            "warp",
            "back",
            "tp",
            "teleport"
        );

        @Comment("")
        @Comment("Czy sprawdzać tylko główną komendę (np. 'home') czy również z argumentami (np. 'home set')")
        public boolean checkMainCommandOnly = true;

    }

    @Comment("")
    @Comment("Czy gracz traci dodatkowe punkty za wylogowanie podczas walki")
    public boolean logoutPenalty = true;

    @Comment("")
    @Comment("Ilość punktów do odjęcia za wylogowanie podczas walki")
    @Comment("Działa tylko gdy logoutPenalty = true")
    public int logoutPenaltyPoints = 50;

    @Comment("")
    @Comment("Czy zabójca otrzymuje punkty gdy przeciwnik wyloguje się podczas walki")
    public boolean killerRewardOnLogout = true;

}
