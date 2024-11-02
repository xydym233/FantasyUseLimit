package cn.xydym.fantasyuse;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.HashSet;
import java.util.Set;

public class BlockHandler implements CommandExecutor, Listener {
    private static final Set<Player> listeningPlayers = new HashSet<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.isOp()) {
                // 设置玩家为监听状态
                listeningPlayers.add(player);
                player.sendMessage(ChatColor.GREEN + "你现在可以放置方块，放置后我会告诉你方块ID和数据值。");
            } else {
                player.sendMessage(ChatColor.RED + "你没有权限使用这个指令。");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "这个指令只能由玩家使用。");
        }
        return true;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (listeningPlayers.contains(player)) {
            Block block = event.getBlock();
            Material blockType = block.getType();
            byte blockData = block.getData();
            player.sendMessage(ChatColor.YELLOW + "注意！请删除前缀 fcul.name. 再使用这个id！");
            player.sendMessage(ChatColor.GREEN + "可点击复制！: fcul.name." + blockType.name() + ":" + blockData);
            // 移除监听状态
            listeningPlayers.remove(player);
        }
    }
}