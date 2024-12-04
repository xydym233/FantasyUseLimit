package cn.xydym.fantasyuse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;

// 当前监听器只在特殊情况下启用
// 当前监听器只在特殊情况下启用
// 当前监听器只在特殊情况下启用

public class PlayerMoveListener implements Listener {
    private final Map<Material, String> bannedBlocks = new HashMap<>();
    private final int checkRange = 6;
    private final Plugin plugin;

    public PlayerMoveListener(Plugin plugin) {
        this.plugin = plugin;
        List<String> bannedBlockConfigs = plugin.getConfig().getStringList("banblock");
        for (String config : bannedBlockConfigs) {
            String[] parts = config.split(":");
            if (parts.length == 2) {
                Material material = Material.getMaterial(parts[0]);
                if (material != null) {
                    bannedBlocks.put(material, parts[1]);
                } else {
                    plugin.getLogger().warning("配置文件中存在无效的方块类型: " + parts[0]);
                }
            } else {
                plugin.getLogger().warning("配置文件格式错误: " + config);
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
                event.getFrom().getBlockY() == event.getTo().getBlockY() &&
                event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        int playerX = player.getLocation().getBlockX();
        int playerY = player.getLocation().getBlockY();
        int playerZ = player.getLocation().getBlockZ();

        for (int x = -checkRange; x <= checkRange; ++x) {
            for (int y = -checkRange; y <= checkRange; ++y) {
                for (int z = -checkRange; z <= checkRange; ++z) {
                    Block block = player.getWorld().getBlockAt(playerX + x, playerY + y, playerZ + z);
                    Material blockType = block.getType();
                    if (bannedBlocks.containsKey(blockType)) {
                        String dataValueConfig = bannedBlocks.get(blockType);
                        if (dataValueConfig.equals("*")) {
                            block.setType(Material.AIR);
                        } else {
                            try {
                                short dataValue = (short) block.getData();
                                short configDataValue = Short.parseShort(dataValueConfig);
                                if (dataValue == configDataValue) {
                                    block.setType(Material.AIR);
                                }
                            } catch (NumberFormatException e) {
                                player.sendMessage("配置文件中存在无效的数据值: " + dataValueConfig);
                            }
                        }
                    }
                }
            }
        }
    }
}
