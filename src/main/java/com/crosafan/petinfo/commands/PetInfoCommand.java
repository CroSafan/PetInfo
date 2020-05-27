package com.crosafan.petinfo.commands;

import com.crosafan.petinfo.PetInfo;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public class PetInfoCommand extends CommandBase {

	private PetInfo petInfo;

	public PetInfoCommand(PetInfo petInfo) {
		this.petInfo = petInfo;
	}

	@Override
	public String getCommandName() {
		return "petinfo";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/petinfo [subcommand]";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if(args.length>0) {
			switch(args[0]) {
			case "gui":
				PetInfo.openGui=true;
				break;
			}
		}

	}
	
	public boolean canCommandSenderUseCommand(final ICommandSender sender) {
	    return true;
	  }

}
