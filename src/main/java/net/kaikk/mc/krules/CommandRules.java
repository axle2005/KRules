package net.kaikk.mc.krules;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Texts;

public class CommandRules implements CommandExecutor{
	private KRules instance;
	
	public CommandRules(KRules instance) {
		this.instance = instance;
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player)) {
			src.sendMessage(Texts.of("KRules commands must be run by a player"));
			return CommandResult.empty();
		}
		instance.rules.pages.sendTo(src);
		
		return CommandResult.success();
	}

}
