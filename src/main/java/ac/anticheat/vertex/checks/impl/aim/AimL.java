package ac.anticheat.vertex.checks.impl.aim;

import ac.anticheat.vertex.buffer.VlBuffer;
import ac.anticheat.vertex.checks.Check;
import ac.anticheat.vertex.checks.type.PacketCheck;
import ac.anticheat.vertex.config.CheckSettings;
import ac.anticheat.vertex.config.ChecksConfig;
import ac.anticheat.vertex.player.APlayer;
import ac.anticheat.vertex.utils.MathUtil;
import ac.anticheat.vertex.utils.PacketUtil;
import ac.anticheat.vertex.utils.kireiko.millennium.math.Statistics;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;

import java.util.ArrayList;
import java.util.List;

public class AimL extends Check implements PacketCheck {
    private final CheckSettings cfg;
    private final List<Double> deltaYaws = new ArrayList<>();
    private final List<Double> deltaPitches = new ArrayList<>();
    private final VlBuffer buffer = new VlBuffer();
    private final VlBuffer buffer2 = new VlBuffer();

    public AimL(APlayer aPlayer) {
        super("Aim", "(L)", aPlayer, false);
        this.cfg = ChecksConfig.get().get(this.getName());
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!isEnabled() || aPlayer.rotationData.isCinematicRotation() || !aPlayer.actionData.inCombat()) return;

        if (PacketUtil.isRotation(event)) {
            double deltaYaw = aPlayer.rotationData.deltaYaw;
            double deltaPitch = aPlayer.rotationData.deltaPitch;
            deltaYaws.add(deltaYaw);
            deltaPitches.add(deltaPitch);

            if (deltaYaws.size() > 20) {
                int signChanges = MathUtil.signChanges(deltaYaws);
                double avg = Statistics.getAverage(deltaYaws);

                if (signChanges > 15 && avg < 1.5) {
                    buffer.fail(2);
                } else if (signChanges > 12) {
                    buffer.fail(0.9);
                } else {
                    buffer.setVl(buffer.getVl() - cfg.bufferDecrease);
                }
                deltaYaws.remove(0);
            }

            if (deltaPitches.size() > 20) {
                int signChanges = MathUtil.signChanges(deltaPitches);
                double avg = Statistics.getAverage(deltaPitches);

                if (signChanges > 15 && avg < 1.5) {
                    buffer.fail(2);
                } else if (signChanges > 12) {
                    buffer.fail(0.9);
                } else {
                    buffer.setVl(buffer.getVl() - cfg.bufferDecrease);
                }
                deltaPitches.remove(0);
            }

            if (buffer.getVl() > cfg.maxBuffer || buffer2.getVl() > cfg.maxBuffer) {
                flag("too many sign changes");
                buffer.setVl(0);
            }
        }
    }
}
