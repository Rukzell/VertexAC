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

public class AimG extends Check implements PacketCheck {
    private final CheckSettings cfg;
    private final VlBuffer buffer = new VlBuffer();
    private final List<Double> deltaYaws = new ArrayList<>();
    private final List<Double> deltaPitches = new ArrayList<>();

    public AimG(APlayer aPlayer) {
        super("Aim", "(G)", aPlayer, false);
        this.cfg = ChecksConfig.get().get(this.getName());
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!isEnabled() || aPlayer.bukkitPlayer.isInsideVehicle() || aPlayer.rotationData.isCinematicRotation() || !aPlayer.actionData.inCombat()) {
            return;
        }

        if (PacketUtil.isRotation(event)) {
            double dy = Math.abs(aPlayer.rotationData.deltaYaw);
            double dp = Math.abs(aPlayer.rotationData.deltaPitch);

            deltaYaws.add(dy);
            deltaPitches.add(dp);

            if (deltaYaws.size() >= 100) {
                double jarqueBera = MathUtil.jarqueBera(deltaYaws);
                if (jarqueBera > 4000) {
                    buffer.fail(2);
                } else if (jarqueBera > 3000) {
                    buffer.fail(1);
                } else {
                    buffer.setVl(buffer.getVl() - cfg.bufferDecrease);
                }
                deltaYaws.clear();
            }

            if (deltaPitches.size() >= 100) {
                double jarqueBera = MathUtil.jarqueBera(deltaPitches);
                if (jarqueBera > 4000) {
                    buffer.fail(2);
                } else if (jarqueBera > 3000) {
                    buffer.fail(1);
                } else {
                    buffer.setVl(buffer.getVl() - cfg.bufferDecrease);
                }
                deltaPitches.clear();
            }

            if (buffer.getVl() > cfg.maxBuffer) {
                flag("failed jarque-bera test");
                buffer.setVl(0);
            }
        }
    }
}