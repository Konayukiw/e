package com.anonymous.e;

import com.anonymous.e.module.eMod;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(
        modid = "Xk7mQ2pR",
        name = "e",
        version = "1.4",
        acceptedMinecraftVersions = "[1.8.9]"
)
public class e {

    public static eMod fastMine;
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

        fastMine      = new eMod();
        configUtils = new ConfigUtils();

        configUtils.load(fastMine);

        if (!fastMine.isEnabled()) {
            fastMine.enable();
            configUtils.save(fastMine);
        }

        ClientCommandHandler.instance.registerCommand(
                new ICommand(fastMine, configUtils)
        );

        initialized = true;
        System.out.println("[e] eMod initialized");
    }

    public static synchronized boolean isInitialized() {
        return initialized;
    }
}
