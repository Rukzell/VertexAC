package ac.anticheat.vertex.checks.impl.aim;

import ac.anticheat.vertex.checks.Check;
import ac.anticheat.vertex.checks.type.PacketCheck;
import ac.anticheat.vertex.player.APlayer;
import ac.anticheat.vertex.utils.Config;
import ac.anticheat.vertex.utils.PacketUtil;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;

public class AimB extends Check implements PacketCheck {
    public AimB(APlayer aPlayer) {
        super("AimB", aPlayer);
        this.maxBuffer = Config.getInt(getConfigPath() + ".max-buffer", 9);
        this.bufferDecrease = Config.getDouble(getConfigPath() + ".buffer-decrease", 0.5);
    }

    private double buffer;
    private double maxBuffer;
    private double bufferDecrease;

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!isEnabled() || aPlayer.bukkitPlayer.isInsideVehicle() || !aPlayer.actionData.inCombat() || aPlayer.rotationData.isCinematicRotation())
            return;

        if (PacketUtil.isRotation(event)) {
            float deltaYaw = Math.abs(aPlayer.rotationData.deltaYaw);
            float deltaPitch = Math.abs(aPlayer.rotationData.deltaPitch);
            float yaw = aPlayer.rotationData.yaw;
            float pitch = aPlayer.rotationData.pitch;
            float moduloYaw = deltaYaw % 1.0F;
            float moduloYaw2 = deltaYaw % 0.1F;
            float moduloYaw3 = deltaYaw % 0.05F;
            float roundYaw = (float)Math.round(yaw);
            float moduloPitch = deltaPitch % 1.0F;
            float moduloPitch2 = deltaPitch % 0.1F;
            float moduloPitch3 = deltaPitch % 0.05F;
            float roundPitch = (float)Math.round(pitch);

            boolean pitchRounded = Math.abs(pitch) < 90.0F && pitch > 0.0F && (double)deltaPitch > 0.0 && (moduloPitch == 0.0F || moduloPitch2 == 0.0F || moduloPitch3 == 0.0F || pitch == roundPitch);
            boolean yawRounded = (double)deltaYaw > 0.0 && (moduloYaw == 0.0F || moduloYaw2 == 0.0F || moduloYaw3 == 0.0F || yaw == roundYaw);

            if (pitchRounded || yawRounded) {
                buffer++;
                if (buffer > maxBuffer) {
                    flag(String.format("dYaw=%.5f\ndPitch=%.5f", deltaYaw, deltaPitch));
                    buffer = 0;
                }
            } else {
                if (buffer > 0) buffer -= bufferDecrease;
            }
        }
    }

    @Override
    public void onReload() {
        this.maxBuffer = Config.getInt(getConfigPath() + ".max-buffer", 9);
            this.bufferDecrease = Config.getDouble(getConfigPath() + ".buffer-decrease", 0.5);
    }
}