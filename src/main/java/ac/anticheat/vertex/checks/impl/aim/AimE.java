package ac.anticheat.vertex.checks.impl.aim;

import ac.anticheat.vertex.checks.Check;
import ac.anticheat.vertex.checks.type.PacketCheck;
import ac.anticheat.vertex.player.APlayer;
import ac.anticheat.vertex.utils.Config;
import ac.anticheat.vertex.utils.PacketUtil;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;

public class AimE extends Check implements PacketCheck {
    public AimE(APlayer aPlayer) {
        super("AimE", aPlayer);
        this.maxBuffer = Config.getInt(getConfigPath() + ".max-buffer", 7);
        this.bufferDecrease = Config.getDouble(getConfigPath() + ".buffer-decrease", 0.5);
    }

    private double buffer1;
    private double buffer2;
    private double maxBuffer;
    private double bufferDecrease;

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!isEnabled() || aPlayer.bukkitPlayer.isInsideVehicle() || Math.abs(aPlayer.rotationData.pitch) == 90 || !aPlayer.actionData.inCombat() || aPlayer.rotationData.isCinematicRotation())
            return;

        float deltaYaw = Math.abs(aPlayer.rotationData.deltaYaw);
        float deltaPitch = Math.abs(aPlayer.rotationData.deltaPitch);

        if (PacketUtil.isRotation(event)) {
            if (deltaYaw > 3.5 && deltaPitch == 0) {
                buffer1++;
                if (buffer1 > maxBuffer) {
                    flag();
                    buffer1 = 0;
                }
            } else {
                if (buffer1 > 0) buffer1 -= bufferDecrease;
            }

            if (deltaPitch > 3.5 && deltaYaw == 0) {
                buffer2++;
                if (buffer2 > maxBuffer) {
                    flag("ну типа y > 3.5 а х == 0");
                    buffer2 = 0;
                }
            } else {
                if (buffer2 > 0) buffer2 -= bufferDecrease;
            }
        }
    }

    @Override
    public void onReload() {
        this.maxBuffer = Config.getInt(getConfigPath() + ".max-buffer", 7);
        this.bufferDecrease = Config.getDouble(getConfigPath() + ".buffer-decrease", 0.5);
    }
}
