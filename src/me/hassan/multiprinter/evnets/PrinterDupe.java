package me.hassan.multiprinter.evnets;

import java.util.UUID;

import com.bgsoftware.wildstacker.api.events.BarrelUnstackEvent;
import com.bgsoftware.wildstacker.api.events.SpawnerUnstackEvent;
import me.hassan.riftgenerators.GeneratorBreakBlockEvent;
import me.hassan.riftgenerators.objects.Storage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownExpBottle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import me.hassan.multiprinter.MultiPrinter;
import me.hassan.multiprinter.printerhandler.PrinterHandler;
import me.hassan.multiprinter.utils.Common;

public class PrinterDupe implements Listener {
	
	
	@EventHandler
	public void onExpBottleEvent(ProjectileLaunchEvent e) {
		if (!(e.getEntity() instanceof ThrownExpBottle)) // If the entity isn't an Exp Bottle, don't cancel event
			return;
		ThrownExpBottle bottle = (ThrownExpBottle) e.getEntity();
		if (!(bottle.getShooter() instanceof Player)) // If the shooter isn't a player, don't cancel event
			return;
		Player player = (Player) bottle.getShooter();
		if(this.hasPrinter(player)) {
			e.setCancelled(true);
			Common.sendMessage(player, MultiPrinter.getInstance().getConfig().getString("Printer-Exp"));
		}

	}

	@EventHandler
	public void onPotionSplash(PotionSplashEvent e) {
		if (e.getEntity().getShooter() instanceof Player) {
			Player player = (Player) e.getEntity().getShooter();
			if(this.hasPrinter(player)) {
				e.setCancelled(true);
				Common.sendMessage(player, MultiPrinter.getInstance().getConfig().getString("Printer-Potions"));
			}
		}
	}

	@EventHandler
	public void eggThrowEvent(PlayerEggThrowEvent e) {
		Player player = e.getPlayer();
		if(this.hasPrinter(player)) {
			e.setHatching(false);
			Common.sendMessage(player, MultiPrinter.getInstance().getConfig().getString("Printer-Egg"));
		}
	}
	
	@EventHandler
	public void onQuitEvent(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		UUID uuid = player.getUniqueId();
		if(this.hasPrinter(player)) {

			if(MultiPrinter.getInstance().teleportBack.containsKey(uuid)) {
				player.teleport(MultiPrinter.getInstance().teleportBack.get(uuid));
				MultiPrinter.getInstance().teleportBack.remove(uuid);
			}

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
					MultiPrinter.getInstance().getMoneySpent().remove(uuid);
					MultiPrinter.getInstance().getCanBreak().remove(uuid);
				}
				
			}
		}
	}

	@EventHandler
	public void onChestClick(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if (event.getClickedBlock() != null) {
					if(this.hasPrinter(player)) {
						Material block = event.getClickedBlock().getType();
						if(block == Material.CHEST || block == Material.TRAPPED_CHEST || block == Material.DISPENSER || block == Material.DROPPER || block == Material.ENCHANTMENT_TABLE) {
							event.setCancelled(true);
							Common.sendMessage(player, MultiPrinter.getInstance().getConfig().getString("Printer-Chest"));
						}
						
					}
			}
		}
	}
	
	@EventHandler
	public void eggClick(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block block = event.getClickedBlock();
			if(player.getItemInHand().getType() == Material.MONSTER_EGG || player.getItemInHand().getType() == Material.SPLASH_POTION || block.getType() == Material.CAULDRON) {
				if(this.hasPrinter(player)) {
					event.setCancelled(true);
					Common.sendMessage(player, MultiPrinter.getInstance().getConfig().getString("Printer-Egg"));
				}
			}
		}
	}

	@EventHandler
	public void barrelUnStack(BarrelUnstackEvent e){
		if(e.getUnstackSource() instanceof Player){
			Player player = (Player) e.getUnstackSource();
			if(this.hasPrinter(player)){
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent e) {
		Player player = e.getPlayer();
		if(this.hasPrinter(player)) {
			if (!(e.getMessage().equalsIgnoreCase("/printer blocks") || e.getMessage().equalsIgnoreCase("/printer")
					|| e.getMessage().equalsIgnoreCase("r") || e.getMessage().equalsIgnoreCase("msg"))) {
				e.setCancelled(true);
				Common.sendMessage(player, MultiPrinter.getInstance().getConfig().getString("Printer-Command"));

			}

		}
	}

	@EventHandler
	public void onPlayerDamage(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player) {
			Player player = (Player) e.getDamager();
			if(this.hasPrinter(player)) {
				e.setCancelled(true);
				Common.sendMessage(player, MultiPrinter.getInstance().getConfig().getString("Printer-Damage"));
			}
		}
	}
	
	@EventHandler
	public void onItemDrop(PlayerDropItemEvent e) {
		Player player = e.getPlayer();
		if(this.hasPrinter(player)) {
			e.setCancelled(true);
			Common.sendMessage(player, MultiPrinter.getInstance().getConfig().getString("Printer-Drop"));
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onItemPickup(EntityPickupItemEvent e) {
		Entity entity = e.getEntity();
		
		if(entity instanceof Player) {
			Player player = (Player) e.getEntity();
			if(this.hasPrinter(player)) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void playerJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		UUID uuid = player.getUniqueId();
		if(this.hasPrinter(player)) {
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
					MultiPrinter.getInstance().getMoneySpent().remove(uuid);
					MultiPrinter.getInstance().getCanBreak().remove(uuid);
				}
				
			}
		}
	}
	
	@EventHandler
	public void armorStand(PlayerArmorStandManipulateEvent e) {
		Player player = e.getPlayer();
		if(this.hasPrinter(player)) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onItemFrame(PlayerInteractEntityEvent e){
        if (!this.hasPrinter(e.getPlayer())){
            return;
        
        }
        
        if (e.getRightClicked() != null && e.getRightClicked() instanceof ItemFrame){
        	e.setCancelled(true);
            return;
        }
    }

    @EventHandler
	public void generatorBreak(GeneratorBreakBlockEvent e){
		Player player = e.getPlayer();
		if(this.hasPrinter(player)){
			e.setCancelled(true);
		}
	}



    @EventHandler
	public void unStackSpawner(SpawnerUnstackEvent e){
		if(e.getUnstackSource() instanceof Player){
			Player player = (Player) e.getUnstackSource();

			if(this.hasPrinter(player)){
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryMove(InventoryClickEvent e) {
        if (e.getCursor() == null || e.getCursor().getType() == Material.AIR || e.getWhoClicked().getGameMode() != GameMode.CREATIVE)
            return;
 
        Player player = (Player) e.getWhoClicked();
        ItemStack clicked = e.getCursor();
 
        if (!this.hasPrinter(player))
            return;
 
        if(clicked.getType() == Material.POTION) {
            e.setCancelled(true);
            Common.sendMessage(player, "&d&lRift&5&lMC &8>> &cYou can only place blocks that you can buy from shop in printer mode.");
            return;
        }
 
        if(!PrinterHandler.isSellabe(player, clicked)) {
        	e.setCancelled(true);
            Common.sendMessage(player, "&d&lRift&5&lMC &8>> &cYou can only place blocks that you can buy from shop in printer mode.");
            return;
        }
            
 
        
    }
	
	 @EventHandler
	    public void onInventoryOpen(InventoryOpenEvent e) {
	        if (!this.hasPrinter((Player) e.getPlayer()))
	            return;
	 
	        if (e.getInventory().getType() == InventoryType.CREATIVE) {
	            return;
	        }
	 
	        e.setCancelled(true);
	        Common.sendMessage(e.getPlayer(), "&d&lRift&5&lMC &8>> &cYou cannot open chests in printer mode.");
	    }

	    @EventHandler
		public void changeWorld(PlayerChangedWorldEvent e){
		Player player = e.getPlayer();
		UUID uuid = player.getUniqueId();
			if(this.hasPrinter(player) && e.getPlayer().getWorld().getName().equalsIgnoreCase("spawn")){
				player.setGameMode(GameMode.SURVIVAL);
				MultiPrinter.getInstance().getPrinterState().remove(uuid);
				MultiPrinter.getInstance().printerState.remove(uuid);
				Common.sendMessage(player, MultiPrinter.getInstance().getConfig().getString("Printer-Disable"));
				if(MultiPrinter.getInstance().getPlayerItem().containsKey(uuid) && MultiPrinter.getInstance().getArmorItem().containsKey(uuid)) {
					player.getInventory().setContents(MultiPrinter.getInstance().getPlayerItem().get(uuid));
					player.getInventory().setArmorContents(MultiPrinter.getInstance().getArmorItem().get(uuid));

					MultiPrinter.getInstance().getArmorItem().remove(uuid);
					MultiPrinter.getInstance().getPlayerItem().remove(uuid);

					MultiPrinter.getInstance().getCanBreak().remove(uuid);

					if(MultiPrinter.getInstance().teleportBack.containsKey(uuid)) {
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
		}
	
	public boolean hasPrinter(Player player) { return MultiPrinter.getInstance().getPrinterState().contains(player.getUniqueId()); }

}
