package com.anonymous.e;

import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;

public final class PlayerControllerAccessor {

    private static final Field CUR_BLOCK_DAMAGE_MP =
            findField("curBlockDamageMP", "field_78770_f");
    private static final Field BLOCK_HIT_DELAY =
            findField("blockHitDelay", "field_78781_i");

    private final PlayerControllerMP controller;

    private PlayerControllerAccessor(PlayerControllerMP controller) {
        this.controller = controller;
    }

    public static PlayerControllerAccessor of(PlayerControllerMP controller) {
        return new PlayerControllerAccessor(controller);
    }

    public float getCurBlockDamageMP() {
        try {
            return CUR_BLOCK_DAMAGE_MP.getFloat(controller);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to read curBlockDamageMP", e);
        }
    }

    public void setCurBlockDamageMP(float curBlockDamageMP) {
        try {
            CUR_BLOCK_DAMAGE_MP.setFloat(controller, curBlockDamageMP);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to write curBlockDamageMP", e);
        }
    }

    public int getBlockHitDelay() {
        try {
            return BLOCK_HIT_DELAY.getInt(controller);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to read blockHitDelay", e);
        }
    }

    public void setBlockHitDelay(int blockHitDelay) {
        try {
            BLOCK_HIT_DELAY.setInt(controller, blockHitDelay);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to write blockHitDelay", e);
        }
    }

    private static Field findField(String mcpName, String srgName) {
        Field field = ReflectionHelper.findField(PlayerControllerMP.class, mcpName, srgName);
        field.setAccessible(true);
        return field;
    }
}
