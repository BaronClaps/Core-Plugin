package core.smp.main;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Abilities {
    private final Manager coreManager;

    public Abilities(Manager coreManager) {
        this.coreManager = coreManager;
    }

    public void triggerBlazeCore(Player attacker, LivingEntity victim) {
        int tier = coreManager.getTier(attacker);
        victim.setFireTicks(tier == 1 ? 40 : 80);
        long cooldownTime = tier == 2 ? 5000 : 10000;
    }

    public void triggerBlazeExplode(Player attacker, LivingEntity victim) {
        if (!coreManager.isCooldownActive(attacker, "blaze", cooldownTime)) {
            if (coreManager.getTier(attacker) == 3) {
                attacker.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 10, 4));
                victim.getWorld().createExplosion(victim.getLocation(), 8.0F, false, false);
                Particles.spawnTripleParticleRingsForTime(
                        (Player) victim,
                        Particle.SOUL_FIRE_FLAME,
                        1.0F,
                        50,
                        45.0,
                        0.5
                );
            }
        }
    }

    public void triggerPhantomDash(Player player) {
        int tier = coreManager.getTier(player);
        if (coreManager.isCooldownActive(player, "dash", 5000)) {
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
            return;
        }

        player.setVelocity(player.getLocation().getDirection().setY(Math.sin(Math.toRadians(-player.getLocation().getPitch()))).multiply(2.5));

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
            Particles.spawnParticleRingForTime(attacker, Particle.CLOUD, 3, 50, 5);
            victim.getWorld().strikeLightning(victim.getLocation());

            if (tier == 3) {
                victim.getNearbyEntities(10, 3, 10).forEach(entity -> {
                    if (entity instanceof Player && entity != victim && entity != attacker) {
                        Particles.spawnParticleLineForTime(victim.getLocation().add(0,1,0), entity.getLocation().add(0,1,0), org.bukkit.Particle.ELECTRIC_SPARK, 0.2, 1);
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
            applyRandomEffect(victim, tier);
        }
    }

    private void applyRandomEffect(LivingEntity victim, int tier) {
        PotionEffectType[] effects = {
                PotionEffectType.SPEED, PotionEffectType.SLOWNESS, PotionEffectType.STRENGTH,
                PotionEffectType.WEAKNESS, PotionEffectType.HASTE, PotionEffectType.MINING_FATIGUE,
                PotionEffectType.JUMP_BOOST, PotionEffectType.INVISIBILITY, PotionEffectType.POISON,
                PotionEffectType.REGENERATION
        };

        PotionEffectType effect = effects[(int) (Math.random() * effects.length)];
        victim.addPotionEffect(new PotionEffect(effect, 100, tier == 3 ? 1 : 0));
    }

    public void passiveThunder(Player player) {
        int tier = coreManager.getTier(player);
        if (tier >= 1) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false));
        }
    }

    public void triggerSpiderCore(Player player, LivingEntity victim) {
        int tier = coreManager.getTier(player);
        int cooldown = tier == 1 ? 10000 : 5000;
        if (tier >= 1 && !coreManager.isCooldownActive(player, "spiderweb", cooldown)) {
            victim.getWorld().getBlockAt(victim.getLocation()).setType(org.bukkit.Material.COBWEB);
        }
    }

    public void triggerSpiderPull(Player player) {
        int tier = coreManager.getTier(player);
        int cooldown = tier == 3 ? 2500 : 5000;

        if (tier >= 2) {
            if (coreManager.isCooldownActive(player, "pull", cooldown)) {
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                return;
            }

            player.getNearbyEntities(10, 3, 10).forEach(entity -> {
                if (entity instanceof Player && entity != player) {
                    entity.teleport(player.getLocation().add(player.getLocation().getDirection().normalize()));
                }
            });
        }
    }

    public void triggerEnderCore(Player player) {
        int tier = coreManager.getTier(player);
        long cooldownTime = tier == 3 ? 30000 : 60000;

        if (coreManager.isCooldownActive(player, "ender", cooldownTime)) {
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
            return;
        }

        Location targetLocation = player.getTargetBlockExact(50).getLocation().add(0, 1, 0);
        targetLocation.setDirection(player.getLocation().getDirection());
        targetLocation.setPitch(player.getLocation().getPitch());
        player.teleport(targetLocation);
        player.getWorld().spawnParticle(Particle.PORTAL, targetLocation, 50, 0.5, 0.5, 0.5, 0.1);
        player.playSound(targetLocation, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);

        if (tier >= 2) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, tier == 2 ? 100 : 200, 0));
        }
        if (tier == 3) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 0));
        }
    }
}