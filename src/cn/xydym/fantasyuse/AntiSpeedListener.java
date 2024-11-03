package cn.xydym.fantasyuse;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class AntiSpeedListener implements Listener {

    private final JavaPlugin plugin;
    private double walkSpeedLimit;
    private double flySpeedLimit;
    private double verticalSpeedLimit; // 新增垂直移动速度限制

    public AntiSpeedListener(JavaPlugin plugin) {
        this.plugin  = plugin;
        loadConfig();
    }

    private void loadConfig() {
        FileConfiguration config = plugin.getConfig();
        walkSpeedLimit = config.getDouble("walk-speed-limit",  0.7);
        flySpeedLimit = config.getDouble("fly-speed-limit",  1.0);
        verticalSpeedLimit = config.getDouble("vertical-speed-limit",  12.0); // 加载垂直移动速度限制
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();


        if (to == null) return;

        // 检查玩家是否为OP
        if (player.isOp())  {
            return; // 如果是OP，跳过速度限制检查
        }

        // 计算水平距离
        double horizontalDistance = from.distance(new  Location(from.getWorld(),  to.getX(),  from.getY(),  to.getZ()));

        // 计算垂直距离
        double verticalDistance = Math.abs(to.getY()  - from.getY());

        if (player.isFlying())  {
            if (horizontalDistance > flySpeedLimit || verticalDistance > verticalSpeedLimit) {
                event.setTo(from);
            }
        } else {
            if (horizontalDistance > walkSpeedLimit || verticalDistance > verticalSpeedLimit) {
                event.setTo(from);
            }
        }
    }
}