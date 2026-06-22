package com.anonymous.e;

import com.anonymous.e.module.eMain;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ConfigUtils {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "fm.json";

    private final File configFile;

    public ConfigUtils() {
        File configDir = new File(Minecraft.getMinecraft().mcDataDir, "config");
        if (!configDir.exists()) configDir.mkdirs();
        this.configFile = new File(configDir, FILE_NAME);
    }

    public void load(eMain fm) {
        if (!configFile.exists()) {
            save(fm);
            return;
        }
        try (Reader reader = new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8)) {
            JsonObject obj = new JsonParser().parse(reader).getAsJsonObject();

            if (obj.has("enabled"))         setEnabled(fm, obj.get("enabled").getAsBoolean());
            if (obj.has("delay"))           fm.delay.setValue(obj.get("delay").getAsDouble());
            if (obj.has("multiplier"))      fm.multiplier.setValue(obj.get("multiplier").getAsDouble());
            if (obj.has("mode"))            fm.mode.setValue(obj.get("mode").getAsDouble());
            if (obj.has("creativeDisable")) fm.creativeDisable.setEnabled(obj.get("creativeDisable").getAsBoolean());

        } catch (Exception e) {
            System.err.println("[e] Failed to load config: " + e.getMessage());
        }
    }

    public void save(eMain fm) {
        JsonObject obj = new JsonObject();
        obj.addProperty("enabled",         fm.isEnabled());
        obj.addProperty("delay",           fm.delay.getInput());
        obj.addProperty("multiplier",      fm.multiplier.getInput());
        obj.addProperty("mode",            (int) fm.mode.getInput());
        obj.addProperty("creativeDisable", fm.creativeDisable.isToggled());

        try (Writer writer = new OutputStreamWriter(new FileOutputStream(configFile), StandardCharsets.UTF_8)) {
            GSON.toJson(obj, writer);
        } catch (Exception e) {
            System.err.println("[e] Failed to save config: " + e.getMessage());
        }
    }

    private void setEnabled(eMain fm, boolean enabled) {
        if (enabled && !fm.isEnabled())  fm.enable();
        if (!enabled && fm.isEnabled())  fm.disable();
    }
}
