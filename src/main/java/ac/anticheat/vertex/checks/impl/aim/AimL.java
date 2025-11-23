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

public class AimL extends Check implements PacketCheck {
    private final List<Double> deltaYaws = new ArrayList<>();
    private final List<Double> deltaPitches = new ArrayList<>();
    private double maxBuffer;
    private double bufferDecrease;
    private double buffer;
    public AimL(APlayer aPlayer) {
        super("AimL", aPlayer);
        this.maxBuffer = Config.getDouble(getConfigPath() + ".max-buffer", 1);
        this.bufferDecrease = Config.getDouble(getConfigPath() + ".buffer-decrease", 0.25);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!isEnabled() || !aPlayer.actionData.inCombat() || aPlayer.rotationData.isCinematicRotation()) return;

        if (PacketUtil.isRotation(event)) {
            double deltaYaw = Math.abs(aPlayer.rotationData.deltaYaw);
            double deltaPitch = Math.abs(aPlayer.rotationData.deltaPitch);
            deltaYaws.add(deltaYaw);
            deltaPitches.add(deltaPitch);

            if (deltaYaws.size() > 20) {
                double ac1 = MathUtil.autocorrelation(deltaYaws, 1);
                double ac2 = MathUtil.autocorrelation(deltaYaws, 2);

                if (Math.abs(ac1) < 0.037 && Math.abs(ac2) < 0.037) {
                    buffer++;
                    if (buffer > maxBuffer) {
                        flag(String.format("yaw\nac1=%.5f\nac2=%.5f", ac1, ac2));
                        buffer = 0;
                    }
                } else {
                    if (buffer > 0) buffer -= bufferDecrease;
                }
                deltaYaws.remove(0);
            }

            if (deltaPitches.size() > 20) {
                double ac1 = MathUtil.autocorrelation(deltaPitches, 1);
                double ac2 = MathUtil.autocorrelation(deltaPitches, 2);

                if (Math.abs(ac1) < 0.037 && Math.abs(ac2) < 0.037) {
                    buffer++;
                    if (buffer > maxBuffer) {
                        flag(String.format("pitch\nac1=%.5f\nac2=%.5f", ac1, ac2));
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
        bufferDecrease = Config.getDouble(getConfigPath() + ".buffer-decrease", 0.25);
    }
}
