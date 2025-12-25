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
public class GuildVaultConfiguration extends OkaeriConfig {

    @Comment("Czy skarbiec gildii ma być włączony")
    public boolean enabled = true;

    @Comment("")
    @Comment("Tytuł GUI głównego skarbca")
    @Comment("Dostępne zmienne: {TAG}, {GUILD}")
    public RawString title = new RawString("&6&lSKARBIEC GILDII {TAG}");

    @Comment("")
    @Comment("Ilość wierszy w GUI głównym (1-6)")
    public int rows = 3;

    @Comment("")
    @Comment("Maksymalna liczba stron magazynu przedmiotów")
    public int maxItemPages = 5;

    @Comment("")
    @Comment("Ilość przedmiotów na stronę (maksymalnie 45, pozostałe sloty na nawigację)")
    public int itemsPerPage = 45;

    @Comment("")
    @Comment("Konfiguracja itemu magazynu przedmiotów")
    public ItemsMenuItem itemsMenuItem = new ItemsMenuItem();

    @Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
    public static class ItemsMenuItem extends OkaeriConfig {

        @Comment("Slot itemu (0-53)")
        public int slot = 11;

        @Comment("")
        @Comment("Typ materiału itemu")
        public Material material = Material.CHEST;

        @Comment("")
        @Comment("Nazwa itemu")
        public RawString name = new RawString("&e&lMAGAZYN PRZEDMIOTÓW");

        @Comment("")
        @Comment("Opis itemu")
        @Comment("Dostępne zmienne: {ITEM-COUNT}")
        public List<RawString> lore = RawString.listOf(
                "&7Przechowuj przedmioty",
                "&7w skarbcu gildii!",
                "",
                "&7Przedmiotów: &e{ITEM-COUNT}",
                "",
                "&aKliknij, aby otworzyć!"
        );
    }

    @Comment("")
    @Comment("Konfiguracja itemu banku pieniędzy")
    public MoneyMenuItem moneyMenuItem = new MoneyMenuItem();

    @Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
    public static class MoneyMenuItem extends OkaeriConfig {

        @Comment("Slot itemu (0-53)")
        public int slot = 15;

        @Comment("")
        @Comment("Typ materiału itemu")
        public Material material = Material.GOLD_INGOT;

        @Comment("")
        @Comment("Nazwa itemu")
        public RawString name = new RawString("&6&lBANK GILDII");

        @Comment("")
        @Comment("Opis itemu")
        @Comment("Dostępne zmienne: {BALANCE}")
        public List<RawString> lore = RawString.listOf(
                "&7Zarządzaj finansami gildii!",
                "",
                "&7Stan konta: &6{BALANCE}$",
                "",
                "&aKliknij, aby otworzyć!"
        );
    }

    @Comment("")
    @Comment("Konfiguracja GUI magazynu przedmiotów")
    public ItemsVaultConfig itemsVault = new ItemsVaultConfig();

    @Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
    public static class ItemsVaultConfig extends OkaeriConfig {

        @Comment("Tytuł GUI magazynu przedmiotów")
        @Comment("Dostępne zmienne: {TAG}, {GUILD}, {PAGE}, {MAX-PAGES}")
        public RawString title = new RawString("&e&lMAGAZYN {TAG} &7[{PAGE}/{MAX-PAGES}]");

        @Comment("")
        @Comment("Slot przycisku poprzedniej strony")
        public int prevPageSlot = 48;

        @Comment("")
        @Comment("Slot przycisku następnej strony")
        public int nextPageSlot = 50;

        @Comment("")
        @Comment("Slot przycisku powrotu")
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

    @Comment("")
    @Comment("Konfiguracja GUI banku pieniędzy")
    public MoneyVaultConfig moneyVault = new MoneyVaultConfig();

    @Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
    public static class MoneyVaultConfig extends OkaeriConfig {

        @Comment("Tytuł GUI banku")
        @Comment("Dostępne zmienne: {TAG}, {GUILD}, {BALANCE}")
        public RawString title = new RawString("&6&lBANK {TAG} &7- &6{BALANCE}$");

        @Comment("")
        @Comment("Kwoty wpłat/wypłat")
        public List<Double> amounts = List.of(10.0, 50.0, 100.0, 500.0, 1000.0);

        @Comment("")
        @Comment("Slot itemu informacyjnego")
        public int infoSlot = 4;

        @Comment("")
        @Comment("Materiał itemu informacyjnego")
        public Material infoMaterial = Material.GOLD_BLOCK;

        @Comment("")
        @Comment("Nazwa itemu informacyjnego")
        @Comment("Dostępne zmienne: {BALANCE}")
        public RawString infoName = new RawString("&6&lSTAN KONTA: &e{BALANCE}$");

        @Comment("")
        @Comment("Opis itemu informacyjnego")
        public List<RawString> infoLore = RawString.listOf(
                "&7Użyj przycisków poniżej,",
                "&7aby wpłacić lub wypłacić pieniądze."
        );

        @Comment("")
        @Comment("Początkowy slot dla przycisków wpłat")
        public int depositStartSlot = 19;

        @Comment("")
        @Comment("Początkowy slot dla przycisków wypłat")
        public int withdrawStartSlot = 28;

        @Comment("")
        @Comment("Materiał przycisku wpłaty")
        public Material depositMaterial = Material.LIME_STAINED_GLASS_PANE;

        @Comment("")
        @Comment("Materiał przycisku wypłaty")
        public Material withdrawMaterial = Material.RED_STAINED_GLASS_PANE;

        @Comment("")
        @Comment("Format nazwy przycisku wpłaty")
        @Comment("Dostępne zmienne: {AMOUNT}")
        public RawString depositName = new RawString("&a+{AMOUNT}$");

        @Comment("")
        @Comment("Format nazwy przycisku wypłaty")
        @Comment("Dostępne zmienne: {AMOUNT}")
        public RawString withdrawName = new RawString("&c-{AMOUNT}$");

        @Comment("")
        @Comment("Slot przycisku powrotu")
        public int backSlot = 40;

        @Comment("")
        @Comment("Materiał przycisku powrotu")
        public Material backMaterial = Material.BARRIER;

        @Comment("")
        @Comment("Nazwa przycisku powrotu")
        public RawString backName = new RawString("&c&lPowrót");
    }

    @Comment("")
    @Comment("Uprawnienia skarbca dla rang")
    public VaultPermissions permissions = new VaultPermissions();

    @Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
    public static class VaultPermissions extends OkaeriConfig {

        @Comment("Czy członkowie mogą wpłacać przedmioty")
        public boolean memberCanDepositItems = true;

        @Comment("")
        @Comment("Czy członkowie mogą wypłacać przedmioty")
        public boolean memberCanWithdrawItems = false;

        @Comment("")
        @Comment("Czy członkowie mogą wpłacać pieniądze")
        public boolean memberCanDepositMoney = true;

        @Comment("")
        @Comment("Czy członkowie mogą wypłacać pieniądze")
        public boolean memberCanWithdrawMoney = false;

        @Comment("")
        @Comment("Czy zastępcy mają pełny dostęp (jak lider)")
        public boolean deputyFullAccess = true;
    }

    @Comment("")
    @Comment("Konfiguracja itemu powrotu (używane w GUI głównym)")
    public BackItem backItem = new BackItem();

    @Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
    public static class BackItem extends OkaeriConfig {

        @Comment("Czy item powrotu ma być włączony")
        public boolean enabled = true;

        @Comment("")
        @Comment("Slot itemu (0-53)")
        public int slot = 22;

        @Comment("")
        @Comment("Typ materiału itemu")
        public Material material = Material.ARROW;

        @Comment("")
        @Comment("Nazwa itemu")
        public RawString name = new RawString("&c&lPowrót do panelu");

        @Comment("")
        @Comment("Opis itemu")
        public List<RawString> lore = RawString.listOf(
                "&7Kliknij, aby wrócić",
                "&7do głównego panelu gildii."
        );
    }
}
