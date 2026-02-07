package ac.anticheat.vertex.listeners;

import ac.anticheat.vertex.VertexAC;
import ac.anticheat.vertex.checks.Check;
import ac.anticheat.vertex.checks.type.PacketCheck;
import ac.anticheat.vertex.managers.PlayerDataManager;
import ac.anticheat.vertex.player.APlayer;
import ac.anticheat.vertex.utils.PacketUtil;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import org.bukkit.entity.Player;

import java.util.List;

public class PacketCheckListener implements com.github.retrooper.packetevents.event.PacketListener {

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        Player player = event.getPlayer();

        if (player == null) return;

        for (Check check : VertexAC.getCheckManager().getChecks(player)) {
            if (!(check instanceof PacketCheck packetCheck)) return;

            packetCheck.onPacketReceive(event);
        }
    }
}
