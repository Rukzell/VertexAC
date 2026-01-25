package ac.anticheat.vertex.checks.impl.aim;

import ac.anticheat.vertex.buffer.VlBuffer;
import ac.anticheat.vertex.checks.Check;
import ac.anticheat.vertex.checks.type.PacketCheck;
import ac.anticheat.vertex.player.APlayer;
import ac.anticheat.vertex.utils.Config;
import ac.anticheat.vertex.utils.MathUtil;
import ac.anticheat.vertex.utils.PacketUtil;
import ac.anticheat.vertex.utils.kireiko.millennium.math.Statistics;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;

import java.util.ArrayList;
import java.util.List;

public class AimL extends Check implements PacketCheck {
    private final List<Double> deltaYaws = new ArrayList<>();
    private final List<Double> deltaPitches = new ArrayList<>();
    private double maxBuffer;
    private double bufferDecrease;
    private final VlBuffer buffer = new VlBuffer();
    public AimL(APlayer aPlayer) {
        super("AimL", aPlayer);
        this.maxBuffer = Config.getDouble(getConfigPath() + ".max-buffer", 1);
        this.bufferDecrease = Config.getDouble(getConfigPath() + ".buffer-decrease", 0.25);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!isEnabled() || !aPlayer.actionData.inCombat() || aPlayer.rotationData.isCinematicRotation()) return;

        if (PacketUtil.isRotation(event)) {
            double deltaYaw = aPlayer.rotationData.deltaYaw;
            double deltaPitch = aPlayer.rotationData.deltaPitch;
            deltaYaws.add(deltaYaw);
            deltaPitches.add(deltaPitch);

            if (deltaYaws.size() > 20) {
                int signChanges = MathUtil.signChanges(deltaYaws);
                double stddev = Math.abs(Statistics.getStandardDeviation(deltaYaws));

                if (signChanges > 15 && stddev < 2.5) {
                    buffer.fail(2);
                } else if (signChanges > 12 && stddev < 4) {
                    buffer.fail(1);
                } else {
                    buffer.setVl(buffer.getVl() - bufferDecrease);
                }
                deltaYaws.remove(0);
            }

            if (deltaPitches.size() > 20) {
                int signChanges = MathUtil.signChanges(deltaPitches);
                double stddev = Math.abs(Statistics.getStandardDeviation(deltaPitches));

                if (signChanges > 15 && stddev < 2.5) {
                    buffer.fail(2);
                } else if (signChanges > 12 && stddev < 4) {
                    buffer.fail(1);
                } else {
                    buffer.setVl(buffer.getVl() - bufferDecrease);
                }
                deltaPitches.remove(0);
            }

            if (buffer.getVl() > maxBuffer) {
                flag("too many sign changes");
                buffer.setVl(0);
            }
        }
    }

    @Override
    public void onReload() {
        maxBuffer = Config.getDouble(getConfigPath() + ".max-buffer", 1);
        bufferDecrease = Config.getDouble(getConfigPath() + ".buffer-decrease", 0.25);
    }
}
