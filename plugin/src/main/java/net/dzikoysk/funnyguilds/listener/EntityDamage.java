package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.damage.DamageManager;
import net.dzikoysk.funnyguilds.damage.DamageState;
import net.dzikoysk.funnyguilds.feature.combatlog.CombatManager;
import net.dzikoysk.funnyguilds.feature.combatlog.CombatNotificationHandler;
import net.dzikoysk.funnyguilds.feature.hooks.HookManager;
import net.dzikoysk.funnyguilds.feature.hooks.worldguard.WorldGuardHook.FriendlyFireStatus;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.shared.bukkit.EntityUtils;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.panda_lang.utilities.inject.annotations.Inject;
import panda.std.Option;

public class EntityDamage extends AbstractFunnyListener {

    @Inject
    private DamageManager damageManager;

    @Inject
    private CombatManager combatManager;

    @Inject
    private CombatNotificationHandler combatNotificationHandler;

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        EntityUtils.getAttacker(event.getDamager()).peek(attacker -> {
            Option<User> attackerUserOption = this.userManager.findByPlayer(attacker);
            if (attackerUserOption.isEmpty()) {
                return;
            }

            User attackerUser = attackerUserOption.get();
            Entity victim = event.getEntity();

            if (this.config.animalsProtection && (victim instanceof Animals || victim instanceof Villager)) {
                this.regionManager.findRegionAtLocation(victim.getLocation())
                        .map(Region::getGuild)
                        .filterNot(guild -> attackerUser.getGuild()
                                .map(it -> it.equals(guild))
                                .orElseGet(false))
                        .peek(guild -> event.setCancelled(true));

                return;
            }

            Option<User> victimOption = Option.of(victim)
                    .is(Player.class)
                    .flatMap(this.userManager::findByPlayer);

            if (victimOption.isEmpty()) {
                return;
            }

            User victimUser = victimOption.get();
            if (victimUser.hasGuild() && attackerUser.hasGuild()) {
                if (victimUser.getUUID().equals(attackerUser.getUUID())) {
                    return;
                }

                Guild victimGuild = victimUser.getGuild().get();
                Guild attackerGuild = attackerUser.getGuild().get();

                boolean shouldReturn = HookManager.WORLD_GUARD
                        .map(hook -> {
                            FriendlyFireStatus victimFriendlyFire = hook.getFriendlyFireStatus(victim.getLocation());
                            FriendlyFireStatus attackerFriendlyFire = hook.getFriendlyFireStatus(attacker.getLocation());

                            if (victimFriendlyFire == FriendlyFireStatus.ALLOW && attackerFriendlyFire == FriendlyFireStatus.ALLOW) {
                                return FriendlyFireStatus.ALLOW;
                            } else if (victimFriendlyFire == FriendlyFireStatus.DENY || attackerFriendlyFire == FriendlyFireStatus.DENY) {
                                return FriendlyFireStatus.DENY;
                            }

                            return FriendlyFireStatus.INHERIT;
                        })
                        .orElse(FriendlyFireStatus.INHERIT)
                        .map(friendlyFire -> {
                            if (friendlyFire == FriendlyFireStatus.ALLOW) {
                                return false;
                            }

                            if (victimGuild.equals(attackerGuild) && (!victimGuild.hasPvPEnabled() || friendlyFire == FriendlyFireStatus.DENY)) {
                                event.setCancelled(true);
                                return true;
                            }

                            if (victimGuild.isAlly(attackerGuild)) {
                                if (friendlyFire == FriendlyFireStatus.DENY) {
                                    event.setCancelled(true);
                                    return true;
                                }

                                if (!this.config.damageAlly) {
                                    event.setCancelled(true);
                                    return true;
                                }

                                if (!(attackerGuild.hasAllyPvPEnabled(victimGuild) && victimGuild.hasAllyPvPEnabled(attackerGuild))) {
                                    event.setCancelled(true);
                                    return true;
                                }
                            }

                            return false;
                        })
                        .get();

                if (shouldReturn) {
                    return;
                }
            }

            if (attacker.equals(victim)) {
                return;
            }

            if (!this.config.assistEnable || event.isCancelled()) {
                return;
            }

            if (HookManager.WORLD_GUARD.map(worldGuard -> worldGuard.isInNonAssistsRegion(victim.getLocation()))
                    .orElseGet(false)) {
                return;
            }

            DamageState damageState = this.damageManager.getDamageState(victimUser.getUUID());
            damageState.addDamage(attackerUser, event.getDamage());

            // Enter both players into combat if combat log is enabled
            if (this.config.combatLog.enabled && victim instanceof Player && attacker instanceof Player) {
                Player victimPlayer = (Player) victim;
                Player attackerPlayer = attacker;
                
                boolean wasInCombat = this.combatManager.isInCombat(victimUser.getUUID());
                
                // Enter victim into combat
                this.combatManager.enterCombat(victimUser, attackerUser, this.config.combatLog.duration);
                
                // Enter attacker into combat
                this.combatManager.enterCombat(attackerUser, victimUser, this.config.combatLog.duration);
                
                // Show notification only if entering combat for the first time
                if (!wasInCombat) {
                    this.combatNotificationHandler.showCombatStart(victimPlayer, attackerUser);
                }
                
                boolean attackerWasInCombat = this.combatManager.isInCombat(attackerUser.getUUID());
                if (!attackerWasInCombat) {
                    this.combatNotificationHandler.showCombatStart(attackerPlayer, victimUser);
                }
            }
        });
    }

}
