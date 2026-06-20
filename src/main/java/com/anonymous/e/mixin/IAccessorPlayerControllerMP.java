package com.anonymous.e.mixin;

import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@SideOnly(Side.CLIENT)
@Mixin(PlayerControllerMP.class)
public interface IAccessorPlayerControllerMP {

    @Accessor
    float getCurBlockDamageMP();

    @Accessor
    void setCurBlockDamageMP(float curBlockDamageMP);

    @Accessor
    int getBlockHitDelay();

    @Accessor
    void setBlockHitDelay(int blockHitDelay);
}
