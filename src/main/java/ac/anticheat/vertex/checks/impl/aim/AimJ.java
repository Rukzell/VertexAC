package ac.anticheat.vertex.checks.impl.aim;

import ac.anticheat.vertex.buffer.VlBuffer;
import ac.anticheat.vertex.checks.Check;
import ac.anticheat.vertex.checks.type.PacketCheck;
import ac.anticheat.vertex.player.APlayer;
import ac.anticheat.vertex.utils.Config;
import ac.anticheat.vertex.utils.MathUtil;
import ac.anticheat.vertex.utils.PacketUtil;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;

import java.util.ArrayList;
import java.util.List;

public class AimJ extends Check implements PacketCheck {
    private final VlBuffer bufferYaw = new VlBuffer();
    private final VlBuffer bufferPitch = new VlBuffer();
    private final List<Double> deltaYaws = new ArrayList<>();
    private final List<Double> deltaPitches = new ArrayList<>();
    private double maxBuffer;
    private double bufferDecrease;

    public AimJ(APlayer aPlayer) {
        super("AimJ", aPlayer);
        this.maxBuffer = Config.getDouble(getConfigPath() + ".max-buffer", 2);
        this.bufferDecrease = Config.getDouble(getConfigPath() + ".buffer-decrease", 0.5);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!isEnabled() || !aPlayer.actionData.inCombat()) return;

        if (PacketUtil.isRotation(event)) {
            double deltaYaw = Math.abs(aPlayer.rotationData.deltaYaw);
            double deltaPitch = Math.abs(aPlayer.rotationData.deltaPitch);

            deltaYaws.add(deltaYaw);
            deltaPitches.add(deltaPitch);

            if (deltaYaws.size() >= 64) {
                double ratio = MathUtil.highFreqRatio(deltaPitches);
                if (ratio > 0.5) {
                    bufferYaw.fail(2);
                } else if (ratio > 0.3) {
                    bufferYaw.fail(1);
                } else {
                    bufferYaw.setVl(bufferYaw.getVl() - bufferDecrease);
                }
                deltaYaws.remove(0);
            }

            if (deltaPitches.size() >= 64) {
                double ratio = MathUtil.highFreqRatio(deltaPitches);
                if (ratio > 0.5) {
                    bufferPitch.fail(2);
                } else if (ratio > 0.3) {
                    bufferPitch.fail(1);
                } else {
                    bufferPitch.setVl(bufferPitch.getVl() - bufferDecrease);
                }
                deltaPitches.remove(0);
            }

            if (bufferYaw.getVl() > maxBuffer || bufferPitch.getVl() > maxBuffer) {
                flag();
                bufferYaw.setVl(0);
                bufferPitch.setVl(0);
            }
        }
    }

    @Override
    public void onReload() {
        this.maxBuffer = Config.getDouble(getConfigPath() + ".max-buffer", 2);
        this.bufferDecrease = Config.getDouble(getConfigPath() + ".buffer-decrease", 0.5);
    }
}
