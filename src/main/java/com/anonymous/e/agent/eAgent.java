package com.anonymous.e.agent;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedHashSet;
import java.util.Set;

public final class eAgent {

    private eAgent() {
    }

    public static void premain(String agentArgs, Instrumentation instrumentation) {
        attach();
    }

    public static void agentmain(String agentArgs, Instrumentation instrumentation) {
        attach();
    }

    private static void attach() {
        try {
            URL jarUrl = eAgent.class.getProtectionDomain()
                    .getCodeSource()
                    .getLocation();

            ClassLoader targetLoader = findMinecraftClassLoader(
                    Thread.currentThread().getContextClassLoader()
            );

            System.out.println("[e] eAgent loader  = " + eAgent.class.getClassLoader());
            System.out.println("[e] target loader  = " + targetLoader);

            Throwable targetFailure = null;

            if (targetLoader != null) {
                try {
                    addUrl(targetLoader, jarUrl);
                    invokeInstall(targetLoader);
                    return;
                } catch (Throwable t) {
                    targetFailure = t;
                    System.err.println("[e] Primary loader failed, trying fallback...");
                }
            }

            ClassLoader parent = targetLoader != null ? targetLoader
                    : Thread.currentThread().getContextClassLoader();
            URLClassLoader childLoader = new URLClassLoader(new URL[]{jarUrl}, parent);
            try {
                invokeInstall(childLoader);
            } catch (Throwable t) {
                if (targetFailure != null && t != targetFailure) {
                    t.addSuppressed(targetFailure);
                }
                throw t;
            }

        } catch (Throwable t) {
            System.err.println("[e] Injection failed");
            t.printStackTrace();
        }
    }

    private static ClassLoader findMinecraftClassLoader(ClassLoader preferred) {
        Set<ClassLoader> candidates = new LinkedHashSet<ClassLoader>();
        addAncestors(candidates, preferred);
        addAncestors(candidates, Thread.currentThread().getContextClassLoader());
        addAncestors(candidates, eAgent.class.getClassLoader());
        addAncestors(candidates, ClassLoader.getSystemClassLoader());

        for (Thread thread : Thread.getAllStackTraces().keySet()) {
            addAncestors(candidates, thread.getContextClassLoader());
        }

        for (ClassLoader candidate : candidates) {
            if (canLoad(candidate, "net.minecraft.client.Minecraft")
                    && canLoad(candidate, "net.minecraftforge.common.MinecraftForge")) {
                return candidate;
            }
        }

        try {
            Class<?> launch = Class.forName("net.minecraft.launchwrapper.Launch");
            Object cl = launch.getField("classLoader").get(null);
            if (cl instanceof ClassLoader) {
                System.out.println("[e] Falling back to Launch.classLoader");
                return (ClassLoader) cl;
            }
        } catch (Throwable ignored) {
        }

        return null;
    }

    private static void addAncestors(Set<ClassLoader> set, ClassLoader loader) {
        for (ClassLoader cl = loader; cl != null; cl = cl.getParent()) {
            set.add(cl);
        }
    }

    private static boolean canLoad(ClassLoader loader, String className) {
        if (loader == null) return false;
        try {
            Class.forName(className, false, loader);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static void addUrl(ClassLoader loader, URL jarUrl) throws Exception {
        if (!(loader instanceof URLClassLoader)) {
            return;
        }
        URLClassLoader ucl = (URLClassLoader) loader;
        // 既に追加済みなら何もしない
        for (URL url : ucl.getURLs()) {
            if (url.sameFile(jarUrl)) {
                return;
            }
        }
        Method addUrl = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        addUrl.setAccessible(true);
        addUrl.invoke(loader, jarUrl);
    }

    private static void invokeInstall(ClassLoader loader) throws Throwable {
        Class<?> bootstrap = Class.forName(
                "com.anonymous.e.agent.AgentBootstrap",
                true,
                loader
        );
        System.out.println("[e] bootstrap loader = " + bootstrap.getClassLoader());
        Method install = bootstrap.getDeclaredMethod("install");
        install.setAccessible(true);
        install.invoke(null);
    }
}