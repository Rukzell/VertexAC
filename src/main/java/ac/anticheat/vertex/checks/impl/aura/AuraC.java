package ac.anticheat.vertex.checks.impl.aura;

import ac.anticheat.vertex.checks.Check;
import ac.anticheat.vertex.checks.type.PacketCheck;
import ac.anticheat.vertex.player.APlayer;
import ac.anticheat.vertex.utils.Config;
import ac.anticheat.vertex.utils.EvictingList;
import ac.anticheat.vertex.utils.PacketUtil;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;

/**
 * автокликер типо
 */
public class AuraC extends Check implements PacketCheck {
    public AuraC(APlayer player) {
        super("AuraC", player);
        this.maxBuffer = Config.getDouble(getConfigPath() + ".max-buffer", 1);
        this.bufferDecrease = Config.getDouble(getConfigPath() + ".buffer-decrease", 1);
    }

    private final EvictingList<Long> attackDelays = new EvictingList<>(10);
    private long lastAttackTime;
    private double buffer;
    private double maxBuffer;
    private double bufferDecrease;

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!isEnabled() || !aPlayer.actionData.inCombat()) return;

        if (PacketUtil.isAttack(event)) {
            long now = System.currentTimeMillis();
            if (lastAttackTime > 0) {
                long delay = now - lastAttackTime;
                attackDelays.add(delay);

                if (attackDelays.isFull()) {
                    if (attackDelays.allEqual()) {
                        buffer++;
                        if (buffer > maxBuffer) {
                            flag();
                            buffer = 0;
                        }
                    } else if (buffer > 0) {
                        buffer -= bufferDecrease;
                    }
                }
            }
            lastAttackTime = now;
        }
    }

    @Override
    public void onReload() {
        this.maxBuffer = Config.getDouble(getConfigPath() + ".max-buffer", 1);
        this.bufferDecrease = Config.getDouble(getConfigPath() + ".buffer-decrease", 1);
    }
}