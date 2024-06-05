package me.penguin.mobloot;

import java.util.HashMap;
import java.util.Map;
import me.penguin.mobloot.API.ConfigManager;
import me.penguin.mobloot.API.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class CommandEntitiesListener implements Listener {

    private final ConfigManager config;
    private final Map<Integer, EntityType> commandEntities;

    public CommandEntitiesListener(ConfigManager config, MessageManager messageManager) {
        this.config = config;

        commandEntities = new HashMap<>();
        ConfigurationSection entitySection = config.getConfigurationSection("config.yml", "Entity");
        if (entitySection != null) {
            for (String key : entitySection.getKeys(false)) {
                ConfigurationSection entityConfig = entitySection.getConfigurationSection(key);
                if (entityConfig != null) {
                    int entityId = Integer.parseInt(key);
                    String entityName = entityConfig.getString("EntityName");
                    EntityType entityType = EntityType.fromName(entityName);
                    if (entityType != null) {
                        commandEntities.put(entityId, entityType);
                    } else {
                        messageManager.sendLog("warn", "Invalid entity type in Entity." + key + ".EntityName: " + entityName);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onCommandEntities(EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null) {
            return;
        }

        Player player = event.getEntity().getKiller();
        EntityType entityType = event.getEntityType();

        // Execute command for specific entities
        for (Map.Entry<Integer, EntityType> entry : commandEntities.entrySet()) {
            if (entry.getValue() == entityType) {
                String command = config.getString("config.yml", "Entity." + entry.getKey() + ".Command");
                if (command != null && !command.isEmpty()) {
                    command = command.replace("{player}", player.getName());
                    command = command.replace("{entity}", entityType.name());
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                }
            }
        }
    }
}
