package core.smp.main;

import org.bukkit.Particle;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.Objects;

public class Commands {

    private final Manager coreManager;
    private final Listen coreListener;

    public Commands(Manager coreManager, Listen coreListener) {
        this.coreManager = coreManager;
        this.coreListener = coreListener;
    }

    @Command("core")
    public void core(BukkitCommandActor actor) {
        String core = coreManager.getCore(Objects.requireNonNull(actor.asPlayer()));
        if (core != null) {
            actor.reply("Your selected core is " + core + ".");
        } else {
            actor.reply("You have not selected a core yet.");
        }
    }

    @Command("kills")
    public void kills(Player player) {
        int kills = coreManager.getKills(player);
        player.sendMessage("You have " + kills + " kills.");
    }

    @Command("core")@Subcommand("set")@CommandPermission("minecraft.command.op")
    public void setCore(Player player, @Default("Blaze") String core) {
        if (!coreManager.isValidCore(core.toLowerCase())) {
            player.sendMessage("Invalid core. Available cores: Blaze, Phantom, Bat, Spider, Stray, Thunder, Wither, Bogged, Ender, Gambler.");
            return;
        }
        coreManager.setCore(player, core);
        coreManager.setTier(player, 1);
        coreListener.passive(player);
        player.sendMessage("You have selected the " + core + " core.");
    }

    @Command("tier")
    public void tier(BukkitCommandActor actor) {
        Player player = actor.asPlayer();
        assert player != null;
        int tier = coreManager.getTier(player);
        actor.reply("Your current tier is " + tier + ".");
    }

    @Command("tier")@Subcommand("set")@CommandPermission("minecraft.command.op")
    public void setTier(Player player, int tier) {
        if (tier < 1 || tier > 3) {
            player.sendMessage("Tier must be between 1 and 3.");
            return;
        }
        coreManager.setTier(player, tier);
        coreListener.passive(player);
        player.sendMessage("Your tier has been set to " + tier + ".");
    }

    @Command("tier")@Subcommand("reset")@CommandPermission("minecraft.command.op")
    public void resetTier(Player player) {
        coreManager.setTier(player, 1);
        player.sendMessage("Your tier has been reset to 1.");
    }

    @Command("core") @Subcommand("select")
    public void selectCore(Player player) {
        if (coreManager.getCore(player) != null) {
            player.sendMessage("You already have a core selected.");
            return;
        }
        player.sendMessage("Opening core selection GUI...");
        coreListener.openCoreSelectionGUI(player); // Directly call the method
    }

    @Command("core") @Subcommand("reset")@CommandPermission("minecraft.command.op")
    public void resetCore(Player sender, @Optional Player target) {
        if (target == null) {
            target = sender;
        }
        coreManager.setCore(target, null);
        coreManager.setTier(target, 1);
        coreManager.clearGUIState(target);
        sender.sendMessage("Reset core of " + target.getName() + ".");
        target.sendMessage("Your core has been reset. Please select a new core.");
        coreListener.openCoreSelectionGUI(target);
    }

    @Command("core") @Subcommand("start")@CommandPermission("minecraft.command.op")
    public void startCore(Player sender) {
        for (Player player : sender.getServer().getOnlinePlayers()) {
            coreManager.resetPlayerData(player);
            coreManager.setCore(player, null);
            coreManager.setTier(player, 1);
            coreManager.clearGUIState(player);
            sender.sendMessage("Setup core of " + player.getName() + ".");
            player.sendMessage("Please select a core.");
            coreListener.openCoreSelectionGUI(player);
        }
    }

    @Command("line")@CommandPermission("minecraft.command.op")
    public void line(Player player, Player target) {
        Particles.spawnParticleLineForTime(player.getLocation(), target.getLocation(), Particle.ELECTRIC_SPARK, 0.1, 2);
    }

    @Command("triple")@CommandPermission("minecraft.command.op")
    public void triple(Player player, @Optional String particleName) {
        Particles.spawnTripleParticleRingsForTime(player, Particle.SOUL_FIRE_FLAME, 3, 50, 0.1, 5);
    }
}