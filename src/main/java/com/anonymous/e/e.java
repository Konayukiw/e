package com.anonymous.e;

import com.anonymous.e.module.eMod;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(
        modid = "Xk7mQ2pR",
        name = "e",
        version = "1.6",
        acceptedMinecraftVersions = "[1.8.9]"
)
public class e {

    public static eMod ez;
    public static ConfigUtils configUtils;
    private static boolean initialized;

    public e() {
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        initInjected();
    }

    public static synchronized void initInjected() {
        if (initialized) {
            return;
        }

        ez = new eMod();
        configUtils = new ConfigUtils();

        configUtils.load(ez);

        if (!ez.isEnabled()) {
            ez.enable();
            configUtils.save(ez);
        }

        ClientCommandHandler.instance.registerCommand(
                new ICommand(ez, configUtils)
        );

        initialized = true;
        System.out.println("[e] e initialized");
    }

    public static synchronized boolean isInitialized() {
        return initialized;
    }
}
