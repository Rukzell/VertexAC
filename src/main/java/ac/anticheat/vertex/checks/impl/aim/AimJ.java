package ac.anticheat.vertex.checks.impl.aim;

import ac.anticheat.vertex.checks.Check;
import ac.anticheat.vertex.checks.type.PacketCheck;
import ac.anticheat.vertex.player.APlayer;

/**
 * unstable acceleration
 */
public class AimJ extends Check implements PacketCheck {
    public AimJ(APlayer aPlayer) {
        super("AimJ", aPlayer);
    }

//    @Override
//    public void onPacketReceive(PacketReceiveEvent event) {
//        if (!isEnabled() || !aPlayer.actionData.inCombat()) return;
//
//        if (PacketUtil.isRotation(event)) {
//            double accelYaw = Math.abs(aPlayer.rotationData.accelYaw);
//            double lastAccelYaw = Math.abs(aPlayer.rotationData.lastAccelYaw);
//            double accelPitch = Math.abs(aPlayer.rotationData.accelPitch);
//            double lastAccelPitch = Math.abs(aPlayer.rotationData.lastAccelPitch);
//
//            if (accelYaw > 40 && lastAccelYaw < 0.001) {
//                flag("unstable yaw acceleration");
//            }
//
//            if (accelPitch > 40 && lastAccelPitch < 0.001) {
//                flag("unstable pitch acceleration");
//            }
//        }
//    }
}
