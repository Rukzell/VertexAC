package ac.anticheat.vertex.checks.type;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;

public interface PacketCheck {
    default void onPacketReceive(PacketReceiveEvent event) {
    }

    default void onReload() {
    }
}
