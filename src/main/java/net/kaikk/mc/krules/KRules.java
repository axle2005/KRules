package net.kaikk.mc.krules;

import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.plugin.java.JavaPlugin;

public class KRules extends JavaPlugin {
	static KRules instance;
	Config config;
	DataStore ds;
	Map<UUID, Boolean> cache;
	Map<UUID, List<Integer>> pagesRead;
	List<String> rules;
	int pages;
	Pattern allowedCommandsEx;
	
	@Override
	public void onEnable() {
		instance=this;
		
		config=new Config();
		try {
			File fRules = new File("plugins"+File.separator+"KRules"+File.separator+"rules.txt");
			if (!fRules.exists()) {
				PrintWriter writer = new PrintWriter(fRules.toPath().toString(), "UTF-8");
				writer.println("1:Rule 1 on page 1\n1:Rule 2 on page 1\n2:Rule 3 on page 2");
				writer.close();
			}
			allowedCommandsEx = Pattern.compile("(^!?/((rules [0-9]+)|(acceptrules))$)",Pattern.CASE_INSENSITIVE);
			rules = Files.readAllLines(fRules.toPath(), StandardCharsets.UTF_8);
			pages = countPages(rules);
			
			
			ds=new DataStore(this, config.dbUrl, config.dbUsername, config.dbPassword);
			cache=new HashMap<UUID, Boolean>();
			
			this.getServer().getPluginManager().registerEvents(new EventListener(this), this);
			this.getCommand("krules").setExecutor(new CommandExec(this));
			this.getCommand("rules").setExecutor(new CommandExec(this));
		} catch (Exception e) {
			e.printStackTrace();
			this.getPluginLoader().disablePlugin(this);
		}
	}
	private static int countPages(List<String> rules) throws Exception
	{
		Pattern pattern = Pattern.compile("([0-9]+:.*)");
		int count = 0;
		for(String rule : rules)
		{
			Matcher m = pattern.matcher(rule);
			if(m.matches())
				if(count == Integer.parseInt(rule.substring(0,rule.indexOf(":"))))
					break;
				else if(count+1 == Integer.parseInt(rule.substring(0,rule.indexOf(":"))))
					count++;
				else
					throw new Exception("Improper rules format.");
		}
		return count;
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
	
	void log(String t) {
		log(Level.INFO, t);
	}
	
	void log(Level level, String t) {
		this.getLogger().log(level, t);
	}

}
