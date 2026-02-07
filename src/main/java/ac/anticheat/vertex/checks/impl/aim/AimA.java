package ac.anticheat.vertex.checks.impl.aim;

import ac.anticheat.vertex.buffer.VlBuffer;
import ac.anticheat.vertex.checks.Check;
import ac.anticheat.vertex.checks.type.PacketCheck;
import ac.anticheat.vertex.config.CheckSettings;
import ac.anticheat.vertex.config.ChecksConfig;
import ac.anticheat.vertex.player.APlayer;
import ac.anticheat.vertex.utils.Config;
import ac.anticheat.vertex.utils.EvictingList;
import ac.anticheat.vertex.utils.PacketUtil;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;

import java.util.List;

public class AimA extends Check implements PacketCheck {
    private final CheckSettings cfg;
    private final List<Double> deltaYaws = new EvictingList<>(3);
    private final VlBuffer buffer = new VlBuffer();

    public AimA(APlayer aPlayer) {
        super("Aim", "(A)", aPlayer, false);
        this.cfg = ChecksConfig.get().get(this.getName());
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!isEnabled() || aPlayer.bukkitPlayer.isInsideVehicle() || !aPlayer.actionData.inCombat())
            return;

        if (PacketUtil.isRotation(event)) {
            deltaYaws.add((double) Math.abs(aPlayer.rotationData.deltaYaw));
            if (deltaYaws.size() == 3) {
                double first = deltaYaws.get(0);
                double last = deltaYaws.get(2);
                double mid = deltaYaws.get(1);
                double min = 1.8;
                boolean flag = first < min && last < min;

                if (flag && mid > 50f && mid < 360f) {
                    buffer.fail(2);
                } else if (flag && mid > 35f && mid <= 50f) {
                    buffer.fail(1);
                } else {
                    buffer.setVl(buffer.getVl() - cfg.bufferDecrease);
                }

                if (buffer.getVl() > cfg.maxBuffer) {
                    flag(String.format("first=%.5f\nmid=%.5f\nlast=%.5f", first, mid, last));
                    buffer.setVl(0);
                }
            }
        }
    }
}
