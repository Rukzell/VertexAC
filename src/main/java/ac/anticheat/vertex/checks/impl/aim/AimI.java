package ac.anticheat.vertex.checks.impl.aim;

import ac.anticheat.vertex.checks.Check;
import ac.anticheat.vertex.checks.type.PacketCheck;
import ac.anticheat.vertex.player.APlayer;
import ac.anticheat.vertex.utils.Config;
import ac.anticheat.vertex.utils.PacketUtil;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;

public class AimI extends Check implements PacketCheck {
    private int yawStreak;
    private int pitchStreak;
    private double buffer1;
    private double buffer2;
    private double maxBuffer;
    private double bufferDecrease;
    public AimI(APlayer aPlayer) {
        super("AimI", aPlayer);
        maxBuffer = Config.getDouble(getConfigPath() + ".max-buffer", 1);
        bufferDecrease = Config.getDouble(getConfigPath() + ".buffer-decrease", 0.15);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!isEnabled() || aPlayer.bukkitPlayer.isInsideVehicle() || !aPlayer.actionData.inCombat() || aPlayer.rotationData.isCinematicRotation())
            return;

        if (PacketUtil.isRotation(event)) {
            float deltaYaw = Math.abs(aPlayer.rotationData.deltaYaw);
            float deltaPitch = Math.abs(aPlayer.rotationData.deltaPitch);
            float lastDeltaYaw = Math.abs(aPlayer.rotationData.lastDeltaYaw);
            float lastDeltaPitch = Math.abs(aPlayer.rotationData.lastDeltaPitch);

            if (deltaYaw == lastDeltaYaw && deltaYaw != 0) {
                yawStreak++;
                if (yawStreak > 2) {
                    buffer1++;
                    if (buffer1 > maxBuffer) {
                        flag(String.format("streakYaw=%d", yawStreak));
                        buffer1 = 0;
                    }
                } else {
                    if (buffer1 > 0) buffer1 -= bufferDecrease;
                }
            } else {
                yawStreak = 0;
            }

            if (deltaPitch == lastDeltaPitch && deltaPitch != 0) {
                pitchStreak++;
                if (pitchStreak > 2) {
                    buffer2++;
                    if (buffer2 > maxBuffer) {
                        flag(String.format("streakPitch=%d", pitchStreak));
                        buffer2 = 0;
                    }
                    pitchStreak = 0;
                } else {
                    if (buffer2 > 0) buffer2 -= bufferDecrease;
                }
            } else {
                pitchStreak = 0;
            }
        }
    }

    @Override
    public void onReload() {
        maxBuffer = Config.getDouble(getConfigPath() + ".max-buffer", 1);
        bufferDecrease = Config.getDouble(getConfigPath() + ".buffer-decrease", 0.05);
    }
}
