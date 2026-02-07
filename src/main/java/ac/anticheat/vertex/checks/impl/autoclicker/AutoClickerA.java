package ac.anticheat.vertex.checks.impl.autoclicker;

import ac.anticheat.vertex.checks.Check;
import ac.anticheat.vertex.checks.type.PacketCheck;
import ac.anticheat.vertex.config.CheckSettings;
import ac.anticheat.vertex.config.ChecksConfig;
import ac.anticheat.vertex.player.APlayer;
import ac.anticheat.vertex.utils.PacketUtil;
import ac.anticheat.vertex.utils.kireiko.millennium.math.Statistics;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;

import java.util.ArrayList;
import java.util.List;

public class AutoClickerA extends Check implements PacketCheck {
    private final List<Long> sample = new ArrayList<>();
    private final CheckSettings cfg;
    private long lastPacketTime;
    public AutoClickerA(APlayer player) {
        super("AutoClicker", "(A)", player, false);
        this.cfg = ChecksConfig.get().get(this.getName());
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!isEnabled() || !aPlayer.actionData.inCombat()) return;

        if (event.getPacketType() == PacketType.Play.Client.ANIMATION) {
            long now = System.currentTimeMillis();
            if (lastPacketTime > 0) {
                long delay = now - lastPacketTime;
                sample.add(delay);
                if (sample.size() >= 20) {
                    double dev = Statistics.getStandardDeviation(sample);

                    if (dev < 1.2) {
                        flag();
                    }

                    sample.remove(0);
                }
            }
            lastPacketTime = now;
        }
    }
}
