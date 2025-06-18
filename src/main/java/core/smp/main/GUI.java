package core.smp.main;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class GUI implements Listener {

	private final Manager coreManager;
	private final Listen coreListener;
	public GUI(Manager coreManager, Listen coreListener) {
		this.coreManager = coreManager;
		this.coreListener = coreListener;
	}

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();

		if (player.hasMetadata("OpenedMenu")) {
			event.setCancelled(true);

			switch (event.getSlot()) {
				case 2:
					player.sendMessage("You selected the Blaze core.");
					coreManager.setCore(player, "Blaze");
					player.closeInventory();
					coreManager.updatePlayerWeapon(player);
					break;
				case 4:
					player.sendMessage("You selected the Phantom core.");
					coreManager.setCore(player, "Phantom");
					player.closeInventory();
					coreManager.updatePlayerWeapon(player);
					break;
				case 6:
					player.sendMessage("You selected the Bat core.");
					coreManager.setCore(player, "Bat");
					player.closeInventory();
					coreManager.updatePlayerWeapon(player);
					break;
				case 10:
					player.sendMessage("You selected the Spider core.");
					coreManager.setCore(player, "Spider");
					player.closeInventory();
					coreManager.updatePlayerWeapon(player);
					break;
				case 12:
					player.sendMessage("You selected the Stray core.");
					coreManager.setCore(player, "Stray");
					player.closeInventory();
					coreManager.updatePlayerWeapon(player);
					break;
				case 14:
					player.sendMessage("You selected the Thunder core.");
					coreManager.setCore(player, "Thunder");
					player.closeInventory();
					coreManager.updatePlayerWeapon(player);
					break;
				case 16:
					player.sendMessage("You selected the Wither core.");
					coreManager.setCore(player, "Wither");
					player.closeInventory();
					coreManager.updatePlayerWeapon(player);
					break;
				case 20:
					player.sendMessage("You selected the Bogged core.");
					coreManager.setCore(player, "Bogged");
					player.closeInventory();
					coreManager.updatePlayerWeapon(player);
					break;
				case 22:
					player.sendMessage("You selected the Ender core.");
					coreManager.setCore(player, "Ender");
					player.closeInventory();
					coreManager.updatePlayerWeapon(player);
					break;
				case 24:
					player.sendMessage("You selected the Gambler core.");
					coreManager.setCore(player, "Gambler");
					player.closeInventory();
					coreManager.updatePlayerWeapon(player);
					coreListener.passive(player);
					break;
				default:
					break;
			}
		}
	}

	@EventHandler
	public void onClose(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();

		if (player.hasMetadata("OpenedMenu"))
			player.removeMetadata("OpenedMenu", Main.getPlugin(Main.class));
	}
}