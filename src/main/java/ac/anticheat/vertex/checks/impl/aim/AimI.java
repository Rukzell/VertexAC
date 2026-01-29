package ac.anticheat.vertex.checks.impl.aim;

import ac.anticheat.vertex.buffer.VlBuffer;
import ac.anticheat.vertex.checks.Check;
import ac.anticheat.vertex.checks.type.PacketCheck;
import ac.anticheat.vertex.player.APlayer;
import ac.anticheat.vertex.utils.Config;
import ac.anticheat.vertex.utils.PacketUtil;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;

public class AimI extends Check implements PacketCheck {
    private final VlBuffer buffer1 = new VlBuffer();
    private final VlBuffer buffer2 = new VlBuffer();
    private int yawStreak;
    private int pitchStreak;
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
                if (yawStreak > 5) {
                    buffer1.fail(3);
                } else if (yawStreak > 3) {
                    buffer1.fail(2);
                } else if (yawStreak > 2) {
                    buffer1.fail(1);
                } else {
                    buffer1.setVl(buffer1.getVl() - bufferDecrease);
                }
            } else {
                yawStreak = 0;
            }

            if (deltaPitch == lastDeltaPitch && deltaPitch != 0) {
                pitchStreak++;
                if (pitchStreak > 5) {
                    buffer2.fail(3);
                } else if (pitchStreak > 3) {
                    buffer2.fail(1.7);
                } else if (pitchStreak > 2) {
                    buffer2.fail(1);
                } else {
                    buffer2.setVl(buffer2.getVl() - bufferDecrease);
                }
            } else {
                pitchStreak = 0;
            }

            if (buffer1.getVl() > maxBuffer) {
                flag(String.format("streakYaw=%d", yawStreak));
                buffer1.setVl(0);
            }

            if (buffer2.getVl() > maxBuffer) {
                flag(String.format("streakPitch=%d", pitchStreak));
                buffer2.setVl(0);
            }
        }
    }

    @Override
    public void onReload() {
        maxBuffer = Config.getDouble(getConfigPath() + ".max-buffer", 1);
        bufferDecrease = Config.getDouble(getConfigPath() + ".buffer-decrease", 0.05);
    }
}
