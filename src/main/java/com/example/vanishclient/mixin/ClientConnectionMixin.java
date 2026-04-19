package com.example.vanishclient.mixin;

import com.example.vanishclient.VanishState;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public abstract class ClientConnectionMixin {
    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;)V", at = @At("HEAD"), cancellable = true)
    private void vanishclient$cancelOutgoingMovePackets(Packet<?> packet, PacketListener callbacks, CallbackInfo ci) {
        if (VanishState.isPacketDesync() && packet instanceof PlayerMoveC2SPacket) {
            ci.cancel();
        }
    }
}
