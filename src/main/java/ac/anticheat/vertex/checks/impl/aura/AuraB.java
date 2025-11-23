package ac.anticheat.vertex.checks.impl.aura;

import ac.anticheat.vertex.checks.Check;
import ac.anticheat.vertex.checks.type.PacketCheck;
import ac.anticheat.vertex.player.APlayer;
import ac.anticheat.vertex.utils.Config;
import ac.anticheat.vertex.utils.PacketUtil;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;

/**
 * снапы
 */
public class AuraB extends Check implements PacketCheck {
    public AuraB(APlayer aPlayer) {
        super("AuraB", aPlayer);
        this.maxBuffer = Config.getInt(getConfigPath() + ".max-buffer", 1);
        this.bufferDecrease = Config.getDouble(getConfigPath() + ".buffer-decrease", 0.25);
    }

    private int buffer;
    private double maxBuffer;
    private double bufferDecrease;

    // check vars
    private double deltaYaw;
    private double lastDeltaYaw;

    // check config
    private final double max = 60;
    private final double min = 3.2;

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!isEnabled() || !aPlayer.actionData.inCombat()) return;

        if (PacketUtil.isAttack(event)) {
            deltaYaw = Math.abs(aPlayer.rotationData.deltaYaw);
            lastDeltaYaw = Math.abs(aPlayer.rotationData.lastDeltaYaw);

            if (deltaYaw > max && lastDeltaYaw < min) {
                buffer++;
                if (buffer > maxBuffer) {
                    flag(String.format("current=%.5f\nlast=%.5f", deltaYaw, lastDeltaYaw));
                    buffer = 0;
                }
            } else {
                if (buffer > 0) buffer -= bufferDecrease;
            }
        }
    }

    @Override
    public void onReload() {
        this.maxBuffer = Config.getDouble(getConfigPath() + ".max-buffer", 1);
        this.bufferDecrease = Config.getDouble(getConfigPath() + ".buffer-decrease", 0.25);
    }
}