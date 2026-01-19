package ac.anticheat.vertex.beauty;

import ac.anticheat.vertex.VertexAC;
import ac.anticheat.vertex.utils.Config;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class PunishEffect {
    private static final JavaPlugin plugin = VertexAC.get();

    private static final String particle = Config.getString("punish-effect.particle", "FLAME").toUpperCase();
    private static final String sound = Config.getString("punish-effect.sound", "ENTITY_DRAGON_FIREBALL_EXPLODE").toUpperCase();

    public static void start(Player player) {
        Location location = player.getLocation();

        final int particleCount = Config.getInt("punish-effect.particle_count", 80);
        final int durationTicks = Config.getInt("punish-effect.duration_ticks", 3);
        final boolean enabled = Config.getBoolean("punish-effect.enabled", true);

        if (!enabled) return;

        player.getWorld().playSound(location, Sound.valueOf(sound), 1, 1);

        spawn(location, particleCount, durationTicks);
    }

    private static void spawn(Location location, int particleCount, int durationTicks) {
        Random random = new Random();

        final double speed = Config.getDouble("punish-effect.speed", 0.2);

        new BukkitRunnable() {
            int ticksLived = 0;

            @Override
            public void run() {
                if (ticksLived++ > durationTicks) {
                    cancel();
                    return;
                }

                for (int i = 0; i < particleCount; i++) {
                    double x = (random.nextDouble() - 0.5) * 2;
                    double y = (random.nextDouble() - 0.5) * 2;
                    double z = (random.nextDouble() - 0.5) * 2;

                    location.getWorld().spawnParticle(
                            Particle.valueOf(particle),
                            location.getX(), location.getY(), location.getZ(),
                            1,
                            x, y, z,
                            speed
                    );
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
