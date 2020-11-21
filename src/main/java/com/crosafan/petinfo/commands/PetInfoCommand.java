package com.crosafan.petinfo.commands;

import java.util.List;

import com.crosafan.petinfo.PetInfo;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;

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
			case "-gui":
				PetInfo.openGui=true;
				break;
			case "-display":
					if(args[1]!=null) {
						if(args[1].equals("icon")) {
							PetInfo.displayIcon=true;
						}else if(args[1].equals("text")) {
							PetInfo.displayIcon=false;
						}else {
							PetInfo.displayIcon=false;
						}
					}
				
				break;
			}
		}

	}
	
	public boolean canCommandSenderUseCommand(final ICommandSender sender) {
	    return true;
	  }

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		if (args.length == 1) {
			return getListOfStringsMatchingLastWord(args, "-gui", "-display");
		} else if (args.length == 2 && args[0].equalsIgnoreCase("-display")) {
			return getListOfStringsMatchingLastWord(args, "icon", "text");
		}

		return null;
	}

}
