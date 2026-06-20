package com.anonymous.e;

import com.anonymous.e.module.Fastmine;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(
        modid = "Xk7mQ2pR",
        name = "e",
        version = "1.1",
        acceptedMinecraftVersions = "[1.8.9]"
)
public class e {

    public static Fastmine fastMine;
    public static ConfigUtils configUtils;

    public e() {
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        fastMine      = new Fastmine();
        configUtils = new ConfigUtils();

        configUtils.load(fastMine);

        if (!fastMine.isEnabled()) {
            fastMine.enable();
            configUtils.save(fastMine);
        }

        ClientCommandHandler.instance.registerCommand(
                new ICommand(fastMine, configUtils)
        );
    }
}
