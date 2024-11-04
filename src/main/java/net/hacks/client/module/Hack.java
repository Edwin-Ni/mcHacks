package net.hacks.client.module;

import net.minecraft.network.packet.Packet;

public abstract class Hack {
    protected boolean enabled = false;

    public void toggle() {
        this.enabled = !enabled;
    }

    public boolean isEnabled() {
        return this.enabled;
    }


    public void modifyPacket(Packet<?> packet) {}

    public void tick() {}

    public void postTick() {}
}
