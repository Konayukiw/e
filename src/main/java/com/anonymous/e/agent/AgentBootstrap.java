package com.anonymous.e.agent;

import com.anonymous.e.e;
import net.minecraft.client.Minecraft;

public final class AgentBootstrap {

    private static boolean scheduled;

    private AgentBootstrap() {
    }

    public static synchronized void install() {

        System.out.println(
                "[e-agent] AgentBootstrap loader = "
                        + AgentBootstrap.class.getClassLoader());

        System.out.println(
                "[e-agent] e loader = "
                        + e.class.getClassLoader());

        if (scheduled || e.isInitialized()) {
            return;
        }
        scheduled = true;

        final Minecraft minecraft = Minecraft.getMinecraft();
        Runnable initializer = new Runnable() {
            @Override
            public void run() {
                try {
                    e.initInjected();
                } catch (Throwable t) {
                    System.err.println("[e-agent] Bootstrap failed");
                    t.printStackTrace();
                }
            }
        };

        if (minecraft.isCallingFromMinecraftThread()) {
            initializer.run();
        } else {
            minecraft.addScheduledTask(initializer);
        }
    }
}
