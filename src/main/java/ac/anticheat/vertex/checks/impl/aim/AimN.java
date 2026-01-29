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

public class AimN extends Check implements PacketCheck {
    private final List<Double> deltaYaws = new ArrayList<>();
    private final List<Double> deltaPitches = new ArrayList<>();

    public AimN(APlayer aPlayer) {
        super("AimN", aPlayer);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!isEnabled() || !aPlayer.actionData.inCombat() || aPlayer.rotationData.isCinematicRotation()) return;

        if (PacketUtil.isRotation(event)) {
            double dy = Math.abs(aPlayer.rotationData.deltaYaw);
            double dp = Math.abs(aPlayer.rotationData.deltaPitch);

            deltaYaws.add(dy);
            deltaPitches.add(dp);

            if (deltaYaws.size() >= 30) {
                double k = Statistics.getKurtosis(deltaYaws);
                double skewness = Statistics.getSkewness(deltaYaws);
                if (k < -0.12 && skewness > 1.1) {
                    flag("k=" + k + "\nskewness=" + skewness);
                }
                deltaYaws.clear();
            }

            if (deltaPitches.size() >= 30) {
                double k = Statistics.getKurtosis(deltaPitches);
                double skewness = Statistics.getSkewness(deltaPitches);
                if (k < -0.12 && skewness > 1.1) {
                    flag("k=" + k + "\nskewness=" + skewness);
                }
                deltaPitches.clear();
            }
        }
    }
}
