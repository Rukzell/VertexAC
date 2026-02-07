package ac.anticheat.vertex.checks.impl.aim;

import ac.anticheat.vertex.buffer.VlBuffer;
import ac.anticheat.vertex.checks.Check;
import ac.anticheat.vertex.checks.type.PacketCheck;
import ac.anticheat.vertex.config.CheckSettings;
import ac.anticheat.vertex.config.ChecksConfig;
import ac.anticheat.vertex.player.APlayer;
import ac.anticheat.vertex.utils.Config;
import ac.anticheat.vertex.utils.PacketUtil;
import ac.anticheat.vertex.utils.kireiko.millennium.math.Statistics;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;

import java.util.ArrayList;
import java.util.List;

public class AimN extends Check implements PacketCheck {
    private final CheckSettings cfg;

    public AimN(APlayer aPlayer) {
        super("Aim", "(N)", aPlayer, false);
        this.cfg = ChecksConfig.get().get(this.getName());
    }

    private final VlBuffer buffer1 = new VlBuffer();
    private final VlBuffer buffer2 = new VlBuffer();

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!isEnabled() || aPlayer.rotationData.isCinematicRotation() || !aPlayer.actionData.inCombat()) return;

        if (PacketUtil.isRotation(event)) {
            double deltaYaw = Math.abs(aPlayer.rotationData.deltaYaw);
            double deltaPitch = Math.abs(aPlayer.rotationData.deltaPitch);

            double stepYaw = aPlayer.rotationData.modeX;
            double stepPitch = aPlayer.rotationData.modeY;

            if (stepYaw <= 0.0 || stepPitch <= 0.0) return;
            if (deltaYaw < 0.1 || deltaYaw > 20.0) return;
            if (deltaPitch < 0.1 || deltaPitch > 20.0) return;

            double modPitch = deltaPitch % stepPitch;

            double rYaw = (deltaYaw % stepYaw) / stepYaw;
            double fracYaw = Math.abs(Math.floor(rYaw) - rYaw);

            double rPitch = (modPitch % stepPitch) / stepPitch;
            double fracPitch = Math.abs(Math.floor(rPitch) - rPitch);

            boolean invalidYaw = fracYaw < 1E-4 && deltaYaw > stepYaw * 3.0 && fracYaw != 0;
            boolean invalidPitch = fracPitch < 1E-4 && deltaPitch > stepPitch * 3.0 && fracPitch != 0;

            if (invalidYaw) {
                buffer1.fail(1);
                if (buffer1.getVl() > 6) {
                    flag("X\nfrac=" + fracYaw + "\ndelta=" + deltaYaw + "\nstep=" + stepYaw);
                }
            } else {
                buffer1.setVl(0);
            }

            if (invalidPitch) {
                buffer2.fail(1);
                if (buffer2.getVl() > 6) {
                    flag("Y\nfrac=" + fracPitch + "\ndelta=" + deltaPitch + "\nstep=" + stepPitch + "\n");
                }
            } else {
                buffer2.setVl(0);
            }
        }
    }
}
