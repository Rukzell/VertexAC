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
    private final List<Double> deltaYaws1 = new ArrayList<>();
    public AimM(APlayer aPlayer) {
        super("AimM", aPlayer);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!isEnabled() || !PacketUtil.isRotation(event)) return;
        if (!aPlayer.actionData.inCombat() || aPlayer.rotationData.isCinematicRotation()) return;

        float yaw = aPlayer.rotationData.deltaYaw;
        deltaYaws.add(yaw);

        if (deltaYaws.size() >= 100) {
            List<Float> jiffDelta = Statistics.getJiffDelta(deltaYaws, 4);
            int distinct = Statistics.getDistinct(jiffDelta);

            float distinctRank = (float) distinct / 60f;
            deltaYaws1.add((double) distinctRank);

            deltaYaws.clear();
        }

        if (deltaYaws1.size() >= 10) {
            double avg = Statistics.getAverage(deltaYaws1);
            int normal = 0;
            for (double d : deltaYaws1) {
                if (d > 0.97) normal++;
            }

            if (avg < 0.95 && normal < 4) {
                flag();
            }

            deltaYaws1.clear();
        }
    }
}
