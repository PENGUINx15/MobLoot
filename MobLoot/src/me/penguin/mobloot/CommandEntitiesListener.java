package me.penguin.mobloot;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class CommandEntitiesListener implements Listener {
	
    private mobloot plugin;
    private MobLootConfig mobLootConfig;
    private Map<Integer, EntityType> commandEntities;

    public CommandEntitiesListener(mobloot plugin, MobLootConfig mobLootConfig) {
        this.plugin = plugin;
        this.mobLootConfig = mobLootConfig;
        
        commandEntities = mobLootConfig.getCommandEntities();
    }
    
    @EventHandler
    public void onCommandEntities(EntityDeathEvent event) {
        if (!(event.getEntity().getKiller() instanceof Player)) {
            return;
        }

        Player player = event.getEntity().getKiller();
        EntityType entityType = event.getEntityType();
        
        // Execute command for specific entities
        for (Map.Entry<Integer, EntityType> entry : commandEntities.entrySet()) {
            if (entry.getValue() == entityType) {
                String command = plugin.getConfig().getString("Entity." + entry.getKey() + ".Command");
                if (command != null && !command.isEmpty()) {
                    command = command.replace("{player}", player.getName());
                    command = command.replace("{entity}", entityType.name());
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                }
            }
        }
    }
}

