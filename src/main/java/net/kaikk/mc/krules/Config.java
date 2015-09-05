package net.kaikk.mc.krules;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

class Config {
	final static String configFilePath = "plugins" + File.separator + "KRules" + File.separator + "config.yml";
	private File configFile;
	FileConfiguration config;
	
	String dbUrl;
	String dbUsername;
	String dbPassword;
	String acceptRules;
	String rulesAccepted;
	String readRules;
	
	Config() {
		this.configFile = new File(configFilePath);
		this.config = YamlConfiguration.loadConfiguration(this.configFile);
		this.load();
	}
	
	void load() {
		this.dbUrl=config.getString("dbUrl", "jdbc:mysql://127.0.0.1/krules");
		this.dbUsername=config.getString("dbUsername", "krules");
		this.dbPassword=config.getString("dbPassword", "");
		
		

		this.acceptRules=config.getString("readRules", "§2You must read all the rules. Use /rules # command.");
		this.acceptRules=config.getString("acceptRules", "§2You must accept the rules. Use /acceptrules command.");
		this.rulesAccepted=config.getString("rulesAccepted", "§3Rules accepted.");
		
		this.save();
	}
	
	void save() {
		try {
			this.config.set("dbUrl", this.dbUrl);
			this.config.set("dbUsername", this.dbUsername);
			this.config.set("dbPassword", this.dbPassword);
			
			this.config.set("acceptRules", this.acceptRules);
			this.config.set("rulesAccepted", this.rulesAccepted);

			this.config.save(this.configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}