package ac.anticheat.vertex.checks.impl.aim;

import ac.anticheat.vertex.checks.Check;
import ac.anticheat.vertex.checks.type.PacketCheck;
import ac.anticheat.vertex.player.APlayer;
import ac.anticheat.vertex.utils.PacketUtil;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;

public class AimK extends Check implements PacketCheck {
    public AimK(APlayer aPlayer) {
        super("AimK", aPlayer);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!isEnabled() || !aPlayer.actionData.inCombat() || aPlayer.bukkitPlayer.isInsideVehicle()) return;

        if (PacketUtil.isRotation(event)) {
            double pitch = Math.abs(aPlayer.rotationData.pitch);

            if (pitch > 90.1) {
                flag(String.format("pitch=%.5f", pitch));
            }
        }
    }
}
