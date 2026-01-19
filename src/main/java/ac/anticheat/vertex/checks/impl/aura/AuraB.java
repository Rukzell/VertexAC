package ac.anticheat.vertex.checks.impl.aura;

import ac.anticheat.vertex.checks.Check;
import ac.anticheat.vertex.checks.type.PacketCheck;
import ac.anticheat.vertex.player.APlayer;
import ac.anticheat.vertex.utils.Config;
import ac.anticheat.vertex.utils.PacketUtil;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;

/**
 * Snap
 */
public class AuraB extends Check implements PacketCheck {
    private final double snapThreshold;

    public AuraB(APlayer aPlayer) {
        super("AuraB", aPlayer);
        this.snapThreshold = Config.getDouble(getConfigPath() + ".snap-threshold", 90);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!isEnabled() || !aPlayer.actionData.inCombat()) return;

        if (PacketUtil.isAttack(event)) {
            double currentDy = aPlayer.rotationData.deltaYaw;
            double lastDy = aPlayer.rotationData.lastDeltaYaw;

            if (lastDy < 2.5 && currentDy > snapThreshold) {
                flag("snap");
            }
        }
    }
}