package net.kaikk.mc.krules;


import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
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
		if (sender instanceof Player) {
			if(!instance.ds.hasPlayerAgreedWithRules(((Player) sender).getUniqueId(),false)) {
				if(instance.rules.hasReadAllRules(((Player) sender).getUniqueId())) {
					instance.ds.addPlayerToAgreed(((Player) sender).getUniqueId());
					sender.sendMessage(instance.config.rulesAccepted);
					return true;
				} else {
						sender.sendMessage("You must read all rules before accepting. Use /rules #");
						sender.sendMessage("There are " + instance.rules.getPageCount() + " pages of rules");
						sender.sendMessage("You have read page(s) " + StringUtils.join(instance.rules.pagesAlreadyRead(((Player) sender).getUniqueId()),","));
					return false;
				}
			} else {
				sender.sendMessage("You have already agreed to the rules");
				return true;
			}
		} else {
			sender.sendMessage("Must be a player");
		}
		return false;
	}

	private boolean commandRules(CommandSender sender, String label, String[] args) {
		if (args.length==0 || args.length > 1 || !args[0].matches("^-?\\d+$")) {
			sender.sendMessage("Usage: /"+label+" #\nThere are " + instance.rules.getPageCount() + " pages of rules");
			return false;
		}

		if(args[0].matches("\\d+")) {
			try{
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', StringUtils.join(instance.rules.readRules(Integer.parseInt(args[0]), ((Player) sender).getUniqueId()),"\n")));
				return true;
			} catch(IndexOutOfBoundsException e){
				sender.sendMessage("Usage: /"+label+" #\nThere are " + instance.rules.getPageCount() + " pages of rules");
				return false;
			}
		}
		return false;
	}
	
	private boolean commandKRules(CommandSender sender, String label, String[] args) {
		
		if (!sender.hasPermission("krules.manage")) {
			sender.sendMessage("Insufficient permissions.");
			return false;
		}
		
		if (args.length==0 || args.length > 2) {
			sender.sendMessage("Usage: /"+label+" [current|reload|add|remove|check] [player name to modify/check]");
			return false;
		}
		
		String c = args[0].toLowerCase();
		if (c.equals("current")) {
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', StringUtils.join(instance.rules.getAllRules(),"\n")));
			return true;
		} else if (c.equals("reload")) {
			instance.getPluginLoader().disablePlugin(instance);
			instance.getPluginLoader().enablePlugin(instance);
			instance.log("Plugin reloaded");
			return true;
		} else if (c.equals("add")) {
			UUID uuid = instance.addPlayer(args[1]);
			if (uuid == null) {
				sender.sendMessage("error, could not find user");
				return false;
			} else {
				sender.sendMessage("Successfully added " + args[1] + " with UUID of " + uuid);
				return true;
			}
		} else if (c.equals("check")) {
			if (instance.checkPlayer(args[1])) {
				sender.sendMessage("Player " + args[1] + " has agreed to the rules.");
				return true;
			} else {
				sender.sendMessage("Player " + args[1] + " has not agreed or was not able to be found.");
				return true;
			}
		} else if (c.equals("remove")) {
			UUID uuid = instance.removePlayer(args[1]);
			if(uuid == null) {
				sender.sendMessage("error, could not find user");
				return false;
			} else {
				sender.sendMessage("Successfully removed " + args[1] + " with a UUID of " + uuid);
				return true;
			}
		}
		sender.sendMessage("Usage: /"+label+" [current|reload|add|remove|check] [player name to modify/check]");
		return false;
	}
}
