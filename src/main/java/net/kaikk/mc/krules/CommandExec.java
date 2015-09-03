package net.kaikk.mc.krules;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandExec implements CommandExecutor {
	private KRules instance;
	
	CommandExec(KRules instance) {
		this.instance = instance;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equals("krules")) {
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
				sender.sendMessage(instance.rules);
				return true;
			} else if (c.equals("reload")) {
				instance.getPluginLoader().disablePlugin(instance);
				instance.getPluginLoader().enablePlugin(instance);
				instance.log("Plugin reloaded");
				return true;
			}
		}
		return false;
	}
}
