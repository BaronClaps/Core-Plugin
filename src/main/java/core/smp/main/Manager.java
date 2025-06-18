package core.smp.main;

import org.apache.maven.model.interpolation.MavenBuildTimestamp;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Manager {
    private final Map<UUID, String> playerCores = new HashMap<>();
    private final Map<UUID, Integer> playerTiers = new HashMap<>();
    private final Map<UUID, Integer> playerKills = new HashMap<>();
    private final Map<String, Long> cooldowns = new HashMap<>();
    private final Map<UUID, Integer> playerHits = new HashMap<>();
    private final Map<UUID, String> playerGUIs = new HashMap<>();
    private final Map<UUID, String> selectedCores = new HashMap<>();

    public void setCore(Player player, String core) {
        playerCores.put(player.getUniqueId(), core);
    }

    public String getCore(Player player) {
        return playerCores.getOrDefault(player.getUniqueId(), null);
    }

    public void setTier(Player player, int tier) {
        playerTiers.put(player.getUniqueId(), tier);
    }

    public int getTier(Player player) {
        return playerTiers.getOrDefault(player.getUniqueId(), 1);
    }

    public boolean isCooldownActive(Player player, String action, long cooldownTime) {
        String key = player.getUniqueId() + ":" + action;
        long now = System.currentTimeMillis();
        if (cooldowns.containsKey(key) && (now - cooldowns.get(key)) < cooldownTime) {
            return true;
        }
        cooldowns.put(key, now);
        return false;
    }

    public void setKills(Player player, int kills) {
        playerKills.put(player.getUniqueId(), kills);
    }

    public int getKills(Player player) {
        return playerKills.getOrDefault(player.getUniqueId(), 0);
    }

    public boolean isValidCore(String core) {
        return switch (core) {
            case "blaze", "phantom", "bat", "spider", "stray", "thunder", "wither", "bogged", "ender", "gambler" -> true;
            default -> false;
        };
    }

    public int getHits(Player player) {
        return playerHits.getOrDefault(player.getUniqueId(), 0);
    }

    public void setHits(Player player, int hits) {
        playerHits.put(player.getUniqueId(), hits);
    }

    public void setGUIState(Player player, String guiState) {
        playerGUIs.put(player.getUniqueId(), guiState);
    }

    public String getGUIState(Player player) {
        return playerGUIs.getOrDefault(player.getUniqueId(), null);
    }

    public void setSelectedCore(Player player, String core) {
        selectedCores.put(player.getUniqueId(), core);
    }

    public String getSelectedCore(Player player) {
        return selectedCores.getOrDefault(player.getUniqueId(), null);
    }

    public void clearGUIState(Player player) {
        playerGUIs.remove(player.getUniqueId());
        selectedCores.remove(player.getUniqueId());
    }

    public void givePlayerWeapon(Player player, Material material) {
        ItemStack sword = new ItemStack(material);
        ItemMeta swordMeta = sword.getItemMeta();
        swordMeta.setLore(List.of(ChatColor.GRAY + "A weapon imbued with the power of the " + getCore(player) + " core"));
        sword.setItemMeta(swordMeta);
        player.getInventory().addItem(sword);
    }

    public void updatePlayerWeapon(Player player) {
        int tier = getTier(player);
        Material material;

        player.getInventory().forEach(item -> {
            if (item != null && item.getItemMeta() != null && item.getItemMeta().getLore() != null &&
                    item.getItemMeta().getLore().contains(ChatColor.GRAY + "A weapon imbued with the power of the " + getCore(player) + " core")) {
                player.getInventory().remove(item);
            }
        });

        switch (tier) {
            case 1 -> material = Material.WOODEN_SWORD;
            case 2 -> material = Material.IRON_SWORD;
            case 3 -> material = Material.DIAMOND_SWORD;
            default -> {
                player.sendMessage(ChatColor.RED + "Invalid tier. Defaulting to Wooden Sword.");
                material = Material.WOODEN_SWORD;
            }
        }

        givePlayerWeapon(player, material);
    }

    public void changePlayerWeapon(Player player, int oldTier) {
        if (oldTier == 1) {
            ItemStack sword = new ItemStack(Material.WOODEN_SWORD);
            ItemMeta swordMeta = sword.getItemMeta();
            swordMeta.setLore(List.of(ChatColor.GRAY + "A weapon imbued with the power of the " + getCore(player) + " core"));
            sword.setItemMeta(swordMeta);
            player.getInventory().remove(sword);
            ItemStack nsword = new ItemStack(Material.IRON_SWORD);
            ItemMeta nswordMeta = nsword.getItemMeta();
            nswordMeta.setLore(List.of(ChatColor.GRAY + "A weapon imbued with the power of the " + getCore(player) + " core"));
            nsword.setItemMeta(nswordMeta);
            player.getInventory().addItem(nsword);
        }

        if (oldTier == 2) {
            ItemStack sword = new ItemStack(Material.IRON_SWORD);
            ItemMeta swordMeta = sword.getItemMeta();
            swordMeta.setLore(List.of(ChatColor.GRAY + "A weapon imbued with the power of the " + getCore(player) + " core"));
            sword.setItemMeta(swordMeta);
            player.getInventory().remove(sword);
            ItemStack nsword = new ItemStack(Material.DIAMOND_SWORD);
            ItemMeta nswordMeta = nsword.getItemMeta();
            nswordMeta.setLore(List.of(ChatColor.GRAY + "A weapon imbued with the power of the " + getCore(player) + " core"));
            nsword.setItemMeta(nswordMeta);
            player.getInventory().addItem(nsword);
        }
    }
}