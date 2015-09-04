package net.kaikk.mc.krules;

import java.util.regex.Matcher;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

class EventListener implements Listener {
	private KRules instance;
	
	EventListener(KRules instance) {
		this.instance = instance;
	}
	
	@EventHandler(ignoreCancelled=true)
	void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
		instance.ds.hasPlayerAgreedWithRules(event.getUniqueId()); // async cache the value
	}
	
	@EventHandler(ignoreCancelled=true)
	void onPlayerLogin(PlayerLoginEvent event) {
		if (!instance.ds.hasPlayerAgreedWithRules(event.getPlayer().getUniqueId())) {
			event.getPlayer().sendMessage(instance.rules+"\n"+instance.config.acceptRules);
		}
	}
	
	@EventHandler(ignoreCancelled=true)
	void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		if (!instance.ds.hasPlayerAgreedWithRules(event.getPlayer().getUniqueId())) {
			Matcher m = instance.allowedCommandsEx.matcher(event.getMessage());
			if(m.matches())
				return;
			event.setCancelled(true);
		}
	}
	
	@EventHandler(ignoreCancelled=true)
	void onPlayerAchievementAwarded(PlayerAchievementAwardedEvent event) {
		if (!instance.ds.hasPlayerAgreedWithRules(event.getPlayer().getUniqueId())) {
			event.getPlayer().sendMessage(instance.rules+"\n"+instance.config.acceptRules);
			event.setCancelled(true);
		}
	}


	@EventHandler(ignoreCancelled=true)
	void onPlayerBedEnter(PlayerBedEnterEvent event) {
		if (!instance.ds.hasPlayerAgreedWithRules(event.getPlayer().getUniqueId())) {
			event.getPlayer().sendMessage(instance.rules+"\n"+instance.config.acceptRules);
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled=true)
	void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
		if (!instance.ds.hasPlayerAgreedWithRules(event.getPlayer().getUniqueId())) {
			event.getPlayer().sendMessage(instance.rules+"\n"+instance.config.acceptRules);
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled=true)
	void onPlayerDropItem(PlayerDropItemEvent event) {
		if (!instance.ds.hasPlayerAgreedWithRules(event.getPlayer().getUniqueId())) {
			event.getPlayer().sendMessage(instance.rules+"\n"+instance.config.acceptRules);
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled=true)
	void onPlayerInteract(PlayerInteractEvent event) {
		if (!instance.ds.hasPlayerAgreedWithRules(event.getPlayer().getUniqueId())) {
			event.getPlayer().sendMessage(instance.rules+"\n"+instance.config.acceptRules);
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled=true)
	void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		if (!instance.ds.hasPlayerAgreedWithRules(event.getPlayer().getUniqueId())) {
			event.getPlayer().sendMessage(instance.rules+"\n"+instance.config.acceptRules);
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled=true)
	void onInventoryOpen(InventoryOpenEvent event) {
		if (!instance.ds.hasPlayerAgreedWithRules(event.getPlayer().getUniqueId())) {
			event.getPlayer().sendMessage(instance.rules+"\n"+instance.config.acceptRules);
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled=true)
	void onPlayerPickupItem(PlayerPickupItemEvent event) {
		if (!instance.ds.hasPlayerAgreedWithRules(event.getPlayer().getUniqueId())) {
			event.getPlayer().sendMessage(instance.rules+"\n"+instance.config.acceptRules);
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled=true)
	void onPlayerTeleport(PlayerTeleportEvent event) {
		if (!instance.ds.hasPlayerAgreedWithRules(event.getPlayer().getUniqueId())) {
			event.getPlayer().sendMessage(instance.rules+"\n"+instance.config.acceptRules);
			event.setCancelled(true);
		}
	}

}
