package cn.xydym.fantasyuse.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class FCShowOpenCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c只有玩家可以使用这个命令。");
            return true;
        }

        Player player = (Player) sender;
        if (args.length > 0) {
            String playerName = args[0];
            ItemStack showItem = FCShowCommand.getShowItem(playerName);
            if (showItem != null) {
                if (!FCShowCommand.hasViewed(playerName, player.getName())) {
                    Inventory inventory = Bukkit.createInventory(null, 9, ChatColor.RED + playerName + ChatColor.AQUA + " 的展示物品");
                    inventory.setItem(4, showItem);
                    player.openInventory(inventory);
                    FCShowCommand.markViewed(playerName, player.getName());
                } else {
                    player.sendMessage("§c>> §a你已经查看过该玩家的展示物品了~");
                }
            } else {
                player.sendMessage("§c该玩家没有展示物品。");
            }
        } else {
            player.sendMessage("§c请指定要查看的玩家。");
        }

        return true;
    }
}