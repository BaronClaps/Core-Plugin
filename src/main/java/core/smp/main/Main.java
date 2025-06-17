package core.smp.main;

import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.bukkit.BukkitLamp;

public class Main extends JavaPlugin {
    private Manager coreManager;
    private Abilities coreAbilities;
    private Listen coreListener;

    @Override
    public void onEnable() {
        coreManager = new Manager();
        coreAbilities = new Abilities(coreManager);
        coreListener = new Listen(coreManager, coreAbilities);

        getServer().getPluginManager().registerEvents(coreListener, this);
        var lamp = BukkitLamp.builder(this).build();
        lamp.register(new Commands(coreManager, coreListener));
        getLogger().info("Core SMP Plugin has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Core SMP plugin has been disabled!");
    }
}