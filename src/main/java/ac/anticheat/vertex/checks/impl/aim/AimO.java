package ac.anticheat.vertex.checks.impl.aim;

import ac.anticheat.vertex.buffer.VlBuffer;
import ac.anticheat.vertex.checks.Check;
import ac.anticheat.vertex.checks.type.PacketCheck;
import ac.anticheat.vertex.player.APlayer;
import ac.anticheat.vertex.utils.Config;
import ac.anticheat.vertex.utils.PacketUtil;
import ac.anticheat.vertex.utils.kireiko.millennium.math.Statistics;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;

import java.util.ArrayList;
import java.util.List;

public class AimO extends Check implements PacketCheck {
    private final List<Double> deltaYaws = new ArrayList<>();
    private final List<Double> deltaPitches = new ArrayList<>();
    private final VlBuffer buffer = new VlBuffer();
    private List<Double> lastDeltaYaws;
    private List<Double> lastDeltaPitches;
    private double maxBuffer;
    private double bufferDecrease;

    public AimO(APlayer aPlayer) {
        super("AimO", aPlayer);
        this.maxBuffer = Config.getDouble(getConfigPath() + ".max-buffer", 3);
        this.bufferDecrease = Config.getDouble(getConfigPath() + ".buffer-decrease", 0.25);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!isEnabled() || !aPlayer.actionData.inCombat() || aPlayer.rotationData.isCinematicRotation()) return;

        if (PacketUtil.isRotation(event)) {
            double dy = Math.abs(aPlayer.rotationData.deltaYaw);
            double dp = Math.abs(aPlayer.rotationData.deltaPitch);

            deltaYaws.add(dy);
            deltaPitches.add(dp);

            if (deltaYaws.size() >= 40) {
                if (lastDeltaYaws != null && lastDeltaYaws.size() == deltaYaws.size()) {
                    double correlation = Statistics.spearmanCorrelation(deltaYaws, lastDeltaYaws);
                    if (correlation > 0.7) {
                        buffer.fail(2);
                    } else if (correlation > 0.5) {
                        buffer.fail(1);
                    } else {
                        buffer.setVl(buffer.getVl() - bufferDecrease);
                    }
                }

                lastDeltaYaws = new ArrayList<>(deltaYaws);
                deltaYaws.clear();
            }

            if (deltaPitches.size() >= 40) {
                if (lastDeltaPitches != null && lastDeltaPitches.size() == deltaPitches.size()) {
                    double correlation = Statistics.spearmanCorrelation(deltaPitches, lastDeltaPitches);
                    if (correlation > 0.7) {
                        buffer.fail(2);
                    } else if (correlation > 0.5) {
                        buffer.fail(1);
                    } else {
                        buffer.setVl(buffer.getVl() - bufferDecrease);
                    }
                }

                lastDeltaPitches = new ArrayList<>(deltaPitches);
                deltaPitches.clear();
            }

            if (buffer.getVl() > maxBuffer) {
                flag("spearman correlation");
            }
        }
    }

    @Override
    public void onReload() {
        this.maxBuffer = Config.getDouble(getConfigPath() + ".max-buffer", 2);
        this.bufferDecrease = Config.getDouble(getConfigPath() + ".buffer-decrease", 0.25);
    }
}
