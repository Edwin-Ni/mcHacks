package net.hacks.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.hacks.client.module.Flight;
import net.hacks.client.module.Hack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class HacksClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("EdwinHax");
    private static final List<Hack> HACKS = new ArrayList<>();

    @Override
    public void onInitializeClient() {
        HACKS.add(new Flight());

        ClientTickEvents.START_CLIENT_TICK.register(client ->
                HACKS.forEach(hack -> {
                    if (hack.isEnabled())
                        hack.tick();
                })
        );
        ClientTickEvents.END_CLIENT_TICK.register(client ->
                HACKS.forEach(hack -> {
                    if (hack.isEnabled())
                        hack.postTick();
                })
        );
    }

    private static <T extends Hack> T getHack(Class<T> clazz) {
        for (var hack : HACKS) {
            if (hack.getClass() == clazz) {
                return clazz.cast(hack);
            }
        }
        return null;
    }

    public static boolean isEnabled(Class<? extends Hack> hakk) {
        var hack = getHack(hakk);
        return hack != null && hack.isEnabled();
    }

    public static void toggle(Class<? extends Hack> hakk) {
        var hack = getHack(hakk);
        if (hack != null) {
            hack.toggle();
        }
    }

    public static List<Hack> getHacks() {
        return HACKS;
    }
}
