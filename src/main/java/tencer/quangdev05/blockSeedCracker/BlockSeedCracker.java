package tencer.quangdev05.blockSeedCracker;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

public class BlockSeedCracker extends JavaPlugin {

    @Override
    public void onEnable() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Status.Server.OUT_SERVER_INFO) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (event.getPacketType() == PacketType.Status.Server.OUT_SERVER_INFO) {
                    String message = event.getPacket().getStrings().read(0);
                    if (message.startsWith("SeedCrackerX|")) {
                        Player player = event.getPlayer();
                        Bukkit.getScheduler().runTask(this.getPlugin(), new BukkitRunnable() {
                            @Override
                            public void run() {
                                player.kickPlayer("Bạn không được phép sử dụng mod SeedCrackerX!");
                                for (Player admin : Bukkit.getOnlinePlayers()) {
                                    if (admin.hasPermission("bsc.admin")) {
                                        admin.sendMessage("Người chơi " + player.getName() + " đã bị kick vì sử dụng mod SeedCrackerX!");
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }
}