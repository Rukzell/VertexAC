package ac.anticheat.vertex.checks.impl.killaura;

import ac.anticheat.vertex.buffer.VlBuffer;
import ac.anticheat.vertex.checks.Check;
import ac.anticheat.vertex.checks.type.PacketCheck;
import ac.anticheat.vertex.config.CheckSettings;
import ac.anticheat.vertex.config.ChecksConfig;
import ac.anticheat.vertex.player.APlayer;
import ac.anticheat.vertex.utils.PacketUtil;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;

public class AuraA extends Check implements PacketCheck {
    private final CheckSettings cfg;
    private final VlBuffer buffer = new VlBuffer();

    public AuraA(APlayer player) {
        super("Aura", "(A)", player, false);
        this.cfg = ChecksConfig.get().get(this.getName());
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!isEnabled()) return;

        if (PacketUtil.isAttack(event)) {
            if (aPlayer.bukkitPlayer.isHandRaised()) {
                buffer.fail(1);
                if (buffer.getVl() > cfg.maxBuffer) {
                    flag();
                    buffer.setVl(0);
                }
            } else {
                buffer.setVl(buffer.getVl() - cfg.bufferDecrease);
            }
        }
    }
}
