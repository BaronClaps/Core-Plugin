package core.smp.main;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Particles {
    public static void spawnParticleRing(Player player, Particle particle, double radius, int count) {
        double heightOffset = 0.5;
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
                ticks += 20;
            }
        }.runTaskTimer(Main.getPlugin(Main.class), 0, 20L);
    }

    public static void spawnTripleParticleRings(Player player, Particle particle, double radius, int count, double angleOffset) {
        double angleIncrement = 2 * Math.PI / count;

        for (int i = 0; i < count; i++) {
            double angle = i * angleIncrement;
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);

            // Centered around the player's body
            double yTop = 1.25; // Top of the player's body
            double yMiddle = 0.75; // Middle of the player's body
            double yBottom = 0.25; // Bottom of the player's body

            // Spawn particles at different heights
            player.getWorld().spawnParticle(particle, player.getLocation().add(x, yTop, z), 1);
            player.getWorld().spawnParticle(particle, player.getLocation().add(x, yMiddle, z), 1);
            player.getWorld().spawnParticle(particle, player.getLocation().add(x, yBottom, z), 1);

            // Spawn particles on each side of the player
            player.getWorld().spawnParticle(particle, player.getLocation().add(radius, yMiddle, 0), 1);
            player.getWorld().spawnParticle(particle, player.getLocation().add(-radius, yMiddle, 0), 1);
        }
    }

    public static void spawnTripleParticleRingsForTime(Player player, Particle particle, double radius, int count, double angleOffset, double time) {
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= time * 20) {
                    cancel();
                    return;
                }
                spawnTripleParticleRings(player, particle, radius, count, angleOffset);
                ticks += 20;
            }
        }.runTaskTimer(Main.getPlugin(Main.class), 0, 20L);
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
                ticks ++;
            }
        }.runTaskTimer(Main.getPlugin(Main.class), 0, 1);
    }
}
