package ac.anticheat.vertex.checks.impl.aim;

import ac.anticheat.vertex.checks.Check;
import ac.anticheat.vertex.checks.type.PacketCheck;
import ac.anticheat.vertex.player.APlayer;
import ac.anticheat.vertex.utils.Config;
import ac.anticheat.vertex.utils.PacketUtil;
import ac.anticheat.vertex.utils.kireiko.millennium.math.Statistics;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;

import java.util.ArrayList;
import java.util.List;

public class AimC extends Check implements PacketCheck {
    public AimC(APlayer aPlayer) {
        super("AimC", aPlayer);
        this.maxBuffer = Config.getInt(getConfigPath() + ".max-buffer", 2);
        this.bufferDecrease = Config.getDouble(getConfigPath() + ".buffer-decrease", 0.25);
    }

    private double buffer1;
    private double buffer2;
    private double maxBuffer;
    private double bufferDecrease;
    private final List<Double> deltaYaw = new ArrayList<>();
    private final List<Double> deltaPitch = new ArrayList<>();
    private final int maxHistory = 10;

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!isEnabled() || aPlayer.bukkitPlayer.isInsideVehicle() || !aPlayer.actionData.inCombat() || aPlayer.rotationData.isCinematicRotation())
            return;

        if (PacketUtil.isRotation(event)) {
            deltaYaw.add((double) Math.abs(aPlayer.rotationData.deltaYaw));
            if (deltaYaw.size() >= maxHistory) {
                double disYaw = Statistics.getDistinct(deltaYaw);
                double avgYaw = Statistics.getAverage(deltaYaw);
                double disPitch = Statistics.getDistinct(deltaPitch);
                double avgPitch = Statistics.getAverage(deltaPitch);
                if (disYaw < 8 && avgYaw > 2.5D) {
                    buffer1++;
                    if (buffer1 > maxBuffer) {
                        flag(String.format("disYaw=%.5f\navgYaw=%.5f", disYaw, avgYaw));
                        buffer1 = 0;
                    }
                } else {
                    if (buffer1 > 0) buffer1 -= bufferDecrease;
                }

                if (disPitch < 8 && avgPitch > 2.5D) {
                    buffer2++;
                    if (buffer2 > maxBuffer) {
                        flag(String.format("disPitch=%.5f\navgPitch=%.5f", disPitch, avgPitch));
                        buffer2 = 0;
                    }
                } else {
                    if (buffer2 > 0) buffer2 -= bufferDecrease;
                }

                deltaPitch.clear();
                deltaYaw.clear();
            }
        }
    }

    @Override
    public void onReload() {
        this.maxBuffer = Config.getInt(getConfigPath() + ".max-buffer", 2);
        this.bufferDecrease = Config.getDouble(getConfigPath() + ".buffer-decrease", 0.25);
    }
}
