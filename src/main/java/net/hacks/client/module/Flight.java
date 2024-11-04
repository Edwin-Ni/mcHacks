package net.hacks.client.module;

import com.mojang.logging.LogUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.hacks.mixin.PlayerMoveC2SPacketAccessor;
import org.slf4j.Logger;

public class Flight extends Hack {
    private static final double FALL_DIST = 0.4;
    private static final int MAX_FLOATING_TICKS = 10;
    private double previousY;
    private int floatingTicks;
    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void toggle() {
        super.toggle();
        var player = MinecraftClient.getInstance().player;
        if (player == null) return;
        player.getAbilities().allowFlying = this.enabled;
        this.previousY = player.getY();
        this.floatingTicks = 0;
    }
    @Override
    public void modifyPacket(Packet<?> packet) {
        if (!(packet instanceof PlayerMoveC2SPacket)) return;
        // Modify packet by lowering Y-value to trick server
        if (floatingTicks >= MAX_FLOATING_TICKS) {
            ((PlayerMoveC2SPacketAccessor) packet).setY(this.previousY - FALL_DIST);
            floatingTicks = 0;
//            LOGGER.info("TOUCHED GRASS!>>>>");
        }
    }
    @Override
    public void tick() {
        var player = MinecraftClient.getInstance().player;
        if (player == null) return;
        player.getAbilities().allowFlying = this.enabled;
        if (!player.getAbilities().flying) return;
        if (player.getY() >= this.previousY - FALL_DIST)
            this.floatingTicks++;
        this.previousY = player.getY();
    }
}

