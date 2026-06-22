package com.anonymous.e.agent;

import com.anonymous.e.e;

public final class AgentBootstrap {

    private AgentBootstrap() {
    }

    public static void install() {
        try {
            e.bootstrap();
        } catch (Throwable t) {
            System.err.println("[e] Bootstrap failed");
            t.printStackTrace();
        }
    }
}