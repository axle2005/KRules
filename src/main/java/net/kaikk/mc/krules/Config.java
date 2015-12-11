package net.kaikk.mc.krules;

import java.io.File;
import java.io.IOException;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;

import com.google.inject.Inject;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
 
class Config {
	
	@Inject
	@DefaultConfig(sharedRoot = true)
	private Path defaultConfig;
	
	@Inject
	@DefaultConfig(sharedRoot = true)
	private ConfigurationLoader<CommentedConfigurationNode> configManager;
	
	@Inject
	@ConfigDir(sharedRoot = true)
	private Path privateConfigDir;
	
	private KRules instance;
	
	private String dbUrl;
	private String dbUsername;
	private String dbPassword;
	private String readRules;
	private String rulesAccepted;
	private String allowedCommands;
	private Boolean forcePlayerToSpawnPoint;

	public String getDbUrl() {
		return dbUrl;
	}
	public String getDbUsername() {
		return dbUsername;
	}
	public String getDbPassword() {
		return dbPassword;
	}
	public String getReadRules() {
		return readRules;
	}
	public String getRulesAccepted() {
		return rulesAccepted;
	}
	public String getAllowedCommands() {
		return allowedCommands;
	}
	public Boolean getForcePlayerToSpawnPoint() {
		return forcePlayerToSpawnPoint;
	}
	
	Config(KRules instance) {
		this.instance = instance;
		setDefaults();
		load();
	}
	Config() {
		throw new UnsupportedOperationException();
	}
	
	
	private void setDefaults() {

		try {
			File theDir = new File("mods"+ File.separator + "KRules");
			theDir.mkdirs();
			File theConfig = new File("mods"+ File.separator + "KRules" + File.separator + "KRules.conf");
			if (!theConfig.exists()) {
				theConfig.createNewFile();
				Path potentialFile = Paths.get("mods"+ File.separator + "KRules" + File.separator + "KRules.conf");
				ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setPath(potentialFile).build();
				ConfigurationNode rootNode;
				    rootNode = loader.load();
				    rootNode.getNode("forcePlayerToSpawnPoint").setValue(true);
				    rootNode.getNode("dbUrl").setValue("jdbc:mysql://127.0.0.1/krules");
				    rootNode.getNode("dbUsername").setValue("krules");
				    rootNode.getNode("dbPassword").setValue("");
				    rootNode.getNode("readRules").setValue("§2You must read all the rules. Use /rules or click here.");
				    rootNode.getNode("rulesAccepted").setValue("§3Rules accepted.");
				    rootNode.getNode("allowedCommands").setValue("rules,spawn,help,sponge:callback");
				    loader.save(rootNode);
			}
		} catch(IOException e) {
		    e.printStackTrace();
		}
	}

	private void load() {
		Path config = Paths.get("mods"+ File.separator + "KRules" + File.separator + "KRules.conf");
		ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setPath(config).build();
		ConfigurationNode rootNode;
		    try {
				rootNode = loader.load();
				forcePlayerToSpawnPoint = rootNode.getNode("forcePlayerToSpawnPoint").getBoolean(true);
				dbUrl = rootNode.getNode("dbUrl").getString("jdbc:mysql://127.0.0.1/krules");
				dbUsername = rootNode.getNode("dbUsername").getString("krules");
				dbPassword = rootNode.getNode("dbPassword").getString("");
			    readRules = rootNode.getNode("readRules").getString("§2You must read all the rules. Use /rules or click here.");
			    rulesAccepted = rootNode.getNode("rulesAccepted").getString("§3Rules accepted.");
			    allowedCommands = rootNode.getNode("allowedCommands").getString("rules,spawn,help,sponge:callback");
		    
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
}