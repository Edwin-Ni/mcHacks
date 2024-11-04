package net.hacks.mixin;

import com.mojang.logging.LogUtils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    private static final Logger LOGGER = LogUtils.getLogger();

    @Shadow public ServerPlayerEntity player;

    private boolean isEntityOnAir(Entity entity) {
        return entity.getWorld().getStatesInBox(entity.getBoundingBox().expand(0.0625).stretch(0.0, -0.55, 0.0)).allMatch(AbstractBlock.AbstractBlockState::isAir);
    }

    @Inject(method = "onPlayerMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;updatePositionAndAngles(DDDFF)V", shift = At.Shift.BEFORE))
    private void captureCoordinates(PlayerMoveC2SPacket packet, CallbackInfo info) {
        double x = packet.getX(0.0); // Default value if not set
        double y = packet.getY(0.0);
        double z = packet.getZ(0.0);


//        LOGGER.info("Player coordinates: X={} Y={} Z={}", x, y, z);
        LOGGER.info("Packet Y: {}", y);
        LOGGER.info("Player Y: {}", player.getY());
        LOGGER.info("Vertical movement (m): {}", y - this.player.getY());

        // Calculate the 'floating' state based on conditions in the onPlayerMove method
        boolean floating = (y - this.player.getY()) >= -0.03125
                && !this.player.groundCollision
//                && this.player.interactionManager.getGameMode() != GameMode.SPECTATOR
//                && !this.server.isFlightEnabled()
                && !this.player.getAbilities().allowFlying
//                && !this.player.hasStatusEffect(StatusEffects.LEVITATION)
                && !this.player.isFallFlying()
                && !this.player.isUsingRiptide()
                && this.isEntityOnAir(this.player);

        LOGGER.info("Calculated floating state: {}", floating);
    }
}
