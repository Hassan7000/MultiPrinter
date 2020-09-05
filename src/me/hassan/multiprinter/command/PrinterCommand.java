package me.hassan.multiprinter.command;

import java.util.UUID;

import me.rerere.matrix.api.MatrixAPI;
import me.rerere.matrix.api.MatrixAPIProvider;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.hassan.multiprinter.MultiPrinter;
import me.hassan.multiprinter.utils.Common;

public class PrinterCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			UUID uuid = player.getUniqueId();
			if(args.length == 0) {
				
				if(!player.hasPermission("multiprinter.use")) {
					Common.sendMessage(player, MultiPrinter.getInstance().getConfig().getString("Printer-Permission"));
					return false;
				}

				if(!player.getWorld().getName().equalsIgnoreCase("Islands")){
					Common.sendMessage(player, "&cYou can only execute this command in the island world!");
					return false;
				}
				
				if(MultiPrinter.getInstance().getPrinterState().contains(uuid)) {
					MultiPrinter.getInstance().getPrinterState().remove(uuid);
					MultiPrinter.getInstance().printerState.remove(uuid);
					Common.sendMessage(player, MultiPrinter.getInstance().getConfig().getString("Printer-Disable"));
					
					if(player.getGameMode() == GameMode.CREATIVE) {
						player.setGameMode(GameMode.SURVIVAL);
						
						if(MultiPrinter.getInstance().getPlayerItem().containsKey(uuid) && MultiPrinter.getInstance().getArmorItem().containsKey(uuid)) {
							player.getInventory().setContents(MultiPrinter.getInstance().getPlayerItem().get(uuid));
							player.getInventory().setArmorContents(MultiPrinter.getInstance().getArmorItem().get(uuid));
							
							MultiPrinter.getInstance().getArmorItem().remove(uuid);
							MultiPrinter.getInstance().getPlayerItem().remove(uuid);
							
							MultiPrinter.getInstance().getCanBreak().remove(uuid);
							
							if(MultiPrinter.getInstance().teleportBack.containsKey(uuid)) {
								player.teleport(MultiPrinter.getInstance().teleportBack.get(uuid));
								MultiPrinter.getInstance().teleportBack.remove(uuid);
							}
							
							if(MultiPrinter.getInstance().getMoneySpent().containsKey(uuid)) {
								double price = MultiPrinter.getInstance().getMoneySpent().get(uuid);
								
								Common.sendMessage(player, MultiPrinter.getInstance().getConfig().getString("Printer-MoneySpent")
										.replace("{amount}", Common.formatNumbers(price)));
								MultiPrinter.getInstance().getMoneySpent().remove(uuid);
								
								
							}
						}
						
					}
				}else {
					
					if(player.getGameMode() == GameMode.SURVIVAL) {


						player.closeInventory();
						//if(!player.getWorld().getName().equalsIgnoreCase("Islands")){
						//		Common.sendMessage(player,"&d&lRift&5&lMC &8>> &cYou have to be in the island world before you can use this command");
						//	return false;
						//}

						MultiPrinter.getInstance().getPrinterState().add(uuid);
						Common.sendMessage(player, MultiPrinter.getInstance().getConfig().getString("Printer-Enable"));
						
						MultiPrinter.getInstance().getPlayerItem().put(uuid, player.getInventory().getContents());
						MultiPrinter.getInstance().getArmorItem().put(uuid, player.getInventory().getArmorContents());
						
						player.getInventory().clear();
						player.getInventory().setArmorContents(null);
						
						player.setGameMode(GameMode.CREATIVE);
						MultiPrinter.getInstance().teleportBack.put(player.getUniqueId(), player.getLocation());
						
					}else {
						Common.sendMessage(player, "&d&lRift&5&lMC &8>> &7You can't enable printer mode in survial");
					}
					
					
					
					
					
				}
			}
			if(args.length == 1) {
				if(args[0].equalsIgnoreCase("reload")) {
					if(player.hasPermission("multiprinter.reload")) {
						MultiPrinter.getInstance().reloadConfig();
						Common.sendMessage(player, "&d&lRift&5&lMC &8>> &7You have reloaded the config file");
					}
				}
			}
		}else {
			Common.sendMessage(sender, "&d&lRift&5&lMC &8>> &cThis command must be executed by a player");
		}
		
		
		return false;
	}

	

}
