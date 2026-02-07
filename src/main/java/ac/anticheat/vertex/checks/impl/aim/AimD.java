package ac.anticheat.vertex.checks.impl.aim;

import ac.anticheat.vertex.checks.Check;
import ac.anticheat.vertex.checks.type.PacketCheck;
import ac.anticheat.vertex.player.APlayer;
import ac.anticheat.vertex.utils.PacketUtil;
import ac.anticheat.vertex.utils.kireiko.millennium.math.Statistics;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;

import java.util.ArrayList;
import java.util.List;

public class AimD extends Check implements PacketCheck {
    private final List<Double> deltaYaws = new ArrayList<>();

    public AimD(APlayer aPlayer) {
        super("Aim", "(D)", aPlayer, false);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!isEnabled() || aPlayer.bukkitPlayer.isInsideVehicle() || aPlayer.rotationData.isCinematicRotation() || !aPlayer.actionData.inCombat())
            return;

        if (PacketUtil.isRotation(event)) {
            double deltaYaw = aPlayer.rotationData.deltaYaw;
            if ((deltaYaw > 1.8 && aPlayer.rotationData.deltaPitch > 1.8)) {
                deltaYaws.add(deltaYaw);
            }
            if (deltaYaws.size() > 20) {
                List<Float> jiff = Statistics.getJiffDelta(deltaYaws, 1);
                float jiff1 = 15959;
                float jiff2 = 15795;
                for (float i : jiff) {
                    if (i == 0 && jiff1 == 0 && jiff2 == 0) {
                        flag();
                        break;
                    }
                    jiff2 = jiff1;
                    jiff1 = i;
                }
                deltaYaws.clear();
            }
        }
    }
}