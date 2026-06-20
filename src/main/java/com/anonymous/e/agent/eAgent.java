package com.anonymous.e.agent;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.net.URL;

public final class eAgent {

    private eAgent() {
    }

    public static void premain(String agentArgs, Instrumentation instrumentation) {
        attach(agentArgs);
    }

    public static void agentmain(String agentArgs, Instrumentation instrumentation) {
        attach(agentArgs);
    }

    private static void attach(String agentArgs) {

        try {

            URL jarUrl = eAgent.class.getProtectionDomain()
                    .getCodeSource()
                    .getLocation();

            ClassLoader launchClassLoader = findLaunchClassLoader();

            System.out.println("[e] eAgent loader      = "
                    + eAgent.class.getClassLoader());

            System.out.println("[e] launch loader      = "
                    + launchClassLoader);

            Method addUrl = launchClassLoader.getClass()
                    .getMethod("addURL", URL.class);

            addUrl.invoke(launchClassLoader, jarUrl);

            Class<?> bootstrap = Class.forName(
                    "com.anonymous.e.agent.AgentBootstrap",
                    true,
                    launchClassLoader
            );

            System.err.println(
                    "[e-agent] bootstrap loader = "
                            + bootstrap.getClassLoader());

            System.err.println(
                    "[e-agent] eAgent loader = "
                            + eAgent.class.getClassLoader());

            bootstrap.getMethod("install").invoke(null);

        } catch (Throwable t) {
            System.err.println("[e] Injection failed");
            t.printStackTrace();
        }
    }

    private static ClassLoader findLaunchClassLoader() throws Exception {
        Class<?> launch = Class.forName("net.minecraft.launchwrapper.Launch");
        Object classLoader = launch.getField("classLoader").get(null);
        if (!(classLoader instanceof ClassLoader)) {
            throw new IllegalStateException("Launch.classLoader is not a ClassLoader");
        }
        return (ClassLoader) classLoader;
    }
}
