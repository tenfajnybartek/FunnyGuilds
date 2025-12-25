package net.dzikoysk.funnyguilds.config;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;
import eu.okaeri.configs.exception.OkaeriException;
import java.io.File;
import java.util.UUID;
import java.util.logging.Logger;
import net.dzikoysk.funnyguilds.shared.Cooldown;

/**
 * A version of PluginConfiguration that loads its data from multiple split configuration files.
 * This class extends PluginConfiguration to maintain API compatibility while loading from split files.
 * 
 * Split files are stored in a config/ subdirectory:
 * - config/core.yml - Core plugin settings
 * - config/database.yml - Database settings
 * - config/gameplay.yml - Gameplay mechanics
 * - config/display.yml - Display and visual settings
 * - config/rank-system.yml - Ranking configuration
 * - config/commands.yml - Command configuration
 * - config/events.yml - Event settings
 * - config/panel.yml - Panel configurations
 * - config/security.yml - Security settings
 * - config/diplomacy.yml - Diplomacy settings
 */
public class SplitPluginConfiguration extends PluginConfiguration {

    @Exclude
    private final PluginConfigurationContainer container;
    
    @Exclude
    private final Logger logger;

    /**
     * Creates a SplitPluginConfiguration that loads from the given config directory.
     * 
     * @param configDir The directory containing split config files
     * @param logger Logger for error reporting
     */
    public SplitPluginConfiguration(File configDir, Logger logger) {
        this.logger = logger;
        this.container = ConfigurationFactory.createConfigurationContainer(configDir, logger);
        loadFromContainer();
    }

    /**
     * Loads all configuration values from the split config container.
     */
    private void loadFromContainer() {
        // Copy values from CoreConfig
        var core = container.core();
        this.pluginName = core.pluginName;
        this.debugMode = core.debugMode;
        this.updateInfo = core.updateInfo;
        this.updateNightlyInfo = core.updateNightlyInfo;
        this.defaultLocale = core.defaultLocale;
        this.availableLocales = core.availableLocales;
        this.guildsEnabled = core.guildsEnabled;
        this.dataInterval = core.dataInterval;
        this.disabledHooks = core.disabledHooks;

        // Copy values from DatabaseConfig
        var database = container.database();
        this.dataModel = database.dataModel;
        this.mysql = database.mysql;

        // Copy values from GameplayConfig  
        var gameplay = container.gameplay();
        this.regionsEnabled = gameplay.regionsEnabled;
        this.createNameLength = gameplay.createNameLength;
        this.createNameMinLength = gameplay.createNameMinLength;
        this.createTagLength = gameplay.createTagLength;
        this.createTagMinLength = gameplay.createTagMinLength;
        this.nameRegex = gameplay.nameRegex;
        this.tagRegex = gameplay.tagRegex;
        this.playerNameRegex = gameplay.playerNameRegex;
        this.playerNameMinLength = gameplay.playerNameMinLength;
        this.playerNameMaxLength = gameplay.playerNameMaxLength;
        this.minMembersToInclude = gameplay.minMembersToInclude;
        this.enableItemComponent = gameplay.enableItemComponent;
        this.createItems = gameplay.createItems;
        this.requiredExperience = gameplay.requiredExperience;
        this.requiredMoney = gameplay.requiredMoney;
        this.createItemsVip = gameplay.createItemsVip;
        this.requiredExperienceVip = gameplay.requiredExperienceVip;
        this.requiredMoneyVip = gameplay.requiredMoneyVip;
        this.rankCreateEnable = gameplay.rankCreateEnable;
        this.rankCreate = gameplay.rankCreate;
        this.rankCreateVip = gameplay.rankCreateVip;
        this.useCommonGUI = gameplay.useCommonGUI;
        this.guiItems_ = gameplay.guiItems_;
        this.guiItemsTitle = gameplay.guiItemsTitle;
        this.guiItemsVip_ = gameplay.guiItemsVip_;
        this.guiItemsVipTitle = gameplay.guiItemsVipTitle;
        this.guiItemsName = gameplay.guiItemsName;
        this.addLoreLines = gameplay.addLoreLines;
        this.guiItemsLore = gameplay.guiItemsLore;
        this.heart = gameplay.heart;
        this.createDistance = gameplay.createDistance;
        this.createMinDistanceFromBorder = gameplay.createMinDistanceFromBorder;
        this.blockedWorlds = gameplay.blockedWorlds;
        this.placingBlocksBypassOnRegion_ = gameplay.placingBlocksBypassOnRegion_;
        this.blockFlow = gameplay.blockFlow;
        this.respawnInBase = gameplay.respawnInBase;
        this.regionSize = gameplay.regionSize;
        this.regionMinDistance = gameplay.regionMinDistance;
        this.regionNotificationCooldown = gameplay.regionNotificationCooldown;
        this.regionCommands = gameplay.regionCommands;
        this.blockedInteract = gameplay.blockedInteract;
        this.maxMembersInGuild = gameplay.maxMembersInGuild;
        this.maxAlliesBetweenGuilds = gameplay.maxAlliesBetweenGuilds;
        this.maxEnemiesBetweenGuilds = gameplay.maxEnemiesBetweenGuilds;
        this.escapeEnable = gameplay.escapeEnable;
        this.escapeDelay = gameplay.escapeDelay;
        this.escapeSpawn = gameplay.escapeSpawn;
        this.baseEnable = gameplay.baseEnable;
        this.baseDelay = gameplay.baseDelay;
        this.baseDelayVip = gameplay.baseDelayVip;
        this.baseItems = gameplay.baseItems;
        this.joinItems = gameplay.joinItems;
        this.enlargeSize = gameplay.enlargeSize;
        this.enlargeItems = gameplay.enlargeItems;
        this.validityStart = gameplay.validityStart;
        this.validityTime = gameplay.validityTime;
        this.validityWhen = gameplay.validityWhen;
        this.validityItems = gameplay.validityItems;
        this.warEnabled = gameplay.warEnabled;
        this.warLives = gameplay.warLives;
        this.warProtection = gameplay.warProtection;
        this.warWait = gameplay.warWait;
        this.warTntProtection = gameplay.warTntProtection;
        this.animalsProtection = gameplay.animalsProtection;
        this.guildDeleteCancelIfSomeoneIsOnRegion = gameplay.guildDeleteCancelIfSomeoneIsOnRegion;
        this.tntProtection = gameplay.tntProtection;
        this.regionExplode = gameplay.regionExplode;
        this.regionExplodeBlockProtected = gameplay.regionExplodeBlockProtected;
        this.regionExplodeBlockTntDisabled = gameplay.regionExplodeBlockTntDisabled;
        this.regionExplodeExcludeEntities = gameplay.regionExplodeExcludeEntities;
        this.regionExplodeBlockBreaking = gameplay.regionExplodeBlockBreaking;
        this.regionExplodeBlockInteractions = gameplay.regionExplodeBlockInteractions;
        this.explodeRadius = gameplay.explodeRadius;
        this.explodeMaterials_ = gameplay.explodeMaterials_;
        this.explodeShouldAffectOnlyGuild = gameplay.explodeShouldAffectOnlyGuild;
        this.buggedBlocks = gameplay.buggedBlocks;
        this.buggedBlocksTimer = gameplay.buggedBlocksTimer;
        this.buggedBlocksExclude = gameplay.buggedBlocksExclude;
        this.buggedBlocksReturn = gameplay.buggedBlocksReturn;
        this.damageGuild = gameplay.damageGuild;
        this.damageAlly = gameplay.damageAlly;
        this.checkForRestrictedGuildNames = gameplay.checkForRestrictedGuildNames;
        this.whitelist = gameplay.whitelist;
        this.restrictedGuildNames = gameplay.restrictedGuildNames;
        this.restrictedGuildTags = gameplay.restrictedGuildTags;
        this.guildTagKeepCase = gameplay.guildTagKeepCase;
        this.guildTagUppercase = gameplay.guildTagUppercase;
        this.giveRewardsForFirstGuild = gameplay.giveRewardsForFirstGuild;
        this.firstGuildRewards = gameplay.firstGuildRewards;
        this.inviteCommandAllArgument = gameplay.inviteCommandAllArgument;
        this.inviteCommandAllArgumentIgnoreCase = gameplay.inviteCommandAllArgumentIgnoreCase;
        this.inviteCommandAllMaxRange = gameplay.inviteCommandAllMaxRange;
        this.inviteCommandAllDefaultRange = gameplay.inviteCommandAllDefaultRange;
        this.eventMove = gameplay.eventMove;

        // Copy values from DisplayConfig
        var display = container.display();
        this.top = display.top;
        this.pointsFormat = display.pointsFormat;
        this.ptopPoints = display.ptopPoints;
        this.gtopPoints = display.gtopPoints;
        this.killPointsChangeFormat = display.killPointsChangeFormat;
        this.pingFormat = display.pingFormat;
        this.ptopRespectVanish = display.ptopRespectVanish;
        this.ptopOnline = display.ptopOnline;
        this.ptopOffline = display.ptopOffline;
        this.gtopRespectVanish = display.gtopRespectVanish;
        this.gtopOnline = display.gtopOnline;
        this.gtopOffline = display.gtopOffline;
        this.scoreboard = display.scoreboard;
        this.chatPosition = display.chatPosition;
        this.chatPositionLeader = display.chatPositionLeader;
        this.chatPositionDeputy = display.chatPositionDeputy;
        this.chatPositionMember = display.chatPositionMember;
        this.chatGuild = display.chatGuild;
        this.chatRank = display.chatRank;
        this.chatPoints = display.chatPoints;
        this.chatPriv = display.chatPriv;
        this.chatAlly = display.chatAlly;
        this.chatGlobal = display.chatGlobal;
        this.chatPrivDesign = display.chatPrivDesign;
        this.chatAllyDesign = display.chatAllyDesign;
        this.chatGlobalDesign = display.chatGlobalDesign;
        this.chatSpyDesign = display.chatSpyDesign;
        this.logGuildChat = display.logGuildChat;
        this.infoPlayerEnabled = display.infoPlayerEnabled;
        this.infoPlayerCommand = display.infoPlayerCommand;
        this.infoPlayerCooldown = display.infoPlayerCooldown;
        this.infoPlayerSneaking = display.infoPlayerSneaking;
        this.displayNotificationForKiller = display.displayNotificationForKiller;
        this.displayNotificationForVictim = display.displayNotificationForVictim;
        this.displayNotificationForAssist = display.displayNotificationForAssist;
        this.regionEnterNotificationGuildMember = display.regionEnterNotificationGuildMember;
        this.translatedMaterialsEnable = display.translatedMaterialsEnable;
        this.useTranslatableComponentsForMaterials = display.useTranslatableComponentsForMaterials;
        this.translatedMaterials = display.translatedMaterials;
        this.itemAmountSuffix = display.itemAmountSuffix;

        // Copy values from RankSystemConfig
        var rankSystem = container.rankSystem();
        this.rankStart = rankSystem.rankStart;
        this.rankingUpdateInterval = rankSystem.rankingUpdateInterval;
        this.rankFarmingProtect = rankSystem.rankFarmingProtect;
        this.bidirectionalRankFarmingProtect = rankSystem.bidirectionalRankFarmingProtect;
        this.considerLastAttackerAsKiller = rankSystem.considerLastAttackerAsKiller;
        this.lastAttackerAsKillerConsiderationTimeout = rankSystem.lastAttackerAsKillerConsiderationTimeout;
        this.rankFarmingCooldown = rankSystem.rankFarmingCooldown;
        this.rankFarmingCooldownIP = rankSystem.rankFarmingCooldownIP;
        this.rankIPProtect = rankSystem.rankIPProtect;
        this.rankMemberProtect = rankSystem.rankMemberProtect;
        this.rankAllyProtect = rankSystem.rankAllyProtect;
        this.skipPrivilegedPlayersInRankPositions = rankSystem.skipPrivilegedPlayersInRankPositions;
        this.assistEnable = rankSystem.assistEnable;
        this.assistsLimit = rankSystem.assistsLimit;
        this.assistKillerShare = rankSystem.assistKillerShare;
        this.assistKillerAlwaysShare = rankSystem.assistKillerAlwaysShare;
        this.assistsRegionsIgnored = rankSystem.assistsRegionsIgnored;
        this.rankSystem = rankSystem.rankSystem;
        this.eloConstants_ = rankSystem.eloConstants_;
        this.eloDivider = rankSystem.eloDivider;
        this.eloExponent = rankSystem.eloExponent;
        this.percentRankChange = rankSystem.percentRankChange;
        this.staticAttackerChange = rankSystem.staticAttackerChange;
        this.staticVictimChange = rankSystem.staticVictimChange;
        this.rankResetItems = rankSystem.rankResetItems;
        this.statsResetItems = rankSystem.statsResetItems;
        this.playerLookupIgnorecase = rankSystem.playerLookupIgnorecase;

        // Copy values from EventsConfig
        var events = container.events();
        this.deathMessageReceivers = events.deathMessageReceivers;
        this.disableDefaultDeathMessage = events.disableDefaultDeathMessage;

        // Copy values from CommandsConfig
        var commands = container.commands();
        this.commands = commands.commands;

        // Copy values from PanelConfig
        var panel = container.panel();
        this.guildPanel = panel.guildPanel;
        this.permissionsPanel = panel.permissionsPanel;
        this.guildVault = panel.guildVault;
        this.eventLog = panel.eventLog;
        this.diplomacy = panel.diplomacy;

        // Copy values from SecurityConfig
        var security = container.security();
        this.securitySystem = security.securitySystem;

        // Load processed properties (like in original PluginConfiguration)
        this.loadProcessedProperties();
    }

    @Override
    public OkaeriConfig load() throws OkaeriException {
        // Reload all split configs
        container.reloadAll(logger);
        // Reload values from container
        loadFromContainer();
        return this;
    }

    /**
     * Gets the underlying configuration container.
     */
    public PluginConfigurationContainer getContainer() {
        return container;
    }

}
