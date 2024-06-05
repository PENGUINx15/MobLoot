package me.penguin.mobloot.API;

import me.clip.placeholderapi.PlaceholderAPI;
import me.penguin.mobloot.MobLoot;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageManager implements Listener {
	
	private final MobLoot plugin;
    public MessageManager(MobLoot plugin) {
    	this.plugin = plugin;
	}

	public void sendMessage(Player player, String message) {
        if (player != null && message != null) {
            message = PlaceholderAPI.setPlaceholders(player, message);
            List<String> result = splitText(message);
            for (String line : result) {
                if (line.contains("{action}")) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(line.replace("&", "§").replace("{action}", "")));
                } else if (line.contains("{title}")) {
                    player.sendTitle(line.replace("&", "§").replace("{title}", ""), "", 10, 70, 20);
                } else if (line.contains("{subtitle}")) {
                    player.sendTitle("", line.replace("&", "§").replace("{subtitle}", ""), 10, 70, 20);
                } else if (line.contains("{message}")) {
                    player.sendMessage(line.replace("&", "§").replace("{message}", ""));
                }
            }
        } else {
            sendLog("info",(player == null ? "Player" : "Message") + " is null");
        }
	}

	public void sendLog(String type , String message) {
		if (message !=null ) {

            switch (type) {
                case "warn":
                    plugin.getLogger().warning(message);
                    break;
                case "error":
                    plugin.getLogger().severe(message);
                    break;
                default:
                    plugin.getLogger().info(message);
                    break;
            }
	    } else {
	        plugin.getLogger().warning("Message is null");
		}
    }
    public static List<String> splitText(String text) {
        String regex = "\\{(title|action|message|subtitle)}(.*?)(?=\\{(?:title|action|message|subtitle)}|$)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        List<String> nonEmptyParts = new ArrayList<>();
        while (matcher.find()) {
            nonEmptyParts.add(matcher.group(0).trim());
        }

        return nonEmptyParts;
    }

}
