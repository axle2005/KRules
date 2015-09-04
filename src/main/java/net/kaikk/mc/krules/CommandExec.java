package net.kaikk.mc.krules;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandExec implements CommandExecutor {
	private KRules instance;
	
	CommandExec(KRules instance) {
		this.instance = instance;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		switch(cmd.getName().toLowerCase()) {
			case "krules":
				return commandKRules(sender, label, args);
			case "rules":
				return commandRules(sender, label, args);
			case "acceptrules":
				return commandAcceptRules(sender, label, args);
			default:
				return false;
		}
	}

	private boolean commandAcceptRules(CommandSender sender, String label, String[] args) {
		if(sender instanceof Player) {
			if(!instance.ds.hasPlayerAgreedWithRules(((Player) sender).getUniqueId())) {
				if(instance.hasIntegersUpTo(instance.pagesRead.get(((Player) sender).getUniqueId()),instance.pages) ) {
					instance.ds.addPlayerToAgreed(((Player) sender).getUniqueId());
					sender.sendMessage(instance.config.rulesAccepted);
					return true;
				}
				else{
					sender.sendMessage("You must read all rules before accepting. Use /rules #");
					return false;
				}
				
			}
			else {
				sender.sendMessage("You have already agreed to the rules");
				return true;
			}
		}
		else
			sender.sendMessage("Must be a player");
		
		return false;
	}

	private boolean commandRules(CommandSender sender, String label, String[] args) {
		
		if (args.length==0 || args.length > 1 || !args[0].matches("^-?\\d+$")) {
			sender.sendMessage("Usage: /"+label+" #\nThere are " + instance.pages + " pages of rules");
			return false;
		}

		if(args[0].matches("\\d+")) {
			int page = Integer.parseInt(args[0]);
			if(page > instance.pages || page < 1) {
				sender.sendMessage("Not a valid page");
				return false;
			}
			
			for(String rule : instance.rules) {
				if(Integer.parseInt(rule.substring(0,rule.indexOf(":"))) == Integer.parseInt(args[0]))
					sender.sendMessage(rule);
			}
			
			
			if(sender instanceof Player) {
				if(!instance.ds.hasPlayerAgreedWithRules(((Player) sender).getUniqueId())) {
					if(instance.pagesRead.containsKey(((Player) sender).getUniqueId())) {
						if(!instance.pagesRead.get(((Player) sender).getUniqueId()).contains(page))
							instance.pagesRead.get(((Player) sender).getUniqueId()).add(page);
					}
					else
						instance.pagesRead.put(((Player) sender).getUniqueId(), Arrays.asList(page));
					
				}
			}
			return true;
		}
	
		return false;
	
	}
	
	private boolean commandKRules(CommandSender sender, String label, String[] args) {
		
		if (!sender.hasPermission("krules.manage")) {
			sender.sendMessage("Insufficient permissions.");
			return false;
		}
		
		if (args.length==0) {
			sender.sendMessage("Usage: /"+label+" [current|reload]");
			return false;
		}
		
		String c = args[0].toLowerCase();
		if (c.equals("current")) {
			sender.sendMessage(StringUtils.join(instance.rules,"\n"));
			return true;
		} else if (c.equals("reload")) {
			instance.getPluginLoader().disablePlugin(instance);
			instance.getPluginLoader().enablePlugin(instance);
			instance.log("Plugin reloaded");
			return true;
		}
		return false;
		
	}
}
