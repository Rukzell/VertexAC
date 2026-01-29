package ac.anticheat.vertex.checks.impl.aim;

import ac.anticheat.vertex.checks.Check;
import ac.anticheat.vertex.checks.type.PacketCheck;
import ac.anticheat.vertex.player.APlayer;
import ac.anticheat.vertex.utils.PacketUtil;
import ac.anticheat.vertex.utils.kireiko.millennium.math.Statistics;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;

import java.util.ArrayList;
import java.util.List;

public class AimM extends Check implements PacketCheck {
    private final List<Float> deltaYaws = new ArrayList<>();
    private final List<Double> kurtosis = new ArrayList<>();

    public AimM(APlayer aPlayer) {
        super("AimM", aPlayer);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!isEnabled() || !PacketUtil.isRotation(event)) return;
        if (!aPlayer.actionData.inCombat() || aPlayer.rotationData.isCinematicRotation()) return;

        float yaw = aPlayer.rotationData.deltaYaw;
        deltaYaws.add(yaw);

        if (deltaYaws.size() > 30) {
            double k = Statistics.getKurtosis(deltaYaws);
            kurtosis.add(k);
            deltaYaws.clear();
        }

        if (kurtosis.size() >= 20) {
            double stddev = Math.abs(Statistics.getStandardDeviation(kurtosis));
            if (stddev < 1) {
                flag("stddev=" + stddev);
            }
            kurtosis.clear();
        }
    }
}
