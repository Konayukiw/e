package com.anonymous.e;

import com.anonymous.e.module.eMain;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import java.util.concurrent.atomic.AtomicBoolean;

@Mod(
        modid = "Xk7mQ2pR",
        name = "e",
        version = "2.1",
        acceptedMinecraftVersions = "[1.8.9]"
)
public class e {

    public static eMain ez;
    public static ConfigUtils configUtils;

    private static final AtomicBoolean initialized = new AtomicBoolean(false);

    public e() {
    /** TEMPORAL ADDITION, REMOVE THIS  RQ **/
        System.err.println("CTOR ENTER");
        bootstrap();
        System.err.println("CTOR EXIT");
    /** TEMPORAL ADDITION, REMOVE THIS RQ **/

    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        bootstrap();
    }

    /**
     * Common entry point.
     *
     * Forge:
     *     FMLInitializationEvent -> bootstrap()
     *
     * JVM Agent:
     *     AgentBootstrap.install() -> bootstrap()
     *
     * DLL Injection:
     *     JNI -> bootstrap()
     */

    public static void bootstrap() {

        System.out.println(
                e.class.getClassLoader()
        );

        System.out.println(
                Thread.currentThread().getContextClassLoader()
        );

        Runnable initializer = new Runnable() {
            @Override
            public void run() {
                try {
                    initInjected();
                    System.out.println("[e] e initialized successfully");
                } catch (Throwable t) {
                    System.err.println("[e] Boostrap initialization failed");
                    t.printStackTrace();
                }
            }
        };

        try {
            Minecraft mc = Minecraft.getMinecraft();

            if (mc == null) {
                initializer.run();
                System.out.println("[e] Minecraft instance is null, running initializer");
                return;
            }

            if (mc.isCallingFromMinecraftThread()) {
                initializer.run();
                System.out.println("[e] e initialized successfully by MinecraftThread");
            } else {
                mc.addScheduledTask(initializer);
                System.out.println("[e] e initialized successfully by ScheduledTask");
            }

        } catch (Throwable t) {
            initializer.run();
        }
    }

    public static void initInjected() {

        if (!initialized.compareAndSet(false, true)) {
            return;
        }

        ez = new eMain();
        configUtils = new ConfigUtils();

        configUtils.load(ez);

        if (!ez.isEnabled()) {
            ez.enable();
            configUtils.save(ez);
        }

        ClientCommandHandler.instance.registerCommand(
                new ICommand(ez, configUtils)
        );

        System.out.println("[e] e initialized successfully");
    }

    public static boolean isInitialized() {
        return initialized.get();
    }
}