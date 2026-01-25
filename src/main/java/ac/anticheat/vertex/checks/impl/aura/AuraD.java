package ac.anticheat.vertex.checks.impl.aura;

import ac.anticheat.vertex.buffer.VlBuffer;
import ac.anticheat.vertex.checks.Check;
import ac.anticheat.vertex.checks.type.PacketCheck;
import ac.anticheat.vertex.player.APlayer;
import ac.anticheat.vertex.utils.PacketUtil;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class AuraD extends Check implements PacketCheck {
    public AuraD(APlayer aPlayer) {
        super("AuraD", aPlayer);
    }

    private Location lastAttackerLocation;
    private VlBuffer buffer = new VlBuffer();

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!isEnabled() || !aPlayer.actionData.inCombat()) {
            return;
        }

        if (!PacketUtil.isAttack(event)) {
            return;
        }

        Player attacker = aPlayer.bukkitPlayer;

        Player target = aPlayer.actionData.getPTarget();
        if (target == null) {
            return;
        }

        Location attackerLocation = attacker.getLocation();

        if (aPlayer.rotationData.yaw != aPlayer.rotationData.lastYaw
                && aPlayer.rotationData.pitch != aPlayer.rotationData.lastPitch
                && lastAttackerLocation != null
                && attackerLocation.distance(lastAttackerLocation) > 0.1) {

            if (!attacker.hasLineOfSight(target)) {
                buffer.fail(1);
                if (buffer.getVl() > 2) {
                    flag();
                    buffer.setVl(0);
                }
            } else {
                buffer.setVl(0);
            }
        }

        lastAttackerLocation = attackerLocation;
    }
}