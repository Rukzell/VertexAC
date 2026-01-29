package ac.anticheat.vertex.data;

import ac.anticheat.vertex.VertexAC;
import ac.anticheat.vertex.checks.Check;
import ac.anticheat.vertex.checks.type.PacketCheck;
import ac.anticheat.vertex.player.APlayer;
import ac.anticheat.vertex.utils.Config;
import ac.anticheat.vertex.utils.PacketUtil;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEntityAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import org.bukkit.entity.Player;

public class ActionData extends Check implements PacketCheck {

    private long lastAttack = -1L;
    private boolean attack = false;
    private boolean interact = false;
    private boolean swing = false;
    private boolean startSprint = false;
    private boolean stopSprint = false;
    private int combatTicks;
    private Player pTarget;
    private boolean digging = false;

    public ActionData(APlayer aPlayer) {
        super("ActionData", aPlayer);
        this.combatTicks = Config.getInt(getConfigPath() + ".combat-ticks", 60);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (PacketUtil.isAttack(event)) {
            WrapperPlayClientInteractEntity wrapper = new WrapperPlayClientInteractEntity(event);
            lastAttack = System.nanoTime();
            attack = true;
            pTarget = VertexAC.getConnectionListener().getPlayerById(wrapper.getEntityId());
            return;
        }

        if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
            interact = true;
            return;
        }

        if (event.getPacketType() == PacketType.Play.Client.ENTITY_ACTION) {
            WrapperPlayClientEntityAction wrapper = new WrapperPlayClientEntityAction(event);
            switch (wrapper.getAction()) {
                case START_SPRINTING -> {
                    if (startSprint) VertexAC.getCheckManager().getCheck(aPlayer.bukkitPlayer, "AuraE").flag();
                    startSprint = true;
                    stopSprint = false;
                }
                case STOP_SPRINTING -> {
                    if (stopSprint) VertexAC.getCheckManager().getCheck(aPlayer.bukkitPlayer, "AuraE").flag();
                    stopSprint = true;
                    startSprint = false;
                }
            }
        }

        if (event.getPacketType() == PacketType.Play.Client.ANIMATION) {
            swing = true;
            return;
        }

        if (event.getPacketType() == PacketType.Play.Client.PLAYER_DIGGING) {
            WrapperPlayClientPlayerDigging wrapper = new WrapperPlayClientPlayerDigging(event);
            switch (wrapper.getAction()) {
                case START_DIGGING -> digging = true;
                case CANCELLED_DIGGING -> digging = false;
                case FINISHED_DIGGING -> digging = false;
            }
        }

        if (event.getPacketType() == PacketType.Play.Client.KEEP_ALIVE) {
            attack = false;
            interact = false;
            startSprint = false;
            stopSprint = false;
            swing = false;
            digging = false;
        }
    }

    public boolean hasAttackedSince(long timeMillis) {
        if (lastAttack == -1) return false;
        long elapsedNanos = System.nanoTime() - lastAttack;
        return (elapsedNanos / 1_000_000) < timeMillis;
    }

    public long getLastAttackMillis() {
        return lastAttack / 1_000_000;
    }

    public long getLastAttackNanos() {
        return lastAttack;
    }

    public boolean attack() {
        return attack;
    }

    public boolean swing() {
        return swing;
    }

    public boolean interact() {
        return interact;
    }

    public boolean startSprint() {
        return startSprint;
    }

    public boolean stopSprint() {
        return stopSprint;
    }

    public Player getPTarget() {
        return pTarget;
    }

    public boolean inCombat() {
        if (lastAttack == -1) return false;
        long elapsedMillis = (System.nanoTime() - lastAttack) / 1_000_000;
        return elapsedMillis < combatTicks * 50L;
    }

    public void onReload() {
        this.combatTicks = Config.getInt(getConfigPath() + ".combat-ticks", 60);
    }

    public boolean isDigging() {
        return digging;
    }
}