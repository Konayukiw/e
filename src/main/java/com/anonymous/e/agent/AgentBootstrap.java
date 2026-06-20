package com.anonymous.e.agent;

import com.anonymous.e.e;
import net.minecraft.client.Minecraft;

public final class AgentBootstrap {

    private static boolean scheduled;

    private AgentBootstrap() {
    }

    public static synchronized void install() {

        System.out.println("===== BUILD 2026-06-20 15:58 =====");

        try {
            Class<?> c = Class.forName(
                    "net.minecraft.command.ICommand",
                    false,
                    AgentBootstrap.class.getClassLoader()
            );
            System.out.println("ICommand = " + c);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
