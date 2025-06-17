package core.smp.main;

import org.bukkit.*;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
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
    public void onPlayerUse(EntityInteractEvent event) {
        if (event.getEntity() instanceof Player player) {
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.getType() == Material.STONE_SWORD || item.getType() == Material.STONE_AXE || item.getType() == Material.IRON_SWORD || item.getType() == Material.IRON_AXE || item.getType() == Material.DIAMOND_SWORD || item.getType() == Material.DIAMOND_AXE) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null && meta.lore() != null && meta.getLore().contains(ChatColor.GRAY + "A weapon imbued with the power of")) {
                    if (coreManager.getCore(player).equals("Phantom") || player.getName().equals("BaronClaps")) {
                        coreAbilities.triggerPhantomDash(player);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(org.bukkit.event.entity.PlayerDeathEvent event) {
        Player attacker = event.getEntity().getKiller();

        if (attacker != null) {
            coreManager.setKills(attacker, coreManager.getKills(attacker) + 1);

            if (coreManager.getKills(attacker) == 5) {
                attacker.sendMessage(ChatColor.RED + "You have " + coreManager.getKills(attacker) + " kills and now are Tier 2");
                attacker.playSound(attacker.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0F, 1.0F);
                coreManager.setTier(attacker, 2);
            } else if (coreManager.getKills(attacker) == 10) {
                attacker.sendMessage(ChatColor.GOLD + "You have " + coreManager.getKills(attacker) + " kills and now are Tier 3");
                attacker.playSound(attacker.getLocation(), Sound.BLOCK_NOTE_BLOCK_IMITATE_ENDER_DRAGON, 1.0F, 1.0F);
                coreManager.setTier(attacker, 3);
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

		Inventory inventory = Bukkit.createInventory(player, 9 * 3, ChatColor.DARK_BLUE + "Core Selection");

		ItemStack getDiamondButton = new ItemStack(Material.DIAMOND);
		ItemMeta diamondMeta = getDiamondButton.getItemMeta();
		diamondMeta.setDisplayName(ChatColor.AQUA + "Get Diamond");
		getDiamondButton.setItemMeta(diamondMeta);

		ItemStack clearInventoryButton = new ItemStack(Material.LAVA_BUCKET);
		ItemMeta clearInventoryMeta = clearInventoryButton.getItemMeta();
		clearInventoryMeta.setDisplayName(ChatColor.RED + "Clear Inventory");
		clearInventoryButton.setItemMeta(clearInventoryMeta);

		ItemStack clearWeatherButton = new ItemStack(Material.SUNFLOWER);
		ItemMeta clearWeatherMeta = clearWeatherButton.getItemMeta();
		clearWeatherMeta.setDisplayName(ChatColor.YELLOW + "Clear Weather");
		clearWeatherButton.setItemMeta(clearWeatherMeta);

		inventory.setItem(11, getDiamondButton);
		inventory.setItem(13, clearInventoryButton);
		inventory.setItem(15, clearWeatherButton);

        ItemStack sword = new ItemStack(Material.WOODEN_SWORD);
        ItemMeta swordMeta = sword.getItemMeta();
        swordMeta.setLore(List.of(ChatColor.GRAY + "A weapon imbued with the power of the " + coreManager.getCore(player) + " core"));
        sword.setItemMeta(swordMeta);

        ItemStack axe = new ItemStack(Material.WOODEN_AXE);
        ItemMeta axeMeta = axe.getItemMeta();
        axeMeta.setLore(List.of(ChatColor.GRAY + "A weapon imbued with the power of the " + coreManager.getCore(player) + " core"));
        axe.setItemMeta(axeMeta);

        inventory.setItem(21, axe);
        inventory.setItem(23, sword);

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

        scheduleGolemPassive(player);
    }
    @EventHandler
    public void onPlayerMove(org.bukkit.event.player.PlayerMoveEvent event) {
        Player player = event.getPlayer();
        String core = coreManager.getCore(player);
        if ("Spider".equals(core)) {
            coreAbilities.handleWallClimb(player); // Tier 2 and Tier 3: Wall climb
        }
    }

    @EventHandler
    public void onPlayerRespawn(org.bukkit.event.player.PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        passive(player);
    }

    public void scheduleGolemPassive(Player player) {
        Bukkit.getScheduler().runTaskTimer(Main.getPlugin(Main.class), () -> {
            if (coreManager.getCore(player).equals("Golem")) {
                coreAbilities.passiveGolem(player);
            }
        }, 0, 2L);
    }

}