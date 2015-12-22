package net.kaikk.mc.krules;

import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.kaikk.mc.uuidprovider.UUIDProvider;

public class KRules extends JavaPlugin {
	static KRules instance;
	Config config;
	DataStore ds;
	Map<UUID, Boolean> cache;
	Rules rules;
	Pattern allowedCommandsEx;
	
	@Override
	public void onEnable() {
		instance=this;
		
		config=new Config();
		try {
			File fRules = new File("plugins"+File.separator+"KRules"+File.separator+"rules.txt");
			if (!fRules.exists()) {
				PrintWriter writer = new PrintWriter(fRules.toPath().toString(), "UTF-8");
				writer.println("1:");
				writer.println("I am rule 1 on page 1");
				writer.println("I am rule 2 on page 1");
				writer.println("2:");
				writer.println("I am rule 3 on page 2");
				writer.println("I am rule 4 on page 2");
				writer.println("I am rule 5 on page 2");
				writer.close();
			}
			allowedCommandsEx = Pattern.compile("(^((/rules [0-9]+)|(/acceptrules)|(/rules)|(/krules.*))$)",Pattern.CASE_INSENSITIVE);
			rules = new Rules(Files.readAllLines(fRules.toPath(), StandardCharsets.UTF_8));

			
			ds=new DataStore(this, config.dbUrl, config.dbUsername, config.dbPassword);
			cache=new HashMap<UUID, Boolean>();
			
			this.getServer().getPluginManager().registerEvents(new EventListener(this), this);
			this.getCommand("krules").setExecutor(new CommandExec(this));
			this.getCommand("rules").setExecutor(new CommandExec(this));
			this.getCommand("acceptrules").setExecutor(new CommandExec(this));
		} catch (Exception e) {
			e.printStackTrace();
			this.getPluginLoader().disablePlugin(this);
		}
	}
	
	public boolean hasIntegersUpTo(List<Integer> list, int num)
	{
		for(int x = 1; x <= num; x++)
		{
			if(list.contains(x))
				continue;
			return false;
		}
		return true;
	}
	public UUID addPlayer(String name) {
		Player p = getOnlinePlayer(name);
		if(p == null) {
			UUID uuid = UUIDProvider.get(name);
			if(uuid == null) {
				return null;
			} else {
				if(addPlayer(uuid)) {
					return uuid;
				}
				return null;
			}
		}
		if(addPlayer(p.getUniqueId())) {
			return p.getUniqueId();
		}
		return null;
	}
	public boolean checkPlayer(String name) {
		Player p = getOnlinePlayer(name);
		if(p == null) {
			UUID uuid = UUIDProvider.get(name);
			if(uuid == null) {
				return false;
			} else {
				return checkPlayer(uuid);
			}
		}
		return checkPlayer(p.getUniqueId());
	}
	public UUID removePlayer(String name) {
		Player p = getOnlinePlayer(name);
		if(p == null) {
			UUID uuid = UUIDProvider.get(name);
			if(uuid == null) {
				return null;
			} else {
				if(removePlayer(uuid)) {
					return uuid;
				} else {
					return null;
				}
			}
		}
		if(removePlayer(p.getUniqueId())) {
			return p.getUniqueId();
		}
		return null;
	}
	public boolean addPlayer(UUID uuid) {
		if(checkPlayer(uuid)) {
			return true;
		}
		ds.addPlayerToAgreed(uuid);
		return true;
	}
	public boolean checkPlayer(UUID uuid) {
		return ds.hasPlayerAgreedWithRules(uuid, false);
	}
	public boolean removePlayer(UUID uuid) {
		if(checkPlayer(uuid)) {
			ds.removePlayerFromAgreed(uuid);
			return true;
		}
		return true;
	}
	@SuppressWarnings("unchecked")
	Player getOnlinePlayer(final String name) {
		Collection<Player> players = (Collection<Player>) this.getServer().getOnlinePlayers();
		
		Predicate<Player> filter = new Predicate<Player>() {
			@Override
			public boolean test(Player t) {
				if (t.getName() != name) {
					return true;
				}
				return false;
			}
		};
		List<Object> remaining = players.stream().filter(filter).collect(Collectors.toList());
		if(remaining.size() == 1) {
			return (Player) remaining.toArray()[0];
		}
		return null;		
	}
	void log(String t) {
		log(Level.INFO, t);
	}
	
	void log(Level level, String t) {
		this.getLogger().log(level, t);
	}

}
