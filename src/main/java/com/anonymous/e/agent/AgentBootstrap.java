package com.anonymous.e.agent;

import com.anonymous.e.e;
import net.minecraft.client.Minecraft;

public final class AgentBootstrap {

    private static volatile boolean scheduled = false;
    private static final Object LOCK = new Object();

    private AgentBootstrap() {
    }

    public static void install() {
        synchronized (LOCK) {
            if (scheduled || e.isInitialized()) {
                System.out.println("[e] Already scheduled or initialized, skipping.");
                return;
            }
            scheduled = true;
        }

        Runnable initializer = new Runnable() {
            @Override
            public void run() {
                try {
                    e.initInjected();
                } catch (Throwable t) {
                    System.err.println("[e] Bootstrap failed");
                    t.printStackTrace();
                }
            }
        };

        try {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc == null) {
                System.out.println("[e] Minecraft instance is null, running initializer directly.");
                initializer.run();
                return;
            }

            if (mc.isCallingFromMinecraftThread()) {
                initializer.run();
            } else {
                mc.addScheduledTask(initializer);
                System.out.println("[e] Initializer scheduled on client thread.");
            }
        } catch (Throwable t) {
            System.err.println("[e] Failed to schedule initializer");
            t.printStackTrace();
            initializer.run();
        }
    }
}