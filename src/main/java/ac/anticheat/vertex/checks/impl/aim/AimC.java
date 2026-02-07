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

public class AimC extends Check implements PacketCheck {
    private final CheckSettings cfg;
    private final List<Double> deltaYaw = new ArrayList<>();
    private final List<Double> deltaPitch = new ArrayList<>();
    private final VlBuffer buffer = new VlBuffer();

    public AimC(APlayer aPlayer) {
        super("Aim", "(C)", aPlayer, false);
        this.cfg = ChecksConfig.get().get(this.getName());
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!isEnabled() || aPlayer.bukkitPlayer.isInsideVehicle() || aPlayer.rotationData.isCinematicRotation() || !aPlayer.actionData.inCombat())
            return;

        if (PacketUtil.isRotation(event)) {
            deltaYaw.add((double) Math.abs(aPlayer.rotationData.deltaYaw));
            if (deltaYaw.size() >= 10) {
                double disYaw = Statistics.getDistinct(deltaYaw);
                double avgYaw = Statistics.getAverage(deltaYaw);
                double disPitch = Statistics.getDistinct(deltaPitch);
                double avgPitch = Statistics.getAverage(deltaPitch);

                if (avgYaw > 2.5) {
                    if (disYaw < 7) {
                        buffer.fail(1.7);
                    } else if (disYaw < 8) {
                        buffer.fail(1);
                    } else {
                        buffer.setVl(buffer.getVl() - cfg.bufferDecrease);
                    }
                }

                if (avgPitch > 2.5) {
                    if (disPitch < 7) {
                        buffer.fail(1.7);
                    } else if (disPitch < 8) {
                        buffer.fail(1);
                    } else {
                        buffer.setVl(buffer.getVl() - cfg.bufferDecrease);
                    }
                }

                if (buffer.getVl() > cfg.maxBuffer) {
                    flag(String.format("disYaw=%.5f\navgYaw=%.5f", disYaw, avgYaw));
                    buffer.setVl(0);
                }

                deltaPitch.clear();
                deltaYaw.clear();
            }
        }
    }
}
