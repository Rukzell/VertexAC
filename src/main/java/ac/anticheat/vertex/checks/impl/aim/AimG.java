package ac.anticheat.vertex.checks.impl.aim;

import ac.anticheat.vertex.checks.Check;
import ac.anticheat.vertex.checks.type.PacketCheck;
import ac.anticheat.vertex.player.APlayer;
import ac.anticheat.vertex.utils.Config;
import ac.anticheat.vertex.utils.MathUtil;
import ac.anticheat.vertex.utils.PacketUtil;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;

import java.util.ArrayList;
import java.util.List;

public class AimG extends Check implements PacketCheck {
    public AimG(APlayer aPlayer) {
        super("AimG", aPlayer);
        this.maxBuffer = Config.getDouble(getConfigPath() + ".max-buffer", 2);
        this.bufferDecrease = Config.getDouble(getConfigPath() + ".buffer-decrease", 0.25);
    }

    private double buffer;
    private double maxBuffer;
    private double bufferDecrease;
    private List<Double> deltaYaws = new ArrayList<>();
    private List<Double> deltaPitches = new ArrayList<>();

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!isEnabled() || aPlayer.bukkitPlayer.isInsideVehicle() || !aPlayer.actionData.inCombat() || aPlayer.rotationData.isCinematicRotation()) {
            return;
        }

        if (PacketUtil.isRotation(event)) {
            double deltaYaw = Math.abs(aPlayer.rotationData.deltaYaw);
            double deltaPitch = Math.abs(aPlayer.rotationData.deltaPitch);

            deltaYaws.add(deltaYaw);
            deltaPitches.add(deltaPitch);

            if (deltaYaws.size() == 20) {
                double ac = MathUtil.autocorrelation(deltaYaws, 1);
                boolean jitter = MathUtil.hasJitterBreaks(deltaYaws, 0.5);

                if ((ac < 0.05 || ac > 0.95) && !jitter) {
                    buffer++;
                    if (buffer > maxBuffer) {
                        flag(String.format("yaw\nautocorr=%.5f", ac));
                        buffer = 0;
                    }
                } else {
                    if (buffer > 0) buffer -= bufferDecrease;
                }

                deltaYaws.remove(0);
            }

            if (deltaPitches.size() > 20) {
                double ac = MathUtil.autocorrelation(deltaPitches, 1);
                boolean jitter = MathUtil.hasJitterBreaks(deltaPitches, 0.5);

                if ((ac < 0.05 || ac > 0.95) && !jitter) {
                    buffer++;
                    if (buffer > maxBuffer) {
                        flag(String.format("pitch\nautocorr=%.5f", ac));
                        buffer = 0;
                    }
                } else {
                    if (buffer > 0) buffer -= bufferDecrease;
                }

                deltaPitches.remove(0);
            }
        }
    }

    @Override
    public void onReload() {
        maxBuffer = Config.getDouble(getConfigPath() + ".max-buffer", 1);
        bufferDecrease = Config.getDouble(getConfigPath() + ".buffer-decrease", 0.05);
    }
}