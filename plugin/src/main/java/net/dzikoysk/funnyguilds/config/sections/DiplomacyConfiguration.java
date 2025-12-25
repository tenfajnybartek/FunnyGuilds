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
public class DiplomacyConfiguration extends OkaeriConfig {

    @Comment("Czy panel dyplomacji ma być włączony")
    public boolean enabled = true;

    @Comment("")
    @Comment("Tytuł GUI panelu dyplomacji")
    @Comment("Dostępne zmienne: {TAG}, {GUILD}")
    public RawString title = new RawString("&b&lDYPLOMACJA GILDII {TAG}");

    @Comment("")
    @Comment("Ilość wierszy w GUI głównym (1-6)")
    public int rows = 4;

    @Comment("")
    @Comment("Konfiguracja itemu listy sojuszy")
    public AlliesMenuItem alliesMenuItem = new AlliesMenuItem();

    @Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
    public static class AlliesMenuItem extends OkaeriConfig {

        @Comment("Slot itemu (0-53)")
        public int slot = 11;

        @Comment("")
        @Comment("Typ materiału itemu")
        public Material material = Material.EMERALD;

        @Comment("")
        @Comment("Nazwa itemu")
        public RawString name = new RawString("&a&lSOJUSZE");

        @Comment("")
        @Comment("Opis itemu")
        @Comment("Dostępne zmienne: {ALLIES-COUNT}, {MAX-ALLIES}")
        public List<RawString> lore = RawString.listOf(
                "&7Zarządzaj sojuszami gildii!",
                "",
                "&7Aktywne sojusze: &a{ALLIES-COUNT}&7/&a{MAX-ALLIES}",
                "",
                "&aKliknij, aby otworzyć!"
        );
    }

    @Comment("")
    @Comment("Konfiguracja itemu listy wrogów")
    public EnemiesMenuItem enemiesMenuItem = new EnemiesMenuItem();

    @Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
    public static class EnemiesMenuItem extends OkaeriConfig {

        @Comment("Slot itemu (0-53)")
        public int slot = 15;

        @Comment("")
        @Comment("Typ materiału itemu")
        public Material material = Material.IRON_SWORD;

        @Comment("")
        @Comment("Nazwa itemu")
        public RawString name = new RawString("&c&lWOJNY");

        @Comment("")
        @Comment("Opis itemu")
        @Comment("Dostępne zmienne: {ENEMIES-COUNT}, {MAX-ENEMIES}")
        public List<RawString> lore = RawString.listOf(
                "&7Zarządzaj wojnami gildii!",
                "",
                "&7Aktywne wojny: &c{ENEMIES-COUNT}&7/&c{MAX-ENEMIES}",
                "",
                "&aKliknij, aby otworzyć!"
        );
    }

    @Comment("")
    @Comment("Konfiguracja itemu zaproszeń sojuszniczych")
    public InvitationsMenuItem invitationsMenuItem = new InvitationsMenuItem();

    @Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
    public static class InvitationsMenuItem extends OkaeriConfig {

        @Comment("Slot itemu (0-53)")
        public int slot = 13;

        @Comment("")
        @Comment("Typ materiału itemu")
        public Material material = Material.WRITABLE_BOOK;

        @Comment("")
        @Comment("Nazwa itemu")
        public RawString name = new RawString("&e&lZAPROSZENIA SOJUSZNICZE");

        @Comment("")
        @Comment("Opis itemu")
        @Comment("Dostępne zmienne: {INVITATIONS-COUNT}")
        public List<RawString> lore = RawString.listOf(
                "&7Przeglądaj oczekujące",
                "&7zaproszenia do sojuszu!",
                "",
                "&7Oczekujące: &e{INVITATIONS-COUNT}",
                "",
                "&aKliknij, aby otworzyć!"
        );
    }

    @Comment("")
    @Comment("Konfiguracja GUI listy sojuszy")
    public AlliesListConfig alliesList = new AlliesListConfig();

    @Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
    public static class AlliesListConfig extends OkaeriConfig {

        @Comment("Tytuł GUI listy sojuszy")
        @Comment("Dostępne zmienne: {TAG}, {GUILD}")
        public RawString title = new RawString("&a&lSOJUSZE GILDII {TAG}");

        @Comment("")
        @Comment("Format nazwy itemu sojusznika")
        @Comment("Dostępne zmienne: {ALLY-TAG}, {ALLY-NAME}")
        public RawString allyItemName = new RawString("&a{ALLY-TAG} &7- &f{ALLY-NAME}");

        @Comment("")
        @Comment("Opis itemu sojusznika")
        @Comment("Dostępne zmienne: {ALLY-TAG}, {ALLY-NAME}, {ALLY-MEMBERS}, {PVP-STATUS}")
        public List<RawString> allyItemLore = RawString.listOf(
                "&7Członków: &a{ALLY-MEMBERS}",
                "&7PvP sojuszu: {PVP-STATUS}",
                "",
                "&eKliknij LPM, aby przełączyć PvP",
                "&cKliknij PPM, aby rozwiązać sojusz"
        );

        @Comment("")
        @Comment("Materiał itemu sojusznika")
        public Material allyMaterial = Material.EMERALD;
    }

    @Comment("")
    @Comment("Konfiguracja GUI listy wrogów")
    public EnemiesListConfig enemiesList = new EnemiesListConfig();

    @Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
    public static class EnemiesListConfig extends OkaeriConfig {

        @Comment("Tytuł GUI listy wrogów")
        @Comment("Dostępne zmienne: {TAG}, {GUILD}")
        public RawString title = new RawString("&c&lWOJNY GILDII {TAG}");

        @Comment("")
        @Comment("Format nazwy itemu wroga")
        @Comment("Dostępne zmienne: {ENEMY-TAG}, {ENEMY-NAME}")
        public RawString enemyItemName = new RawString("&c{ENEMY-TAG} &7- &f{ENEMY-NAME}");

        @Comment("")
        @Comment("Opis itemu wroga")
        @Comment("Dostępne zmienne: {ENEMY-TAG}, {ENEMY-NAME}, {ENEMY-MEMBERS}, {ENEMY-LIVES}")
        public List<RawString> enemyItemLore = RawString.listOf(
                "&7Członków: &c{ENEMY-MEMBERS}",
                "&7Żyć: &c{ENEMY-LIVES}",
                "",
                "&cKliknij, aby zakończyć wojnę"
        );

        @Comment("")
        @Comment("Materiał itemu wroga")
        public Material enemyMaterial = Material.IRON_SWORD;
    }

    @Comment("")
    @Comment("Konfiguracja GUI zaproszeń sojuszniczych")
    public InvitationsListConfig invitationsList = new InvitationsListConfig();

    @Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
    public static class InvitationsListConfig extends OkaeriConfig {

        @Comment("Tytuł GUI zaproszeń")
        @Comment("Dostępne zmienne: {TAG}, {GUILD}")
        public RawString title = new RawString("&e&lZAPROSZENIA SOJUSZNICZE {TAG}");

        @Comment("")
        @Comment("Format nazwy itemu zaproszenia")
        @Comment("Dostępne zmienne: {INVITER-TAG}, {INVITER-NAME}")
        public RawString invitationItemName = new RawString("&e{INVITER-TAG} &7- &f{INVITER-NAME}");

        @Comment("")
        @Comment("Opis itemu zaproszenia")
        @Comment("Dostępne zmienne: {INVITER-TAG}, {INVITER-NAME}, {INVITER-MEMBERS}")
        public List<RawString> invitationItemLore = RawString.listOf(
                "&7Członków: &e{INVITER-MEMBERS}",
                "",
                "&aKliknij LPM, aby przyjąć",
                "&cKliknij PPM, aby odrzucić"
        );

        @Comment("")
        @Comment("Materiał itemu zaproszenia")
        public Material invitationMaterial = Material.PAPER;
    }

    @Comment("")
    @Comment("Konfiguracja przycisku powrotu (dla wszystkich GUI dyplomacji)")
    public int backSlot = 49;

    @Comment("")
    @Comment("Materiał przycisku powrotu")
    public Material backMaterial = Material.BARRIER;

    @Comment("")
    @Comment("Nazwa przycisku powrotu")
    public RawString backName = new RawString("&c&lPowrót");

    @Comment("")
    @Comment("Konfiguracja itemu w panelu głównym gildii")
    public PanelIconItem panelIcon = new PanelIconItem();

    @Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
    public static class PanelIconItem extends OkaeriConfig {

        @Comment("Czy item dyplomacji ma być wyświetlany w panelu gildii")
        public boolean enabled = true;

        @Comment("")
        @Comment("Slot itemu (0-53)")
        public int slot = 28;

        @Comment("")
        @Comment("Typ materiału itemu")
        public Material material = Material.SHIELD;

        @Comment("")
        @Comment("Nazwa itemu")
        public RawString name = new RawString("&b&lDYPLOMACJA");

        @Comment("")
        @Comment("Opis itemu")
        public List<RawString> lore = RawString.listOf(
                "&7Zarządzaj sojuszami",
                "&7i wojnami gildii!",
                "",
                "&aKliknij, aby otworzyć!"
        );
    }
}
