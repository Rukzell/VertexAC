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

public class AimO extends Check implements PacketCheck {
    private final CheckSettings cfg;
    private final List<Double> deltaYaws = new ArrayList<>();
    private final List<Double> deltaPitches = new ArrayList<>();
    private final VlBuffer buffer = new VlBuffer();

    public AimO(APlayer aPlayer) {
        super("Aim", "(O)", aPlayer, false);
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

            if (deltaYaws.size() >= 20) {
                double std = Statistics.getStandardDeviation(deltaYaws);
                double avg = Statistics.getAverage(deltaYaws);

                if (std < 0.9 && avg > 1.9) {
                    flag("stddevX=" + std + "\navgX=" + avg);
                }

                deltaYaws.clear();
            }

            if (deltaPitches.size() >= 20) {
                double std = Statistics.getStandardDeviation(deltaPitches);
                double avg = Statistics.getAverage(deltaPitches);

                if (std < 0.9 && avg > 1.9) {
                    flag("stddevY=" + std + "\navgY=" + avg);
                }

                deltaPitches.clear();
            }
        }
    }
}
