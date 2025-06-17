package core.smp.main;

import org.bukkit.entity.Player;

import java.util.HashMap;
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
            case "blaze", "phantom", "bat", "spider", "stray", "thunder", "wither", "bogged", "golem", "gambler" -> true;
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
}