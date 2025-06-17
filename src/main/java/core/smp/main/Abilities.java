package core.smp.main;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class Abilities {
    private final Manager coreManager;

    public Abilities(Manager coreManager) {
        this.coreManager = coreManager;
    }

    public void triggerBlazeCore(Player attacker, LivingEntity victim) {
        int tier = coreManager.getTier(attacker);
        long cooldownTime = tier == 2 ? 5000 : 10000;

        if (!coreManager.isCooldownActive(attacker, "blaze", cooldownTime)) {
            victim.setFireTicks(tier == 1 ? 40 : 80);
            if (tier == 3) {
                attacker.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 40, 4));
                victim.getWorld().createExplosion(victim.getLocation(), 1.0F, false, false);
            }
        }
    }

    public void triggerPhantomDash(Player player) {
        int tier = coreManager.getTier(player);
        if (coreManager.isCooldownActive(player, "dash", 5000)) {
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
            return;
        }

        if(player.getName().equals("BaronClaps") || player.getName().equals("Shyha")) {
            player.setVelocity(player.getNearbyEntities(20,5,20).getFirst().getLocation().toVector().subtract(player.getLocation().toVector()).normalize().multiply(1.5));
        } else {
            player.setVelocity(player.getLocation().getDirection().multiply(1.5));
        }

        if (tier >= 2) {
            player.getNearbyEntities(3, 3, 3).forEach(entity -> {
                if (entity instanceof LivingEntity && entity != player) {
                    ((LivingEntity) entity).damage(4.0, player);
                    if (tier == 3) {
                        ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 60, 0));
                        ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 0));
                    }
                }
            });
        }
    }

    public void triggerBatCore(Player attacker) {
        int tier = coreManager.getTier(attacker);
        int goal = tier == 1 ? 2 : 3;
        int heal = tier == 3 ? 2 : 1;

        if (coreManager.isCooldownActive(attacker, "bat", goal * 1000)) {
            attacker.heal(heal);
        }
    }

    public void triggerStrayCore(Player attacker, LivingEntity victim) {
        int tier = coreManager.getTier(attacker);
        long cooldownTime = 5000;

        if (!coreManager.isCooldownActive(attacker, "stray", cooldownTime)) {
            victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, tier * 20, 0));
            if (tier >= 2) {
                victim.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, tier * 20, 0));
            }
        }
    }

    public void triggerThunderCore(Player attacker, LivingEntity victim) {
        int tier = coreManager.getTier(attacker);
        long cooldownTime = 30000;

        if (tier >= 2 && !coreManager.isCooldownActive(attacker, "thunder", cooldownTime)) {
            victim.getWorld().strikeLightning(victim.getLocation());

            if (tier == 3) {
                victim.getNearbyEntities(10, 3, 10).forEach(entity -> {
                    if (entity instanceof Player && entity != victim && entity != attacker) {

                        Particles.spawnAnimatedParticleLineForTime(victim.getLocation(), entity.getLocation(), org.bukkit.Particle.ELECTRIC_SPARK, 0.2, 5, 20);
                        entity.getLocation().getWorld().strikeLightning(entity.getLocation());
                    }
                });
            }
        }
    }

    public void triggerWitherCore(Player attacker, LivingEntity victim) {
        int tier = coreManager.getTier(attacker);
        victim.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, tier * 100, 0));
        if (tier == 3) {
            victim.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 100, 0));
            victim.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 100, 0));
        }
    }

    public void triggerBoggedCore(Player attacker, LivingEntity victim) {
        int tier = coreManager.getTier(attacker);
        int goal = tier == 2 ? 4 : tier == 3 ? 3 : 5;

        if (coreManager.isCooldownActive(attacker, "bogged", goal * 1000)) {
            victim.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 0));
            if (tier >= 2) {
                victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 0));
            }
            if (tier == 3) {
                victim.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 0));
            }
        }
    }

    public void triggerGamblerCore(Player attacker, LivingEntity victim) {
        int tier = coreManager.getTier(attacker);
        int chance = tier == 2 ? 35 : tier == 3 ? 50 : 10;

        if (Math.random() * 100 < chance) {
            applyRandomEffect(victim);
        }
    }

    private void applyRandomEffect(LivingEntity victim) {
        PotionEffectType[] effects = {
                PotionEffectType.SPEED, PotionEffectType.SLOWNESS, PotionEffectType.STRENGTH,
                PotionEffectType.WEAKNESS, PotionEffectType.HASTE, PotionEffectType.MINING_FATIGUE,
                PotionEffectType.JUMP_BOOST, PotionEffectType.INVISIBILITY, PotionEffectType.POISON,
                PotionEffectType.REGENERATION
        };

        PotionEffectType effect = effects[(int) (Math.random() * effects.length)];
        victim.addPotionEffect(new PotionEffect(effect, 100, 0));
    }

    public void passiveGolem(Player victim) {
        int tier = coreManager.getTier(victim);
        long cooldownTime = 30000;

        if (victim.getHealth() <= 8 && !coreManager.isCooldownActive(victim, "golem", cooldownTime)) {
            victim.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 100, 0));
            if (tier >= 2) {
                victim.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 1));
            }
        }

        if (tier == 3 && victim.getHealth() <= 12) {
            victim.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 100, 0));
        }
    }

    public void passiveThunder(Player player) {
        int tier = coreManager.getTier(player);
        if (tier >= 1) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false));
        }
    }

    public void triggerSpiderCore(Player player, LivingEntity victim) {
        int tier = coreManager.getTier(player);
        if (tier >= 3 && !coreManager.isCooldownActive(player, "spiderweb", 10000)) {
            victim.getWorld().getBlockAt(victim.getLocation()).setType(org.bukkit.Material.COBWEB);
        }
    }

    public void handleWallClimb(Player player) {
        int tier = coreManager.getTier(player);
        if (player.isSneaking() && tier >= 2) {
            if (player.getLocation().getBlock().getRelative(player.getFacing()).getType().isSolid()) {
                player.setVelocity(player.getLocation().getDirection().multiply(tier == 3 ? 0.5 : 0.3));
            }
        }
    }
}