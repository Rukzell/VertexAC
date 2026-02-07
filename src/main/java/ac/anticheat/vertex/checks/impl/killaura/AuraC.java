package ac.anticheat.vertex.checks.impl.killaura;

import ac.anticheat.vertex.checks.Check;
import ac.anticheat.vertex.checks.type.PacketCheck;
import ac.anticheat.vertex.player.APlayer;
import ac.anticheat.vertex.utils.PacketUtil;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;

public class AuraC extends Check implements PacketCheck {
    public AuraC(APlayer aPlayer) {
        super("Aura", "(C)", aPlayer, false);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!isEnabled()) return;

        if (PacketUtil.isAttack(event)) {
            double deltaYaw = aPlayer.rotationData.deltaYaw;
            double lastDeltaYaw = aPlayer.rotationData.lastDeltaYaw;
            double deltaPitch = aPlayer.rotationData.deltaPitch;
            double lastDeltaPitch = aPlayer.rotationData.lastDeltaPitch;

            if (lastDeltaYaw < 0 && deltaYaw > 80 || lastDeltaYaw > 0 && deltaYaw < -80) {
                flag("X\nlast=" + lastDeltaYaw + "\ncurrent=" + deltaYaw);
            }

            if (lastDeltaPitch < 0 && deltaPitch > 50 || lastDeltaPitch > 0 && deltaPitch < -50) {
                flag("Y\nlast=" + lastDeltaPitch + "\ncurrent=" + deltaPitch);
            }
        }
    }
}
