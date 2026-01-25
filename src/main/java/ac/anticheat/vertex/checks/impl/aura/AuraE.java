package ac.anticheat.vertex.checks.impl.aura;

import ac.anticheat.vertex.checks.Check;
import ac.anticheat.vertex.checks.type.PacketCheck;
import ac.anticheat.vertex.player.APlayer;

/**
 * in ActionData
 */
public class AuraE extends Check implements PacketCheck {
    public AuraE(APlayer aPlayer) {
        super("AuraE", aPlayer);
    }
}
