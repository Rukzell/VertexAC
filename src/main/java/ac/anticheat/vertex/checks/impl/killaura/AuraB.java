package ac.anticheat.vertex.checks.impl.killaura;

import ac.anticheat.vertex.checks.Check;
import ac.anticheat.vertex.checks.type.PacketCheck;
import ac.anticheat.vertex.player.APlayer;
import ac.anticheat.vertex.utils.PacketUtil;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;

public class AuraB extends Check implements PacketCheck {
    public AuraB(APlayer aPlayer) {
        super("Aura", "(B)", aPlayer, false);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!isEnabled()) return;

        if (PacketUtil.isAttack(event)) {
            double currentDy = aPlayer.rotationData.deltaYaw;
            double lastDy = aPlayer.rotationData.lastDeltaYaw;

            if (lastDy < 3 && currentDy > 90) {
                flag("snap");
            }
        }
    }
}