package com.gecgooden.chunkgen;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;

public class ChunkGenCommand implements ICommand
{	
	private List aliases;
	public ChunkGenCommand()
	{
		this.aliases = new ArrayList();
		this.aliases.add("chunkgen");
	}

	@Override
	public String getCommandName()
	{
		return "chunkgen";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender)
	{
		return "chunkgen <x> <y> <height> <width> [dimension]";
	}

	@Override
	public List getCommandAliases()
	{
		return this.aliases;
	}

	/**
	 * Return the required permission level for this command.
	 */
	public int getRequiredPermissionLevel()
	{
		return 4;
	}

	@Override
	public void processCommand(ICommandSender icommandsender, String[] astring)
	{
		if(!icommandsender.canCommandSenderUseCommand(getRequiredPermissionLevel(), this.getCommandName())) {
			icommandsender.sendChatToPlayer(new ChatMessageComponent().addText("commands.generic.permission"));
//			ChatComponentTranslation chatTranslation = new ChatComponentTranslation("commands.generic.permission", new Object[0]);
//			MinecraftServer.getServer().addChatMessage(chatTranslation);
//			icommandsender.addChatMessage(new ChatComponentText(chatTranslation.getUnformattedTextForChat()));
		} else {
			int playerX = 0;
			int playerY = 0;
			int playerZ = 0;
			if(!icommandsender.getCommandSenderName().equalsIgnoreCase("Rcon")) {
				EntityPlayer ep = MinecraftServer.getServer().worldServerForDimension(0).getPlayerEntityByName(icommandsender.getCommandSenderName());
				ChunkCoordinates cc = icommandsender.getPlayerCoordinates();
				playerX = cc.posX;
				playerY = cc.posY;
				playerZ = cc.posZ;
			}
			try {
				int x = 0;
				int z = 0;
				if(astring[0].equalsIgnoreCase("~")) {
					x = playerX/16;
				} else {
					x = Integer.parseInt(astring[0]);
				}
				if(astring[1].equalsIgnoreCase("~")) {
					z = playerZ/16;
				} else {
					z = Integer.parseInt(astring[1]);
				}
				int height = Integer.parseInt(astring[2]);
				int width = Integer.parseInt(astring[3]);
				int dimensionID = 0;
				if(astring.length == 5) {
					dimensionID = Integer.parseInt(astring[4]);
				}

				System.out.println(x + " " + z + " " + height + " " + width + " " + dimensionID);

				ChunkProviderServer cps = MinecraftServer.getServer().worldServerForDimension(dimensionID).theChunkProviderServer;
				List<Chunk> chunks = new ArrayList<Chunk>(width*height);
				for(int i = (x - width/2); i < (x + width/2); i++) {
					for(int j = (z - height/2); j < (z + height/2); j++) {
						System.out.println("About to generate chunk at " + i + " " + j);
						if(!cps.chunkExists(i, j)) {
							chunks.add(cps.loadChunk(i, j)); 
							cps.saveChunks(true, null);
						}
						System.out.println("Loaded Chunk at " + i + " " + j);
					}
				}
				for(Chunk c : chunks) {
					cps.unloadChunksIfNotNearSpawn(c.xPosition, c.zPosition);
				}
//				ChatComponentTranslation chatTranslation = new ChatComponentTranslation("commands.successful");
//				MinecraftServer.getServer().addChatMessage(chatTranslation);
//				icommandsender.addChatMessage(new ChatComponentText(chatTranslation.getUnformattedTextForChat()));
				icommandsender.sendChatToPlayer(new ChatMessageComponent().addText("commands.successful"));
			} catch (NumberFormatException e) {
				e.printStackTrace();
//				ChatComponentTranslation chatTranslation = new ChatComponentTranslation("commands.numberFormatException");
//				MinecraftServer.getServer().addChatMessage(chatTranslation);
//				icommandsender.addChatMessage(new ChatComponentText(chatTranslation.getUnformattedTextForChat()));
				icommandsender.sendChatToPlayer(new ChatMessageComponent().addText("commands.numberFormatException"));
			} catch (Exception e) {
				e.printStackTrace();
//				ChatComponentTranslation chatTranslation = new ChatComponentTranslation("commands.failed");
//				MinecraftServer.getServer().addChatMessage(chatTranslation);
//				icommandsender.addChatMessage(new ChatComponentText(chatTranslation.getUnformattedTextForChat()));
				icommandsender.sendChatToPlayer(new ChatMessageComponent().addText("commands.failed"));
			}
			
		}
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender icommandsender)
	{	
		return true;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender icommandsender,
			String[] astring)
	{
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] astring, int i)
	{
		return false;
	}

	@Override
	public int compareTo(Object o)
	{
		return 0;
	}
}