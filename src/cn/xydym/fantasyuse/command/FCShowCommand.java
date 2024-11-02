package cn.xydym.fantasyuse.command;

import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class FCShowCommand implements CommandExecutor {

    private static final Map<String, ItemStack> showItems = new HashMap<>();
    private static final Map<String, Map<String, Boolean>> playerViewStatus = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("只有玩家可以使用这个命令。");
            return true;
        }

        Player player = (Player) sender;
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand.getType() != Material.AIR) {
            // 使用ProtocolLib确保NBT数据的完整性
            NbtCompound nbtItem = (NbtCompound) NbtFactory.fromItemTag(itemInHand);
            ItemStack clonedItem = itemInHand.clone();
            NbtFactory.setItemTag(clonedItem, nbtItem);

            // 更新展示物品
            showItems.put(player.getName(), clonedItem);
            // 重置所有查看过该玩家物品的玩家状态
            resetViewedStatus(player.getName());

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                TextComponent messageComponent = new TextComponent(ChatColor.GOLD + player.getName() + "§c展示了物品: " + ChatColor.GREEN + getItemDisplayName(clonedItem));
                messageComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fcshow_open " + player.getName()));
                messageComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("点击查看展示物品").create()));
                onlinePlayer.spigot().sendMessage(messageComponent);
            }

        } else {
            player.sendMessage("§c你手上没有物品可以展示。");
        }

        return true;
    }

    private String getItemDisplayName(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasDisplayName()) {
            return meta.getDisplayName();
        } else {
            return item.getType().name().toLowerCase().replace('_', ' ');
        }
    }

    public static ItemStack getShowItem(String playerName) {
        return showItems.get(playerName);
    }

    public static boolean hasViewed(String playerName, String viewerName) {
        return playerViewStatus.getOrDefault(playerName, new HashMap<>()).getOrDefault(viewerName, false);
    }

    public static void markViewed(String playerName, String viewerName) {
        playerViewStatus.computeIfAbsent(playerName, k -> new HashMap<>()).put(viewerName, true);
    }

    private void resetViewedStatus(String playerName) {
        playerViewStatus.put(playerName, new HashMap<>());
    }
}