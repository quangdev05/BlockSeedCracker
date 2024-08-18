package tencer.quangdev05.blockSeedCracker;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import com.google.common.collect.Multimap;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BlockSeedCracker extends JavaPlugin {

    private final List<String> blockedMods = Arrays.asList("seedcracker");
    private ProtocolManager protocolManager;

    @Override
    public void onEnable() {
        // Đăng ký ProtocolLib
        protocolManager = ProtocolLibrary.getProtocolManager();

        // Đăng ký sự kiện lắng nghe gói tin login
        protocolManager.addPacketListener(new PacketAdapter(this, PacketType.Login.Client.START) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                WrappedGameProfile profile = event.getPacket().getGameProfiles().read(0);
                Player player = Bukkit.getPlayer(profile.getName());

                if (player != null && isUsingBlockedMod(profile)) {
                    // Kick người chơi ra khỏi server
                    event.setCancelled(true);
                    Bukkit.getScheduler().runTask(BlockSeedCracker.this, () -> {
                        player.kickPlayer(ChatColor.RED + "Bạn không được phép sử dụng Seed Cracker hoặc các mod tương tự!");

                        // Gửi thông báo đến các admin
                        for (Player admin : Bukkit.getOnlinePlayers()) {
                            if (admin.hasPermission("bsc.admin")) {
                                admin.sendMessage(ChatColor.RED + "Người chơi " + player.getName() + " đã bị kick vì sử dụng mod Seed Cracker.");
                            }
                        }
                    });
                }
            }
        });

        // Đăng ký sự kiện lắng nghe gói tin chat và gói tin khác nếu cần
        protocolManager.addPacketListener(new PacketAdapter(this, PacketType.Play.Client.CHAT) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                // Xử lý gói tin chat hoặc các gói tin khác nếu cần
            }
        });

        getLogger().info("BlockSeedCracker đã được kích hoạt!");
    }

    @Override
    public void onDisable() {
        getLogger().info("BlockSeedCracker đã được tắt!");
    }

    private boolean isUsingBlockedMod(WrappedGameProfile profile) {
        Multimap<String, WrappedSignedProperty> properties = profile.getProperties();
    
        // Kiểm tra các thuộc tính của game profile
        for (Map.Entry<String, WrappedSignedProperty> entry : properties.entries()) {
            WrappedSignedProperty property = entry.getValue();
            String value = property.getValue();
    
            // Kiểm tra chuỗi dữ liệu từ các mod client gửi đến
            for (String mod : blockedMods) {
                if (value.toLowerCase().contains(mod.toLowerCase())) {
                    return true;
                }
            }
        }
    
        // Kiểm tra các thuộc tính khác nếu cần
        return false;
    }
}