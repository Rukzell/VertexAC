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

public class AimF extends Check implements PacketCheck {
    private final CheckSettings cfg;
    private final VlBuffer buffer = new VlBuffer();
    private final VlBuffer buffer2 = new VlBuffer();
    private final List<Double> deltaYaws = new ArrayList<>();
    private final List<Double> deltaPitches = new ArrayList<>();

    public AimF(APlayer aPlayer) {
        super("Aim", "(F)", aPlayer, false);
        this.cfg = ChecksConfig.get().get(this.getName());
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!isEnabled() || aPlayer.bukkitPlayer.isInsideVehicle() || aPlayer.rotationData.isCinematicRotation() || !aPlayer.actionData.inCombat())
            return;

        if (PacketUtil.isRotation(event)) {
            float deltaYaw = Math.abs(aPlayer.rotationData.deltaYaw);
            float deltaPitch = Math.abs(aPlayer.rotationData.deltaPitch);

            deltaYaws.add((double) Math.abs(deltaYaw));
            deltaPitches.add((double) Math.abs(deltaPitch));

            if (deltaYaws.size() >= 100) {
                int fails = MathUtil.tooSmallValues(deltaYaws, 1E-4);
                if (fails > 6) {
                    buffer.fail(cfg.maxBuffer + 1);
                    buffer.setVl(0);
                } else if (fails > 3) {
                    buffer.fail(cfg.maxBuffer);
                } else if (fails > 2) {
                    buffer.fail(1.5);
                } else if (fails > 1) {
                    buffer.fail(1);
                } else {
                    buffer.setVl(buffer.getVl() - cfg.bufferDecrease);
                }
                deltaYaws.clear();
            }

            if (deltaPitches.size() >= 100) {
                int fails = MathUtil.tooSmallValues(deltaPitches, 1E-4);
                if (fails > 6) {
                    buffer2.fail(cfg.maxBuffer + 1);
                    buffer2.setVl(0);
                } else if (fails > 3) {
                    buffer2.fail(cfg.maxBuffer);
                } else if (fails > 2) {
                    buffer2.fail(1.5);
                } else if (fails > 1) {
                    buffer2.fail(1);
                }
                deltaPitches.clear();
            }

            if (buffer.getVl() > cfg.maxBuffer || buffer2.getVl() > cfg.maxBuffer) {
                flag("too many small deltas");
                buffer.setVl(0);
            }
        }
    }
}
