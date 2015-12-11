package net.kaikk.mc.krules;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.achievement.GrantAchievementEvent;
import org.spongepowered.api.event.action.InteractEvent;
import org.spongepowered.api.event.action.MessageEvent;
import org.spongepowered.api.event.command.SendCommandEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.DisplaceEntityEvent;
import org.spongepowered.api.event.entity.TargetEntityEvent;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.Texts;


public class EventListener {
	

	
	private KRules instance;
	
	EventListener(KRules instance) {
		this.instance = instance;
	}
	
	
	@Listener(order=Order.FIRST, beforeModifications=false)
	public void onPlayerLogin(ClientConnectionEvent.Login event) {
		instance.ds.hasPlayerAgreedWithRules(event.getProfile().getUniqueId()); // async cache the value
	}
	
	@Listener(order=Order.FIRST, beforeModifications=false)
	public void onPlayerJoin(ClientConnectionEvent.Join event) {
		Player p = (Player) event.getTargetEntity();
		if (!instance.ds.hasPlayerAgreedWithRules(p.getUniqueId())) {
			if (instance.config.getForcePlayerToSpawnPoint()) {
				p.setLocation(p.getWorld().getSpawnLocation());
			}
			event.setMessage(Texts.of(instance.config.getReadRules()));
		}
	}
	 
	@Listener(order=Order.FIRST, beforeModifications=false)
	public void onSendCommandEvent(SendCommandEvent event) {
		List<String> allowedCommands = Arrays.asList(instance.config.getAllowedCommands().split("\\s*,\\s*"));
		if (event.getCause().root() != null) {
			Object cause = event.getCause().root().get();
			if (cause instanceof Player) {
				if (!allowedCommands.contains(event.getCommand())) {
					if (!instance.ds.hasPlayerAgreedWithRules(((Player)cause).getUniqueId())) {
						instance.log(Level.WARNING, "KRules blocked command:" + event.getCommand() + " issued by:" + cause + " To allow this command place it in the config.");
						event.setCancelled(true);
						event.setResult(CommandResult.empty());
						((Player) cause).sendMessage(Texts.of(instance.config.getReadRules()));
					}
				}
			}
		}
	}
	
	@Listener(order=Order.FIRST, beforeModifications=false)
	public void onGrantAchievementEvent(GrantAchievementEvent event) {
		Player p = (Player) ((TargetEntityEvent) event).getTargetEntity();
		if (!instance.ds.hasPlayerAgreedWithRules(p.getUniqueId())) {
			p.sendMessage(Texts.of(instance.config.getReadRules()));
			event.setCancelled(true);
		}
	}

	@Listener(order=Order.FIRST, beforeModifications=false)
	public void onMessageEvent(MessageEvent event) {
		if (event.getCause().isEmpty()) {
			return;
		}
		if (event.getCause().root().get() instanceof Player)
		{
			Player p = (Player) event.getCause().root().get();
			if (!instance.ds.hasPlayerAgreedWithRules(p.getUniqueId())) {
				p.sendMessage(Texts.of(instance.config.getReadRules()));
				((Cancellable) event).setCancelled(true);
			}
		}
	}

	@Listener(order=Order.FIRST, beforeModifications=false)
	public void onInteractInventoryEvent(InteractInventoryEvent.Open event) {
		if (event.getCause().root() != null) {
			if (event.getCause().root().get() instanceof Player) {
				Player p = (Player) event.getCause().root().get();
				if (!instance.ds.hasPlayerAgreedWithRules(p.getUniqueId())) {
					p.sendMessage(Texts.of(instance.config.getReadRules()));
					event.setCancelled(true);
				}
			}
		}
	}

	@Listener(order=Order.FIRST, beforeModifications=false)
	public void onInteract(InteractEvent event) {
		if (event.getCause().root() != null) {
			if (event.getCause().root().get() instanceof Player) {
				Player p = (Player) event.getCause().root().get();
				if (!instance.ds.hasPlayerAgreedWithRules(p.getUniqueId())) {
					p.sendMessage(Texts.of(instance.config.getReadRules()));
					event.setCancelled(true);
				}
			}
		}
	}

	@Listener(order=Order.FIRST, beforeModifications=false)
	public void onChangeInventoryEvent(ChangeInventoryEvent event) {
		if (event.getCause().root() != null) {
			if (event.getCause().root().get() instanceof Player) {
				Player p = (Player) event.getCause().root().get();
				if (!instance.ds.hasPlayerAgreedWithRules(p.getUniqueId())) {
					p.sendMessage(Texts.of(instance.config.getReadRules()));
					event.setCancelled(true);
				}
			}
		}
	}

	@Listener(order=Order.FIRST, beforeModifications=false)
	public void onPlayerTeleport(DisplaceEntityEvent.Teleport.TargetPlayer event) {
		if (event.getCause().root() != null) {
			if (event.getCause().root().get() instanceof Player) {
				Player p = (Player) event.getCause().root().get();
				if (!instance.ds.hasPlayerAgreedWithRules(p.getUniqueId())) {
					p.sendMessage(Texts.of(instance.config.getReadRules()));
					event.setCancelled(true);
				}
			}
		}
	}
	
	@Listener(order=Order.FIRST, beforeModifications=false)
	public void onDamangeEntityEvent(DamageEntityEvent event) {
		if (event.getTargetEntity() instanceof Player) {
			Player p = (Player) event.getTargetEntity();
			if (!instance.ds.hasPlayerAgreedWithRules(p.getUniqueId())) {
				event.setCancelled(true);
			}
		}
	}
}
