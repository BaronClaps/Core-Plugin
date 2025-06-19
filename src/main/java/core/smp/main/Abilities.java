package core.smp.main;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.util.ArrayList;

public class Abilities {
    private final Manager coreManager;

    public Abilities(Manager coreManager) {
        this.coreManager = coreManager;
    }

    public void triggerBlazeCore(Player attacker, LivingEntity victim) {
        int tier = coreManager.getTier(attacker);
        victim.setFireTicks(tier == 1 ? 40 : 80);
    }

    public void triggerBlazeExplode(Player attacker) {
        int tier = coreManager.getTier(attacker);
        LivingEntity victim = (LivingEntity) attacker.getEyeLocation().getNearbyEntities(10,10,10).stream()
                .filter(entity -> entity instanceof LivingEntity && entity != attacker)
                .findFirst().orElse(null);
        if (victim == null) {
            return;
        }
        long cooldownTime = 10000;
        if (!coreManager.isCooldownActive(attacker, "blaze", cooldownTime) && tier >= 3) {
            attacker.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 10, 4));
            victim.getWorld().createExplosion(victim.getLocation(), 5.0F, false, false);
            Particles.spawnTripleParticleRingsForTime(
                    (Player) victim,
                    Particle.SOUL_FIRE_FLAME,
                    1.0F,
                    50,
                    45.0,
                    0.5
            );
            attacker.setCooldown(Material.STICK, 10 * 20);
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

        player.setCooldown(Material.STICK, 100);
    }

    public void triggerBatCore(Player attacker) {
        int tier = coreManager.getTier(attacker);
        int goal = tier == 1 ? 2 : 3;
        int heal = tier == 3 ? 2 : 1;

        if (coreManager.isCooldownActive(attacker, "bat", goal * 1000)) {
            attacker.heal(heal);
        }
    }

    public void triggerBatSummon(Player player) {
        int tier = coreManager.getTier(player);
        if (tier >= 2 && !coreManager.isCooldownActive(player, "batsummon", tier == 3 ? 15000 : 30000)) {
            player.getWorld().getNearbyEntities(player.getLocation(), 10, 10, 10).forEach(entity -> {
                if (entity instanceof Player p && entity != player) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, tier == 2 ? 100: 200, 0));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, tier == 2 ? 60: 160, 0));

                    if (tier == 3) {
                        p.playSound(p.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 1.0F, 1.0F);
                        Particles.spawnTripleParticleRingsForTime(
                                p,
                                Particle.SQUID_INK,
                                1.0F,
                                100,
                                45.0,
                                10
                        );
                    }
                }
            });
            player.setCooldown(Material.STICK, tier == 3 ? (15 * 20) : (30 * 20));
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

    public void triggerStraySummon(Player attacker) {
        int tier = coreManager.getTier(attacker);
        long cooldownTime = tier == 3 ? 15000 : 30000;
        if (tier >= 2 && !coreManager.isCooldownActive(attacker, "straysummon", cooldownTime)) {
            attacker.getWorld().getNearbyEntities(attacker.getLocation(), 10, 10, 10).forEach(entity -> {
                if (entity instanceof Player && entity != attacker) {
                    Player p = (Player) entity;
                    for (int i = 0; i < 5; i++) {
                        LivingEntity stray = (LivingEntity) p.getWorld().spawnEntity(p.getLocation(), EntityType.STRAY);
                        if (stray instanceof org.bukkit.entity.Mob) {
                            ((org.bukkit.entity.Mob) stray).setTarget(p);
                        }
                    }
                    Particles.spawnParticleRingForTime(
                            p,
                            Particle.SNOWFLAKE,
                            1.0F,
                            50,
                            2
                    );
                    p.playSound(p.getLocation(), Sound.ENTITY_STRAY_AMBIENT, 1.0F, 1.0F);
                    if (tier == 3) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 2));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 0));
                    }
                }
            });
            attacker.setCooldown(Material.STICK, tier == 3 ? (15 * 20) : (30 * 20));
        }
    }

    public void triggerThunderStrike(Player attacker) {
        int tier = coreManager.getTier(attacker);
        long cooldownTime = tier == 3 ? 15000 : 30000;

        if (tier >= 2 && !coreManager.isCooldownActive(attacker, "thunder", cooldownTime)) {
            Particles.spawnParticleRingForTime(attacker, Particle.ANGRY_VILLAGER, 3, 50, 2);
            LivingEntity victim = (LivingEntity) attacker.getNearbyEntities(25,25,25).stream()
                    .filter(entity -> entity instanceof LivingEntity && entity != attacker)
                    .findFirst().orElse(null);

            if (victim != null)
                victim.getWorld().strikeLightning(victim.getLocation());
            else
                attacker.getWorld().strikeLightning(attacker.getLocation());

            if (tier == 3) {
                attacker.getNearbyEntities(10, 10, 10).forEach(entity -> {
                    if (entity instanceof LivingEntity && entity != victim && entity != attacker) {
                        Particles.spawnParticleLineForTime(victim.getLocation().add(0,1,0), entity.getLocation().add(0,1,0), org.bukkit.Particle.ELECTRIC_SPARK, 0.2, 1);
                        entity.getLocation().getWorld().strikeLightning(entity.getLocation());
                    }
                });
            }

            attacker.setCooldown(Material.STICK, tier == 3 ? (15 * 20) : (30 * 20));
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

    public void triggerWitherShoot(Player attacker) {
        int tier = coreManager.getTier(attacker);
        long cooldownTime = tier == 3 ? 10000 : 15000;

        LivingEntity victim = (LivingEntity) attacker.getEyeLocation().getNearbyEntities(10,10,10).stream()
                .filter(entity -> entity instanceof LivingEntity && entity != attacker)
                .findFirst().orElse(null);

        if (!coreManager.isCooldownActive(attacker, "wither_shoot", cooldownTime) && tier >= 2) {
            attacker.playSound(attacker.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1.0F, 1.0F);
            Particles.spawnParticleRingForTime(attacker, Particle.DAMAGE_INDICATOR, 1.0F, 50, 2);
            assert victim != null;
            victim.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 100, 1));
            for (int i = 0; i < 5; i++) {
                attacker.launchProjectile(org.bukkit.entity.WitherSkull.class, victim.getLocation().subtract(attacker.getLocation()).toVector().normalize().multiply(2));
            }
            attacker.setCooldown(Material.STICK, tier == 3 ? (10 * 20) : (15 * 20));
        }
    }

    public void triggerBoggedCore(Player attacker, LivingEntity victim) {
        int tier = coreManager.getTier(attacker);
        int goal = tier == 2 ? 4 : tier == 3 ? 3 : 5;

        if (!coreManager.isCooldownActive(attacker, "bogged", goal * 1000)) {
            victim.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 0));
            if (tier >= 2) {
                victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 0));
            }
            if (tier == 3) {
                victim.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 0));
            }
        }
    }

    public void triggerBoggedSummon(Player attacker) {
        int tier = coreManager.getTier(attacker);
        long cooldownTime = tier == 3 ? 15000 : 30000;
        if (tier >= 2 && !coreManager.isCooldownActive(attacker, "boggedsummon",  cooldownTime)) {
            attacker.getWorld().getNearbyEntities(attacker.getLocation(), 10, 10, 10).forEach(entity -> {
                if (entity instanceof Player && entity != attacker) {
                    Player p = (Player) entity;
                    for (int i = 0; i < 5; i++) {
                        LivingEntity bogged = (LivingEntity) p.getWorld().spawnEntity(p.getLocation(), EntityType.BOGGED);
                        if (bogged instanceof org.bukkit.entity.Mob) {
                            ((org.bukkit.entity.Mob) bogged).setTarget(p);
                        }
                    }
                    Particles.spawnParticleRingForTime(
                            p,
                            Particle.NOTE,
                            1.0F,
                            50,
                            2
                    );
                    p.playSound(p.getLocation(), Sound.ENTITY_WARDEN_EMERGE, 1.0F, 1.0F);
                    if (tier == 3) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 2));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 0));
                    }
                    attacker.setCooldown(Material.STICK, tier == 3 ? (15 * 20) : (30 * 20));
                }
            });
        }
    }

    public void triggerGamblerCore(Player attacker, LivingEntity victim) {
        int tier = coreManager.getTier(attacker);
        int chance = tier == 2 ? 35 : tier == 3 ? 50 : 10;

        if (Math.random() * 100 < chance) {
            applyRandomEffect(victim, tier);
        }
    }

    public void triggerGamblerLuck(Player player) {
        int tier = coreManager.getTier(player);
        int radius = tier == 3 ? 10 : 7;
        if (coreManager.isCooldownActive(player, "luck", 15000) || tier <= 1) {
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
            return;
        }
        player.getWorld().getNearbyEntities(player.getLocation(), radius, radius, radius).forEach(entity -> {
            if (entity instanceof Player && entity != player) {
                Player p = (Player) entity;
                p.getActivePotionEffects().forEach(effect -> p.removePotionEffect(effect.getType()));
                p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 100, 0));
                p.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 100, 0));
            }
        });
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.2F);
        player.setCooldown(Material.STICK, (15 * 20));

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

            player.getNearbyEntities(10, 10, 10).forEach(entity -> {
                if (entity instanceof Player && entity != player) {
                    entity.teleport(player.getLocation().add(player.getLocation().getDirection().normalize()));
                }
            });

            player.setCooldown(Material.STICK, tier == 3 ? (int) (2.5 * 20) : (5 * 20));
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

    public void thunderGod(Player player) {
        int floorY = (int) player.getLocation().getY();

        ArrayList<LivingEntity> nearbyEntities = new ArrayList<>();
        player.getNearbyEntities(50, 50, 50).forEach(entity -> {
            if (entity instanceof LivingEntity && entity != player) {
                nearbyEntities.add((LivingEntity) entity);
            }
        });

        int count = nearbyEntities.size();

        if (nearbyEntities.isEmpty()) {
            player.sendMessage("No entities nearby to strike.");
        } else {
            player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 400, 1));
            player.setInvulnerable(true);
            double angleIncrement = 2 * Math.PI / count;
            Location playerLoc = player.getLocation();
            for (int i = 0; i < count; i++) {
                double angle = i * angleIncrement;
                double x = 10 * Math.cos(angle);
                double z = 10 * Math.sin(angle);
                Location targetLoc = playerLoc.clone().add(x, 0, z);
                targetLoc.setY(floorY);
                targetLoc.setDirection(playerLoc.toVector().subtract(targetLoc.toVector()).normalize());
                LivingEntity entity = nearbyEntities.get(i);
                entity.teleport(targetLoc);
                entity.setAI(false);
                entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 9, false, false));
                entity.setGravity(false);
                if (entity.getType() == EntityType.PLAYER) {
                    Location baseLoc = entity.getLocation();
                    int[][] offsets = {
                            {1, 0, 0}, {-1, 0, 0}, {0, 0, 1}, {0, 0, -1},
                            {1, 1, 0}, {-1, 1, 0}, {0, 1, 1}, {0, 1, -1},
                            {0, -1, 0}
                    };
                    for (int[] offset : offsets) {
                        Location loc = baseLoc.clone().add(offset[0], offset[1], offset[2]);
                        loc.getBlock().setType(org.bukkit.Material.BARRIER);
                    }
                }
            }

            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0F, 1.0F);

            Location startLoc = player.getLocation().clone();
            player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 60, 1, false, false));

            new org.bukkit.scheduler.BukkitRunnable() {
                @Override
                public void run() {
                    if (!player.isOnline()) {
                        cancel();
                        return;
                    }
                    double deltaY = player.getLocation().getY() - startLoc.getY();
                    if (deltaY >= 4) {
                        player.removePotionEffect(PotionEffectType.LEVITATION);

                        org.bukkit.block.Block blockBelow = player.getLocation().subtract(0, 1, 0).getBlock();
                        blockBelow.setType(org.bukkit.Material.BARRIER);

                        for (LivingEntity entity : nearbyEntities) {
                            org.bukkit.util.Vector direction = player.getLocation().toVector().subtract(entity.getLocation().toVector()).normalize();
                            Location entityLoc = entity.getLocation();
                            entityLoc.setDirection(direction);
                            entity.teleport(entityLoc);
                        }

                        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1.0F, 1.0F);

                        nearbyEntities.removeIf(entity -> entity == null || !entity.isValid());

                        for (int i = 0; i < nearbyEntities.size(); i++) {
                            LivingEntity current = nearbyEntities.get(i);
                            LivingEntity left = nearbyEntities.get((i - 1 + nearbyEntities.size()) % nearbyEntities.size());
                            LivingEntity right = nearbyEntities.get((i + 1) % nearbyEntities.size());

                            Particles.spawnParticleLineForTime(
                                    current.getLocation().add(0, 1, 0),
                                    left.getLocation().add(0, 1, 0),
                                    org.bukkit.Particle.ELECTRIC_SPARK,
                                    0.2,
                                    1
                            );
                            Particles.spawnParticleLineForTime(
                                    current.getLocation().add(0, 1, 0),
                                    right.getLocation().add(0, 1, 0),
                                    org.bukkit.Particle.ELECTRIC_SPARK,
                                    0.2,
                                    1
                            );
                        }

                        new org.bukkit.scheduler.BukkitRunnable() {
                            @Override
                            public void run() {
                                for (LivingEntity entity : nearbyEntities) {
                                    for (int j = 0; j < 10; j++) {
                                        entity.getWorld().strikeLightning(entity.getLocation());
                                    }
                                }

                                org.bukkit.block.Block barrier = player.getLocation().subtract(0, 1, 0).getBlock();
                                if (barrier.getType() == org.bukkit.Material.BARRIER) {
                                    barrier.setType(org.bukkit.Material.AIR);
                                }

                                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 100, 0, false, false));


                                player.removePotionEffect(PotionEffectType.GLOWING);
                                player.setInvulnerable(false);

                                for(LivingEntity entity : nearbyEntities) {
                                    if (entity != null && entity.isValid()) {
                                        entity.setAI(true);
                                        entity.removePotionEffect(PotionEffectType.SLOWNESS);
                                        entity.setGravity(true);
                                    }
                                }
                            }
                        }.runTaskLater(Main.getPlugin(Main.class), 20);

                        cancel();
                    }
                }
            }.runTaskTimer(Main.getPlugin(Main.class), 0, 2);
        }
    }
}