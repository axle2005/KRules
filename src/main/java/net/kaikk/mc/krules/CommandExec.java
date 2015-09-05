package net.kaikk.mc.krules;


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
				if(instance.rules.hasReadAllRules(((Player) sender).getUniqueId())) {
					instance.ds.addPlayerToAgreed(((Player) sender).getUniqueId());
					sender.sendMessage(instance.config.rulesAccepted);
					return true;
				}
				else{
						sender.sendMessage("You must read all rules before accepting. Use /rules #");
						sender.sendMessage("There are " + instance.rules.getPageCount() + " pages of rules");
						sender.sendMessage("You have read page(s) " + StringUtils.join(instance.rules.pagesAlreadyRead(((Player) sender).getUniqueId()),","));
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
			sender.sendMessage("Usage: /"+label+" #\nThere are " + instance.rules.getPageCount() + " pages of rules");
			return false;
		}

		if(args[0].matches("\\d+")) {
			try{
				sender.sendMessage(StringUtils.join(instance.rules.readRules(Integer.parseInt(args[0]), ((Player) sender).getUniqueId()),"\n"));
				return true;
			}catch(IndexOutOfBoundsException e){
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
		
		if (args.length==0) {
			sender.sendMessage("Usage: /"+label+" [current|reload]");
			return false;
		}
		
		String c = args[0].toLowerCase();
		if (c.equals("current")) {
			sender.sendMessage(StringUtils.join(instance.rules.getAllRules(),"\n"));
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
