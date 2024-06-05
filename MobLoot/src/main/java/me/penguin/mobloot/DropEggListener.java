package me.penguin.mobloot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.penguin.mobloot.API.ConfigManager;
import me.penguin.mobloot.API.MessageManager;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class DropEggListener implements Listener {

    private final ConfigManager config;
    private final MessageManager messageManager;

    public DropEggListener(ConfigManager config, MessageManager messageManager) {
        this.config = config;
        this.messageManager = messageManager;
    }

    @EventHandler
    public void onDropEgg(EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null) {
            return;
        }

        EntityType entityType = event.getEntityType();

        if (getBlacklistedDropEgg().contains(entityType)) {
            return;
        }

        if (config.getBoolen("config.yml", "DropEgg.Enable") && new Random().nextDouble() < config.getDouble("config.yml", "DropEgg.Chance")) {
            ItemStack spawnEgg = new ItemStack(Material.MONSTER_EGG, 1, entityType.getTypeId());
            event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), spawnEgg);
        }
    }

    private ArrayList<Object> getBlacklistedDropEgg() {
        ArrayList<Object> blacklistedDropEgg = new ArrayList<>();
        List<String> dropEggBlacklist = config.getStringList("config.yml","DropEgg.Blacklist");
        for (String entityName : dropEggBlacklist) {
            EntityType entityType = EntityType.fromName(entityName);
            if (entityType != null) {
                blacklistedDropEgg.add(entityType);
            } else {
                messageManager.sendLog("warn", "Invalid entity type in DropEgg.Blacklist: " + entityName);
            }
        }
        return blacklistedDropEgg;
    }

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        if (config.getBoolen("config.yml", "DropEgg.Rename")) {
            ItemStack inputItem = event.getInventory().getItem(0);
            if (inputItem != null && inputItem.getType() == Material.MONSTER_EGG) {
                event.setResult(null);
            }
        }
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (config.getBoolen("config.yml", "DropEgg.SpawnFromEgg")) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
                ItemStack item = event.getItem();
                if (item != null && item.getType() == Material.MONSTER_EGG) {
                    event.setCancelled(true);
                }
            }
        }
    }

}
