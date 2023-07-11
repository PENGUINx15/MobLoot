package me.penguin.mobloot;

import java.util.Random;

import org.apache.commons.lang.WordUtils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.milkbowl.vault.economy.Economy;

public class CurrencyDropsListener implements Listener {

    private mobloot plugin;
    private MobLootConfig mobLootConfig;

    public CurrencyDropsListener(mobloot plugin, MobLootConfig mobLootConfig) {
        this.plugin = plugin;
        this.mobLootConfig = mobLootConfig;
    }

    private String getCurrencyMessage(EntityType entityType, int amount) {
        String message = mobLootConfig.getCurrencyMessage();
        message = message.replace("{entity}", getFriendlyName(entityType));
        message = message.replace("{amount}", String.valueOf(amount));
        return message;
    }

    private String getDropItemMessage(EntityType entityType, int amount) {
        String message = mobLootConfig.getDropItemMessage();
        message = message.replace("{entity}", getFriendlyName(entityType));
        message = message.replace("{amount}", String.valueOf(amount));
        return message;
    }

    private String getFriendlyName(EntityType entityType) {
        return WordUtils.capitalizeFully(entityType.name().replace("_", " "));
    }  
    
    @EventHandler
    public void onCurrencyDrops(EntityDeathEvent event) {
        if (!(event.getEntity().getKiller() instanceof Player)) {
            return;
        }

        Player player = event.getEntity().getKiller();
        EntityType entityType = event.getEntityType();
        
        if (mobLootConfig.getBlacklistedCurrencyDrop().contains(entityType)) {
            return;
        }

        if (mobLootConfig.isDropItemEnabled()) {
            ItemStack dropItem = new ItemStack(mobLootConfig.getDropItemMaterial());
            ItemMeta meta = dropItem.getItemMeta();
            meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true);
            dropItem.setItemMeta(meta);

            event.getDrops().add(dropItem);
        } else if (new Random().nextDouble() < mobLootConfig.getCurrencyDropChance()) {
            int min = mobLootConfig.getCurrencyDropsMin();
            int max = mobLootConfig.getCurrencyDropsMax();
            int amount = new Random().nextInt(max - min + 1) + min;

            Economy economy = plugin.getServer().getServicesManager().getRegistration(Economy.class).getProvider();
            economy.depositPlayer(player, amount);

            player.sendMessage(getCurrencyMessage(entityType, amount).replace("&", "§"));
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (!mobLootConfig.isDropItemEnabled()) {
            return;
        }

        Player player =  event.getPlayer();
        ItemStack item = event.getItem().getItemStack();  
        
        if (item.getType() == mobLootConfig.getDropItemMaterial()) {
            int min = mobLootConfig.getCurrencyDropsMin();
            int max = mobLootConfig.getCurrencyDropsMax();
            int amount = new Random().nextInt(max - min + 1) + min;

            int dropItemCount = item.getAmount();
            for (int i = 0; i < dropItemCount; i++) {
                Economy economy = plugin.getServer().getServicesManager().getRegistration(Economy.class).getProvider();
                economy.depositPlayer(player, amount);
            }

            player.sendMessage(getDropItemMessage(EntityType.UNKNOWN, dropItemCount * amount).replace("&", "§"));

            event.getItem().remove();
            event.setCancelled(true);
        }
    }
}
