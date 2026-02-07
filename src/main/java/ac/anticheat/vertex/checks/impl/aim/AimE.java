package ac.anticheat.vertex.checks.impl.aim;

import ac.anticheat.vertex.buffer.VlBuffer;
import ac.anticheat.vertex.checks.Check;
import ac.anticheat.vertex.checks.type.PacketCheck;
import ac.anticheat.vertex.config.CheckSettings;
import ac.anticheat.vertex.config.ChecksConfig;
import ac.anticheat.vertex.player.APlayer;
import ac.anticheat.vertex.utils.Config;
import ac.anticheat.vertex.utils.PacketUtil;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;

public class AimE extends Check implements PacketCheck {
    private final CheckSettings cfg;
    private final VlBuffer buffer = new VlBuffer();

    public AimE(APlayer aPlayer) {
        super("Aim", "(E)", aPlayer, false);
        this.cfg = ChecksConfig.get().get(this.getName());
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!isEnabled() || aPlayer.bukkitPlayer.isInsideVehicle() || Math.abs(aPlayer.rotationData.pitch) == 90 || aPlayer.rotationData.isCinematicRotation() || !aPlayer.actionData.inCombat())
            return;

        float deltaYaw = Math.abs(aPlayer.rotationData.deltaYaw);
        float deltaPitch = Math.abs(aPlayer.rotationData.deltaPitch);

        if (PacketUtil.isRotation(event)) {
            if (deltaPitch == 0) {
                if (deltaYaw > 80) {
                    buffer.fail(cfg.maxBuffer + 1);
                } else if (deltaYaw > 30) {
                    buffer.fail(3);
                } else if (deltaYaw > 10) {
                    buffer.fail(2);
                } else if (deltaYaw > 2) {
                    buffer.fail(1);
                }
            } else {
                buffer.setVl(buffer.getVl() - cfg.bufferDecrease);
            }

            if (deltaYaw == 0) {
                if (deltaPitch > 80) {
                    buffer.fail(cfg.maxBuffer + 1);
                } else if (deltaPitch > 30) {
                    buffer.fail(3);
                } else if (deltaPitch > 10) {
                    buffer.fail(2);
                } else if (deltaPitch > 2) {
                    buffer.fail(1);
                }
            } else {
                buffer.setVl(buffer.getVl() - cfg.bufferDecrease);
            }
        }
    }
}
