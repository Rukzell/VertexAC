package ac.anticheat.vertex.checks.impl.aim;

import ac.anticheat.vertex.buffer.VlBuffer;
import ac.anticheat.vertex.checks.Check;
import ac.anticheat.vertex.checks.type.PacketCheck;
import ac.anticheat.vertex.config.CheckSettings;
import ac.anticheat.vertex.config.ChecksConfig;
import ac.anticheat.vertex.player.APlayer;
import ac.anticheat.vertex.utils.MathUtil;
import ac.anticheat.vertex.utils.PacketUtil;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;

import java.util.ArrayList;
import java.util.List;

public class AimJ extends Check implements PacketCheck {
    private final CheckSettings cfg;
    private final VlBuffer bufferYaw = new VlBuffer();
    private final VlBuffer bufferPitch = new VlBuffer();
    private final List<Double> deltaYaws = new ArrayList<>();
    private final List<Double> deltaPitches = new ArrayList<>();

    public AimJ(APlayer aPlayer) {
        super("Aim", "(J)", aPlayer, false);
        this.cfg = ChecksConfig.get().get(this.getName());
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!isEnabled() || !aPlayer.actionData.inCombat()) return;

        if (PacketUtil.isRotation(event)) {
            double deltaYaw = Math.abs(aPlayer.rotationData.deltaYaw);
            double deltaPitch = Math.abs(aPlayer.rotationData.deltaPitch);

            deltaYaws.add(deltaYaw);
            deltaPitches.add(deltaPitch);

            if (deltaYaws.size() >= 128) {
                double ratio = MathUtil.highFreqRatio(deltaPitches);
                double proba = probability(ratio, 0.2, 0.5, 8, false);

                if (proba >= cfg.probaThreshold) {
                    flag("ProbabilityX=" + proba);
                }
//                if (ratio > 0.5) {
//                    bufferYaw.fail(2);
//                } else if (ratio > 0.36) {
//                    bufferYaw.fail(1);
//                } else {
//                    bufferYaw.setVl(bufferYaw.getVl() - cfg.bufferDecrease);
//                }
                deltaYaws.remove(0);
            }

            if (deltaPitches.size() >= 128) {
                double ratio = MathUtil.highFreqRatio(deltaPitches);
                double proba = probability(ratio, 0.2, 0.5, 8, false);

                if (proba >= cfg.probaThreshold) {
                    flag("ProbabilityY=" + proba);
                }
//                if (ratio > 0.5) {
//                    bufferPitch.fail(2);
//                } else if (ratio > 0.36) {
//                    bufferPitch.fail(1);
//                } else {
//                    bufferPitch.setVl(bufferPitch.getVl() - cfg.bufferDecrease);
//                }
                deltaPitches.remove(0);
            }

            if (bufferYaw.getVl() > cfg.maxBuffer || bufferPitch.getVl() > cfg.maxBuffer) {
                flag();
                bufferYaw.setVl(0);
                bufferPitch.setVl(0);
            }
        }
    }
}
