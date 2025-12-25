package net.dzikoysk.funnyguilds.config.sections;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import java.util.List;
import net.dzikoysk.funnyguilds.config.RawString;
import org.bukkit.Material;

@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class EventLogConfiguration extends OkaeriConfig {

    @Comment("Czy dziennik zdarzeń gildii ma być włączony")
    public boolean enabled = true;

    @Comment("")
    @Comment("Maksymalna liczba wpisów w dzienniku (starsze są usuwane)")
    public int maxEntries = 50;

    @Comment("")
    @Comment("Tytuł GUI dziennika zdarzeń")
    @Comment("Dostępne zmienne: {TAG}, {GUILD}, {PAGE}, {MAX-PAGES}")
    public RawString title = new RawString("&d&lDZIENNIK GILDII {TAG} &7[{PAGE}/{MAX-PAGES}]");

    @Comment("")
    @Comment("Ilość wierszy w GUI (1-6)")
    public int rows = 6;

    @Comment("")
    @Comment("Ilość wpisów na stronę")
    public int entriesPerPage = 45;

    @Comment("")
    @Comment("Format wyświetlania wpisu w GUI")
    @Comment("Dostępne zmienne: {TYPE}, {ACTOR}, {TARGET}, {DETAILS}, {DATE}, {TIME}")
    public RawString entryFormat = new RawString("&7{TYPE}");

    @Comment("")
    @Comment("Opis wpisu w GUI")
    @Comment("Dostępne zmienne: {TYPE}, {ACTOR}, {TARGET}, {DETAILS}, {DATE}, {TIME}")
    public List<RawString> entryLore = RawString.listOf(
            "&7Wykonał: &f{ACTOR}",
            "&7Cel: &f{TARGET}",
            "&7Szczegóły: &f{DETAILS}",
            "",
            "&8{DATE} {TIME}"
    );

    @Comment("")
    @Comment("Konfiguracja materiałów dla różnych typów zdarzeń")
    public EventMaterials eventMaterials = new EventMaterials();

    @Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
    public static class EventMaterials extends OkaeriConfig {

        @Comment("Materiał dla zdarzeń członków (dołączenie, opuszczenie, wyrzucenie)")
        public Material memberEvents = Material.PLAYER_HEAD;

        @Comment("")
        @Comment("Materiał dla zdarzeń uprawnień (lider, zastępca, uprawnienia)")
        public Material permissionEvents = Material.NAME_TAG;

        @Comment("")
        @Comment("Materiał dla zdarzeń skarbca (wpłaty, wypłaty)")
        public Material vaultEvents = Material.GOLD_INGOT;

        @Comment("")
        @Comment("Materiał dla zdarzeń dyplomacji (sojusze, wojny)")
        public Material diplomacyEvents = Material.SHIELD;

        @Comment("")
        @Comment("Materiał dla zdarzeń zarządzania gildią")
        public Material managementEvents = Material.BOOK;

        @Comment("")
        @Comment("Domyślny materiał")
        public Material defaultMaterial = Material.PAPER;
    }

    @Comment("")
    @Comment("Konfiguracja przycisku poprzedniej strony")
    public int prevPageSlot = 48;

    @Comment("")
    @Comment("Konfiguracja przycisku następnej strony")
    public int nextPageSlot = 50;

    @Comment("")
    @Comment("Konfiguracja przycisku powrotu")
    public int backSlot = 49;

    @Comment("")
    @Comment("Materiał przycisku poprzedniej strony")
    public Material prevPageMaterial = Material.ARROW;

    @Comment("")
    @Comment("Materiał przycisku następnej strony")
    public Material nextPageMaterial = Material.ARROW;

    @Comment("")
    @Comment("Materiał przycisku powrotu")
    public Material backMaterial = Material.BARRIER;

    @Comment("")
    @Comment("Nazwa przycisku poprzedniej strony")
    public RawString prevPageName = new RawString("&c← Poprzednia strona");

    @Comment("")
    @Comment("Nazwa przycisku następnej strony")
    public RawString nextPageName = new RawString("&aNastępna strona →");

    @Comment("")
    @Comment("Nazwa przycisku powrotu")
    public RawString backName = new RawString("&c&lPowrót");
}
