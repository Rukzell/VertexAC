package ac.anticheat.vertex.checks.impl.aim;

import ac.anticheat.vertex.checks.Check;
import ac.anticheat.vertex.checks.type.PacketCheck;
import ac.anticheat.vertex.player.APlayer;
import ac.anticheat.vertex.utils.PacketUtil;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;

public class AimP extends Check implements PacketCheck {
    public AimP(APlayer aPlayer) {
        super("AimP", aPlayer);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!isEnabled() || !aPlayer.actionData.inCombat()) return;

        if (PacketUtil.isAttack(event)) {
            if (aPlayer.rotationData.deltaYaw > 45 && aPlayer.rotationData.lastDeltaYaw > 45 && aPlayer.rotationData.accelYaw < 1E-3) {
                flag();
            }
        }
    }
}
