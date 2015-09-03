package net.kaikk.mc.krules;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import org.apache.commons.lang.StringUtils;
import org.bukkit.plugin.java.JavaPlugin;

public class KRules extends JavaPlugin {
	static KRules instance;
	Config config;
	DataStore ds;
	Map<UUID, Boolean> cache;
	String rules;
	
	@Override
	public void onEnable() {
		instance=this;

		config=new Config();
		try {
			File fRules = new File("plugins"+File.separator+"KRules"+File.separator+"rules.txt");
			if (!fRules.exists()) {
				fRules.createNewFile();
			}
			
			rules=StringUtils.join(Files.readAllLines(fRules.toPath(), StandardCharsets.UTF_8), "\n");
			
			ds=new DataStore(this, config.dbUrl, config.dbUsername, config.dbPassword);
			cache=new HashMap<UUID, Boolean>();
			
			this.getServer().getPluginManager().registerEvents(new EventListener(this), this);
			this.getCommand("krules").setExecutor(new CommandExec(this));
		} catch (Exception e) {
			e.printStackTrace();
			this.getPluginLoader().disablePlugin(this);
		}
	}
	
	void log(String t) {
		log(Level.INFO, t);
	}
	
	void log(Level level, String t) {
		this.getLogger().log(level, t);
	}
}
