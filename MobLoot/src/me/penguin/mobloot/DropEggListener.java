package me.penguin.mobloot;

import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class DropEggListener implements Listener {
	
    private mobloot plugin;
    private MobLootConfig mobLootConfig;
    private List<EntityType> blacklistedDropEgg;

    public DropEggListener(mobloot plugin, MobLootConfig mobLootConfig) {
        this.plugin = plugin;
        this.mobLootConfig = mobLootConfig;

        blacklistedDropEgg = mobLootConfig.getBlacklistedDropEgg();
    }
    
    @EventHandler
    public void onDropEgg(EntityDeathEvent event) {
        if (!(event.getEntity().getKiller() instanceof Player)) {
            return;
        }

        Player player = event.getEntity().getKiller();
        EntityType entityType = event.getEntityType();
        
        // Check if entity is blacklisted
        if (blacklistedDropEgg.contains(entityType)) {
            return;
        }
        
        // Spawn spawner egg with given chance
        if (mobLootConfig.isDropEggEnabled() && new Random().nextDouble() < mobLootConfig.getEggDropChance()) {
            ItemStack spawnEgg = new ItemStack(Material.MONSTER_EGG, 1, entityType.getTypeId());
            event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), spawnEgg);
        } 
    }
    
    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        if (mobLootConfig.isRenameEggEnabled()) {
            ItemStack inputItem = event.getInventory().getItem(0);
            if (inputItem != null && inputItem.getType() == Material.MONSTER_EGG) {
                event.setResult(null);
            }
        }
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
    	if (mobLootConfig.isSpawnFromEgg()) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
                ItemStack item = event.getItem();
                if (item != null && item.getType() == Material.MONSTER_EGG) {
                     event.setCancelled(true);
                }
            }
        }
    }

}

