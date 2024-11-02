package cn.xydym.fantasyuse;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;

public class FCWXNJCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("只有玩家可以使用这个指令！");
            return true;
        }

        Player player = (Player) sender;

        // 检查玩家是否为OP
        if (!player.isOp()) {
            player.sendMessage("你没有权限执行这个指令！");
            return true;
        }

        ItemStack itemInHand = player.getItemInHand();

        if (itemInHand == null || itemInHand.getType() == Material.AIR) {
            player.sendMessage("你必须手持一个物品！");
            return true;
        }

        // 使用 ProtocolLib 设置物品为无法破坏
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.SET_SLOT);

        // 获取物品的 NBT 数据
        NbtCompound nbt = (NbtCompound) NbtFactory.fromItemTag(itemInHand);
        nbt.put("Unbreakable", (byte) 1);

        // 更新物品的 NBT 数据
        NbtFactory.setItemTag(itemInHand, nbt);

        // 发送更新后的物品给玩家
        try {
            protocolManager.sendServerPacket(player, packet);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        player.sendMessage("物品已设置为无法破坏！");
        return true;
    }
}