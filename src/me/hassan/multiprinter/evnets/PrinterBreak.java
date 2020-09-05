package me.hassan.multiprinter.evnets;

import java.util.UUID;

import me.hassan.riftgenerators.RiftGenerators;
import me.hassan.riftgenerators.objects.Storage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import me.hassan.multiprinter.MultiPrinter;
import me.hassan.multiprinter.utils.Common;

public class PrinterBreak implements Listener {

	@EventHandler
	public void printerBreak(BlockBreakEvent e) {
		Player player = e.getPlayer();
		UUID uuid = player.getUniqueId();
		Location location = e.getBlock().getLocation();

		if(Storage.isGenerator(location)){
			e.setCancelled(true);
			return;

		}

		if(MultiPrinter.getInstance().getPrinterState().contains(uuid)) {

			if(Storage.isGenerator(location)){
				e.setCancelled(true);
				return;
			}

			if(MultiPrinter.getInstance().getCanBreak().containsKey(uuid)) {
				
				if(MultiPrinter.getInstance().getCanBreak().get(uuid).contains(location)) {
					
					MultiPrinter.getInstance().getCanBreak().get(uuid).remove(location);
				}else {
					e.setCancelled(true);
					
					Common.sendMessage(player, MultiPrinter.getInstance().getConfig().getString("Printer-CantBreak"));
				}
			}else {
				e.setCancelled(true);
				Common.sendMessage(player, MultiPrinter.getInstance().getConfig().getString("Printer-CantBreak"));
			}
		}else{
			if (e.getBlock().getType().equals((Object)Material.ENDER_PORTAL_FRAME)) {
				this.breakPortal(e.getBlock());
			}
		}
		
		
	}



	public void breakPortal(final Block portal) {
		portal.breakNaturally();
		BlockFace[] values;
		for (int length = (values = BlockFace.values()).length, i = 0; i < length; ++i) {
			final BlockFace face = values[i];
			if (portal.getRelative(face).getType().equals((Object)Material.ENDER_PORTAL)) {
				this.breakPortal(portal.getRelative(face));
			}
		}
	}

}
