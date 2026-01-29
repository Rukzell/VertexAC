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

public class AimF extends Check implements PacketCheck {
    private final VlBuffer buffer = new VlBuffer();
    private final VlBuffer buffer2 = new VlBuffer();
    private final List<Double> deltaYaws = new ArrayList<>();
    private final List<Double> deltaPitches = new ArrayList<>();
    private double maxBuffer;
    private double bufferDecrease;

    public AimF(APlayer aPlayer) {
        super("AimF", aPlayer);
        this.maxBuffer = Config.getDouble(getConfigPath() + ".max-buffer", 1);
        this.bufferDecrease = Config.getDouble(getConfigPath() + ".buffer-decrease", 0.05);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!isEnabled() || aPlayer.bukkitPlayer.isInsideVehicle() || !aPlayer.actionData.inCombat() || aPlayer.rotationData.isCinematicRotation())
            return;

        if (PacketUtil.isRotation(event)) {
            float deltaYaw = Math.abs(aPlayer.rotationData.deltaYaw);
            float deltaPitch = Math.abs(aPlayer.rotationData.deltaPitch);

            deltaYaws.add((double) Math.abs(deltaYaw));
            deltaPitches.add((double) Math.abs(deltaPitch));

            if (deltaYaws.size() >= 100) {
                int fails = MathUtil.tooSmallValues(deltaYaws, 1E-4);
                if (fails > 4) {
                    buffer.fail(maxBuffer + 1);
                    buffer.setVl(0);
                } else if (fails > 3) {
                    buffer.fail(maxBuffer);
                } else if (fails > 2) {
                    buffer.fail(1.5);
                } else if (fails > 1) {
                    buffer.fail(1);
                } else {
                    buffer.setVl(buffer.getVl() - bufferDecrease);
                }
                deltaYaws.clear();
            }

            if (deltaPitches.size() >= 100) {
                int fails = MathUtil.tooSmallValues(deltaPitches, 1E-4);
                if (fails > 4) {
                    buffer2.fail(maxBuffer + 1);
                    buffer2.setVl(0);
                } else if (fails > 3) {
                    buffer2.fail(maxBuffer);
                } else if (fails > 2) {
                    buffer2.fail(1.5);
                } else if (fails > 1) {
                    buffer2.fail(1);
                }
                deltaPitches.clear();
            }

            if (buffer.getVl() > maxBuffer || buffer2.getVl() > maxBuffer) {
                flag("too many small deltas");
                buffer.setVl(0);
            }
        }
    }

    @Override
    public void onReload() {
        this.maxBuffer = Config.getDouble(getConfigPath() + ".max-buffer", 1);
        this.bufferDecrease = Config.getDouble(getConfigPath() + ".buffer-decrease", 0.05);
    }
}
