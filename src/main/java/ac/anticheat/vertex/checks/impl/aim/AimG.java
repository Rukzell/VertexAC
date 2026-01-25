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

public class AimG extends Check implements PacketCheck {
    private final VlBuffer buffer = new VlBuffer();
    private double maxBuffer;
    private double bufferDecrease;
    private final List<Double> deltaYaws = new ArrayList<>();
    private final List<Double> deltaPitches = new ArrayList<>();
    public AimG(APlayer aPlayer) {
        super("AimG", aPlayer);
        this.maxBuffer = Config.getDouble(getConfigPath() + ".max-buffer", 3);
        this.bufferDecrease = Config.getDouble(getConfigPath() + ".buffer-decrease", 0.5);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!isEnabled() || aPlayer.bukkitPlayer.isInsideVehicle() || !aPlayer.actionData.inCombat() || aPlayer.rotationData.isCinematicRotation()) {
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
                    buffer.setVl(buffer.getVl() - bufferDecrease);
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
                    buffer.setVl(buffer.getVl() - bufferDecrease);
                }
                deltaPitches.clear();
            }

            if (buffer.getVl() > maxBuffer) {
                flag("failed jarque-bera test");
                buffer.setVl(0);
            }
        }
    }

    @Override
    public void onReload() {
        maxBuffer = Config.getDouble(getConfigPath() + ".max-buffer", 3);
        bufferDecrease = Config.getDouble(getConfigPath() + ".buffer-decrease", 0.5);
    }
}