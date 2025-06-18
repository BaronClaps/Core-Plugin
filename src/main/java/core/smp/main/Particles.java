package core.smp.main;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Particles {
    public static void spawnParticleRing(Player player, Particle particle, double radius, int count) {
        double heightOffset = -0.75;
        double angleIncrement = 2 * Math.PI / count;
        for (int i = 0; i < count; i++) {
            double angle = i * angleIncrement;
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);
            player.getWorld().spawnParticle(particle, player.getLocation().add(x, heightOffset, z), 1);
        }
    }

    public static void spawnParticleRingForTime(Player player, Particle particle, double radius, int count, int time) {
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= time * 20) {
                    cancel();
                    return;
                }
                spawnParticleRing(player, particle, radius, count);
                ticks++;
            }
        }.runTaskTimer(Main.getPlugin(Main.class), 0, 1);
    }

    public static void spawnAngledParticleRings(Player player, Particle particle, double radius, int count, double angleOffset) {
        double angleIncrement = 2 * Math.PI / count;
        double angleRadians = Math.toRadians(angleOffset);

        for (int i = 0; i < count; i++) {
            double angle = i * angleIncrement;
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);

            double y = x * Math.sin(angleRadians);
            x = x * Math.cos(angleRadians);

            player.getWorld().spawnParticle(particle, player.getLocation().add(x, y, z), 1);
        }
    }

    public static void spawnAngledParticleRingsForTime(Player player, Particle particle, double radius, int count, double angleOffset, int time) {
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= time * 20) {
                    cancel();
                    return;
                }
                spawnAngledParticleRings(player, particle, radius, count, angleOffset);
                ticks++;
            }
        }.runTaskTimer(Main.getPlugin(Main.class), 0, 1);
    }

    public static void spawnParticleLine(Location start, Location end, Particle particle, double step) {
        World world = start.getWorld();
        if (world == null || !world.equals(end.getWorld())) {
            throw new IllegalArgumentException("Start and end locations must be in the same world.");
        }

        org.bukkit.util.Vector direction = end.toVector().subtract(start.toVector()).normalize();
        double distance = start.distance(end);

        for (double i = 0; i <= distance; i += step) {
            Location point = start.clone().add(direction.clone().multiply(i));
            world.spawnParticle(particle, point, 1);
        }
    }

    public static void spawnParticleLineForTime(Location start, Location end, Particle particle, double step, int time) {
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= time * 20) {
                    cancel();
                    return;
                }
                spawnParticleLine(start, end, particle, step);
                ticks++;
            }
        }.runTaskTimer(Main.getPlugin(Main.class), 0, 1);
    }

    public static void spawnAnimatedParticleLineForTime(Location start, Location end, Particle particle, double step, int time, int delay) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Start or end location cannot be null.");
        }

        World world = start.getWorld();
        if (world == null || !world.equals(end.getWorld())) {
            throw new IllegalArgumentException("Start and end locations must be in the same world.");
        }

        if (particle == null) {
            throw new IllegalArgumentException("Particle type cannot be null.");
        }

        new BukkitRunnable() {
            int ticks = 0;
            double currentStep = 0;
            boolean animationComplete = false;

            @Override
            public void run() {
                if (ticks >= time * 20) {
                    cancel();
                    return;
                }

                if (!animationComplete) {
                    if (currentStep <= start.distance(end)) {
                        Location point = start.clone().add(end.toVector().subtract(start.toVector()).normalize().multiply(currentStep));
                        world.spawnParticle(particle, point, 1);
                        currentStep += step;
                    } else {
                        animationComplete = true;
                    }
                } else {
                    spawnParticleLine(start, end, particle, step);
                }

                ticks += delay;
            }
        }.runTaskTimer(Main.getPlugin(Main.class), 0, delay);
    }


}
