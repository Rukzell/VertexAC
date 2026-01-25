package ac.anticheat.vertex.checks.impl.aura;

import ac.anticheat.vertex.buffer.VlBuffer;
import ac.anticheat.vertex.checks.Check;
import ac.anticheat.vertex.checks.type.PacketCheck;
import ac.anticheat.vertex.player.APlayer;
import ac.anticheat.vertex.utils.Config;
import ac.anticheat.vertex.utils.EvictingList;
import ac.anticheat.vertex.utils.PacketUtil;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;

/**
 * Вы, нерелигиозные люди, будете нести ответственность перед Аль-Каххаром за свои дела, иншаллах, Судный день уже очень близок
 */
public class AuraC extends Check implements PacketCheck {
    private final EvictingList<Long> attackDelays = new EvictingList<>(5);
    private long lastAttackTime;
    private final VlBuffer buffer = new VlBuffer();
    private double maxBuffer;
    private double bufferDecrease;

    public AuraC(APlayer player) {
        super("AuraC", player);
        this.maxBuffer = Config.getDouble(getConfigPath() + ".max-buffer", 1);
        this.bufferDecrease = Config.getDouble(getConfigPath() + ".buffer-decrease", 1);
    }

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
                        buffer.fail(1);
                        if (buffer.getVl() > maxBuffer) {
                            flag();
                            buffer.setVl(0);
                        }
                    } else {
                        buffer.setVl(buffer.getVl() - bufferDecrease);
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