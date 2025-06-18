package core.smp.main;

import org.bukkit.*;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.List;
import java.util.Objects;

public class Listen implements Listener {
    private final Manager coreManager;
    private final Abilities coreAbilities;

    public Listen(Manager coreManager, Abilities coreAbilities) {
        this.coreManager = coreManager;
        this.coreAbilities = coreAbilities;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if(event.getCause() == EntityDamageEvent.DamageCause.FALL && event.getDamager() instanceof Player && Objects.equals(coreManager.getCore((Player) event.getDamager()), "Spider")) {
            event.setCancelled(true);
        }

        if (event.getCause() == EntityDamageEvent.DamageCause.LIGHTNING && event.getEntity() instanceof Player && Objects.equals(coreManager.getCore((Player) event.getDamager()), "Thunder")) {
            event.setCancelled(true);
        }

        if (event.getDamager() instanceof Player && event.getEntity() instanceof LivingEntity) {
            Player attacker = (Player) event.getDamager();
            LivingEntity victim = (LivingEntity) event.getEntity();
            String core = coreManager.getCore(attacker);

            int hits = coreManager.getHits(attacker);
            coreManager.setHits(attacker, hits + 1);

            switch (core.toLowerCase()) {
                case "blaze" -> coreAbilities.triggerBlazeCore(attacker, victim);
                case "bat" -> coreAbilities.triggerBatCore(attacker);
                case "spider" -> coreAbilities.triggerSpiderCore(attacker, victim);
                case "stray" -> coreAbilities.triggerStrayCore(attacker, victim);
                case "thunder" -> coreAbilities.triggerThunderCore(attacker, victim);
                case "wither" -> coreAbilities.triggerWitherCore(attacker, victim);
                case "bogged" -> coreAbilities.triggerBoggedCore(attacker, victim);
                case "gambler" -> coreAbilities.triggerGamblerCore(attacker, victim);
            }
        }
    }

    @EventHandler
    public void onItemClick(PlayerInteractEvent ev){
        Player p = ev.getPlayer();
        if (ev.getAction() == Action.RIGHT_CLICK_AIR && ev.getItem() != null && ev.getItem().getItemMeta() != null && ev.getItem().getItemMeta().getDisplayName() != null && (ev.getItem().getType() == Material.DIAMOND_SWORD || ev.getItem().getType() == Material.IRON_SWORD || ev.getItem().getType() == Material.WOODEN_SWORD)) {
            if (coreManager.getCore(p).equalsIgnoreCase("phantom")) {
                coreAbilities.triggerPhantomDash(p);
            }

            if (coreManager.getCore(p).equalsIgnoreCase("spider")) {
                coreAbilities.triggerSpiderPull(p);
            }

            if (coreManager.getCore(p).equalsIgnoreCase("ender")) {
                coreAbilities.triggerEnderCore(p);
            }
        }
    }

    @EventHandler
    public void onPlayerHold(PlayerItemHeldEvent ev){
        ItemStack ite = ev.getPlayer().getActiveItem();

        if (ite != null && ite.getItemMeta() != null && ite.getItemMeta().getDisplayName() != null && (ite.getType() == Material.DIAMOND_SWORD || ite.getType() == Material.IRON_SWORD || ite.getType() == Material.WOODEN_SWORD)) {
            Particles.spawnParticleRing(
                    ev.getPlayer(),
                    Particle.DRIPPING_DRIPSTONE_LAVA,
                    1.0F,
                    100
            );
        }
    }

    @EventHandler
    public void onPlayerDeath(org.bukkit.event.entity.PlayerDeathEvent event) {
        Player attacker = event.getEntity().getKiller();
        int preKills = coreManager.getKills(event.getPlayer());

        if (attacker != null) {
            coreManager.setKills(attacker, coreManager.getKills(attacker) + 1);

            if (coreManager.getKills(attacker) == 5) {
                attacker.sendMessage(ChatColor.RED + "You have " + coreManager.getKills(attacker) + " kills and now are Tier 2");
                coreManager.updatePlayerWeapon(attacker);
                attacker.playSound(attacker.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0F, 1.0F);
                coreManager.setTier(attacker, 2);
            } else if (coreManager.getKills(attacker) == 10) {
                attacker.sendMessage(ChatColor.GOLD + "You have " + coreManager.getKills(attacker) + " kills and now are Tier 3");
                coreManager.updatePlayerWeapon(attacker);
                attacker.playSound(attacker.getLocation(), Sound.BLOCK_NOTE_BLOCK_IMITATE_ENDER_DRAGON, 1.0F, 1.0F);
                coreManager.setTier(attacker, 3);
            }

            coreManager.setKills(event.getPlayer(), coreManager.getKills(event.getPlayer()) - 1);

            if (preKills == 5 && coreManager.getKills(event.getPlayer()) < 5) {
                event.getPlayer().sendMessage(ChatColor.RED + "You have lost your Tier 2 status.");
                coreManager.updatePlayerWeapon(event.getPlayer());
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_ANVIL_USE, 1.0F, 1.0F);
                coreManager.setTier(event.getPlayer(), 1);
            } else if (preKills == 10 && coreManager.getKills(event.getPlayer()) < 10) {
                event.getPlayer().sendMessage(ChatColor.RED + "You have lost your Tier 3 status.");
                coreManager.updatePlayerWeapon(event.getPlayer());
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_IMITATE_ENDER_DRAGON, 1.0F, 1.0F);
                coreManager.setTier(event.getPlayer(), 2);
            }

            if (Objects.equals(coreManager.getCore(attacker), "STRAY")) {
                event.getPlayer().getNearbyEntities(3, 3, 3).forEach(entity -> {
                    if (entity instanceof LivingEntity && entity != attacker) {
                        ((LivingEntity) entity).addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.SLOWNESS, 60, 3));
                        event.getPlayer().spawnParticle(org.bukkit.Particle.DRAGON_BREATH, Objects.requireNonNull(event.getEntity().getLastDeathLocation()), 20, 0.5, 0.5, 0.5, 0.1);
                    }
                });
            }


        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (coreManager.getCore(player) == null) {
            openCoreSelectionGUI(player);
        } else {
            passive(player);
        }
    }

    void openCoreSelectionGUI(Player player) {

		Inventory inventory = Bukkit.createInventory(player, 9 * 3, ChatColor.BLACK + "Core Selection");

        ItemStack blazeCore = new ItemStack(Material.BLAZE_POWDER);
        ItemMeta blazeMeta = blazeCore.getItemMeta();
        blazeMeta.setDisplayName(ChatColor.YELLOW + "Blaze Core");
        blazeCore.setItemMeta(blazeMeta);

        ItemStack phantomCore = new ItemStack(Material.PHANTOM_MEMBRANE);
        ItemMeta phantomMeta = phantomCore.getItemMeta();
        phantomMeta.setDisplayName(ChatColor.GRAY + "Phantom Core");
        phantomCore.setItemMeta(phantomMeta);

        ItemStack batCore = new ItemStack(Material.REDSTONE);
        ItemMeta batMeta = batCore.getItemMeta();
        batMeta.setDisplayName(ChatColor.DARK_RED + "Bat Core");
        batCore.setItemMeta(batMeta);

        ItemStack spiderCore = new ItemStack(Material.SPIDER_EYE);
        ItemMeta spiderMeta = spiderCore.getItemMeta();
        spiderMeta.setDisplayName(ChatColor.DARK_GRAY + "Spider Core");
        spiderCore.setItemMeta(spiderMeta);

        ItemStack strayCore = new ItemStack(Material.SNOWBALL);
        ItemMeta strayMeta = strayCore.getItemMeta();
        strayMeta.setDisplayName(ChatColor.AQUA + "Stray Core");
        strayCore.setItemMeta(strayMeta);

        ItemStack thunderCore = new ItemStack(Material.LIGHTNING_ROD);
        ItemMeta thunderMeta = thunderCore.getItemMeta();
        thunderMeta.setDisplayName(ChatColor.BLUE + "Thunder Core");
        thunderCore.setItemMeta(thunderMeta);

        ItemStack witherCore = new ItemStack(Material.WITHER_ROSE);
        ItemMeta witherMeta = witherCore.getItemMeta();
        witherMeta.setDisplayName(ChatColor.DARK_PURPLE + "Wither Core");
        witherCore.setItemMeta(witherMeta);

        ItemStack boggedCore = new ItemStack(Material.RED_MUSHROOM);
        ItemMeta boggedMeta = boggedCore.getItemMeta();
        boggedMeta.setDisplayName(ChatColor.GREEN + "Bogged Core");
        boggedCore.setItemMeta(boggedMeta);

        ItemStack enderCore = new ItemStack(Material.ENDER_PEARL);
        ItemMeta enderMeta = enderCore.getItemMeta();
        enderMeta.setDisplayName(ChatColor.GRAY + "Ender Core");
        enderCore.setItemMeta(enderMeta);

        ItemStack gamblerCore = new ItemStack(Material.GOLD_INGOT);
        ItemMeta gamblerMeta = gamblerCore.getItemMeta();
        gamblerMeta.setDisplayName(ChatColor.GOLD + "Gambler Core");
        gamblerCore.setItemMeta(gamblerMeta);

        inventory.setItem(2, blazeCore);
        inventory.setItem(4, phantomCore);
        inventory.setItem(6, batCore);
        inventory.setItem(10, spiderCore);
        inventory.setItem(12, strayCore);
        inventory.setItem(14, thunderCore);
        inventory.setItem(16, witherCore);
        inventory.setItem(20, boggedCore);
        inventory.setItem(22, enderCore);
        inventory.setItem(24, gamblerCore);

        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.setDisplayName(ChatColor.GRAY + " ");
        filler.setItemMeta(fillerMeta);
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, filler);
            }
        }

		player.openInventory(inventory);
		player.setMetadata("OpenedMenu", new FixedMetadataValue(Main.getPlugin(Main.class), "Core Selection"));
    }

    @EventHandler
    public void onAnvilRename(PrepareAnvilEvent event) {
        ItemStack item = event.getResult();
        if (item != null && item.getItemMeta().getLore() != null && item.getItemMeta().getLore().contains(ChatColor.GRAY + "A weapon imbued with the power of")) {
            event.setResult(null);
        }
    }


    public void passive(Player player) {
        String core = coreManager.getCore(player);
        if (core.equals("Thunder")) {
            coreAbilities.passiveThunder(player);
        }
    }

    @EventHandler
    public void onPlayerRespawn(org.bukkit.event.player.PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        passive(player);
    }
}