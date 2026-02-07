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

public class AimH extends Check implements PacketCheck {
    private final CheckSettings cfg;
    private final VlBuffer buffer = new VlBuffer();
    private final List<Double> deltaYaws = new ArrayList<>();
    private final List<Double> deltaPitches = new ArrayList<>();

    public AimH(APlayer aPlayer) {
        super("Aim", "(H)", aPlayer, false);
        this.cfg = ChecksConfig.get().get(this.getName());
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!isEnabled() || aPlayer.rotationData.isCinematicRotation() || !aPlayer.actionData.inCombat()) return;

        if (PacketUtil.isRotation(event)) {
            double dy = Math.abs(aPlayer.rotationData.deltaYaw);
            double dp = Math.abs(aPlayer.rotationData.deltaPitch);

            deltaYaws.add(dy);
            deltaPitches.add(dp);

            if (deltaYaws.size() >= 80) {
                double runszs = MathUtil.runsZScore(deltaYaws);
                if (runszs > 3) {
                    buffer.fail(2);
                } else if (runszs > 1.5) {
                    buffer.fail(1);
                } else {
                    buffer.setVl(buffer.getVl() - cfg.bufferDecrease);
                }
                deltaYaws.clear();
            }

            if (deltaPitches.size() >= 80) {
                double runszs = MathUtil.runsZScore(deltaPitches);
                if (runszs > 3) {
                    buffer.fail(2);
                } else if (runszs > 1.5) {
                    buffer.fail(1);
                } else {
                    buffer.setVl(buffer.getVl() - cfg.bufferDecrease);
                }
                deltaPitches.clear();
            }

            if (buffer.getVl() > cfg.maxBuffer) {
                flag("runsZScore");
                buffer.setVl(0);
            }
        }
    }
}