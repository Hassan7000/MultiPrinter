package me.hassan.multiprinter.evnets;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import me.hassan.multiprinter.MultiPrinter;
import me.hassan.multiprinter.printerhandler.PrinterHandler;

public class PrinterPlace implements Listener {
	
	@EventHandler(priority = EventPriority.HIGH)
	public void printerPlace(BlockPlaceEvent e) {
		Player player = e.getPlayer();
		UUID uuid = player.getUniqueId();
		ItemStack item = player.getItemInHand();
		Location location = e.getBlock().getLocation();
		if(MultiPrinter.getInstance().getPrinterState().contains(uuid)) {
			
			if(e.isCancelled()) return;
			
			if(PrinterHandler.handleBlockPlaceMent(player, item)) {
				
				if(MultiPrinter.getInstance().getCanBreak().containsKey(uuid)) {
					
					ArrayList<Location> locations = MultiPrinter.getInstance().getCanBreak().get(uuid);
					
					
					locations.add(location);
					
					MultiPrinter.getInstance().getCanBreak().put(uuid, locations);
				}else {
					ArrayList<Location> locations = new ArrayList<>();
					
					
					locations.add(location);
					MultiPrinter.getInstance().getCanBreak().put(uuid, locations);
				}
			}else {
				e.setCancelled(true);
			}
		}
		
	}

}
