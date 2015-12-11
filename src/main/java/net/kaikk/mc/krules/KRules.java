package net.kaikk.mc.krules;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.spongepowered.api.Game;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Texts;



@Plugin(id = "KRules", name = "KRules", version = "1.0")
public class KRules {
	static KRules instance;
	Game game;
	Config config;
	DataStore ds;
	Map<UUID, Boolean> cache;
	Rules rules;
	
	@Listener
	public void onInitialization(GameInitializationEvent event) {
		instance=this;
		config=new Config(instance);
		game = event.getGame();
		cache=new HashMap<UUID, Boolean>();
		
		try {
			ds=new DataStore(this, config.getDbUrl(), config.getDbUsername(), config.getDbPassword());
		} catch (Exception e) {
			e.printStackTrace();
		}
		CommandSpec commandRules = CommandSpec.builder()
				.executor(new CommandRules(this))
				.build();
		game.getCommandManager().register(this, commandRules, "rules");
		game.getEventManager().registerListeners(this, new EventListener(instance));
	}
	
	@Listener
	public void onServerStart(GameStartedServerEvent event) {
		try {
			rules = new Rules(instance);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void acceptRules(CommandSource src) {
		if (!(src instanceof Player)) {
			src.sendMessage(Texts.of("KRules commands must be run by a player"));
			return;
		}
		Player p = (Player) src;
		if (!ds.hasPlayerAgreedWithRules(p.getUniqueId())) {
			cache.put(p.getUniqueId(), true);
			ds.addPlayerToAgreed(p.getUniqueId());
		}
		return;
	}
	
	public boolean isFakePlayer(UUID uuid) {
		for (Player player : game.getServer().getOnlinePlayers()) {
			if (player.getUniqueId().equals(uuid)) {
				return false;
			}
		}
		return true;
	}
	
	public boolean hasIntegersUpTo(List<Integer> list, int num) {
		for(int x = 1; x <= num; x++) {
			if (list.contains(x))
				continue;
			return false;
		}
		return true;
	}
	
	void log(String t) {
		log(Level.INFO, t);
	}
	
	void log(Level level, String t) {
		Logger.getGlobal().log(level, t, this);
	}

}
