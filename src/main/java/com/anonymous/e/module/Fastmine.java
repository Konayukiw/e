package com.anonymous.e.module;

import com.anonymous.e.BlockUtils;
import com.anonymous.e.mixin.IAccessorPlayerControllerMP;
import com.anonymous.e.module.setting.impl.ButtonSetting;
import com.anonymous.e.module.setting.impl.SliderSetting;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import org.lwjgl.input.Mouse;

public class Fastmine {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public final SliderSetting delay      = new SliderSetting("Break delay",  " tick", 5.0, 0.0, 5.0, 1.0);
    public final SliderSetting multiplier = new SliderSetting("Break speed",  "x",     1.0, 1.0, 2.0, 0.02);
    public final SliderSetting mode       = new SliderSetting("Mode", 2, new String[]{"Pre", "Post", "Increment"});
    public final ButtonSetting creativeDisable = new ButtonSetting("Disable in creative", true);

    private boolean enabled = false;
    private float lastCurBlockDamageMP = 0.0F;

    public Fastmine() {}

    public void enable() {
        if (!enabled) {
            enabled = true;
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    public void disable() {
        if (enabled) {
            enabled = false;
            MinecraftForge.EVENT_BUS.unregister(this);
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (e.phase != Phase.END) return;
        if (!mc.inGameHasFocus || !BlockUtils.nullCheck()) return;
        if (creativeDisable.isToggled() && mc.thePlayer.capabilities.isCreativeMode) return;

        IAccessorPlayerControllerMP controller = (IAccessorPlayerControllerMP) mc.playerController;

        int delayTarget = (int) delay.getInput();
        if (delayTarget < 5) {
            if (delayTarget == 0) {
                controller.setBlockHitDelay(0);
            } else if (controller.getBlockHitDelay() > delayTarget) {
                controller.setBlockHitDelay(delayTarget);
            }
        }

        double mult = multiplier.getInput();
        if (mult > 1.0) {
            if (!mc.thePlayer.capabilities.isCreativeMode && Mouse.isButtonDown(0)) {
                float curBlockDamage = controller.getCurBlockDamageMP();
                int modeIndex = (int) mode.getInput();

                switch (modeIndex) {
                    case 0: { // Pre
                        float damage = (float) (1.0 - 1.0 / mult);
                        if (curBlockDamage > 0.0F && curBlockDamage < damage) {
                            controller.setCurBlockDamageMP(damage);
                        }
                        break;
                    }
                    case 1: { // Post
                        double extra = 1.0 / mult;
                        if (curBlockDamage < 1.0F && (double) curBlockDamage >= extra) {
                            controller.setCurBlockDamageMP(1.0F);
                        }
                        break;
                    }
                    case 2: { // Increment
                        float damage2 = -1.0F;
                        if (curBlockDamage < 1.0F) {
                            if (mc.objectMouseOver != null && curBlockDamage > lastCurBlockDamageMP) {
                                net.minecraft.block.Block hitBlock =
                                        mc.theWorld
                                                .getBlockState(mc.objectMouseOver.getBlockPos())
                                                .getBlock();
                                net.minecraft.item.ItemStack heldItem =
                                        mc.thePlayer.inventory
                                                .getStackInSlot(mc.thePlayer.inventory.currentItem);
                                float perTickHardness = BlockUtils.getBlockHardness(
                                        hitBlock, heldItem, false, false);
                                damage2 = (float) (lastCurBlockDamageMP
                                        + perTickHardness * (mult - 0.2152857 * (mult - 1.0)));
                            }
                            if (damage2 != -1.0F && curBlockDamage > 0.0F) {
                                controller.setCurBlockDamageMP(damage2);
                            }
                        }
                        lastCurBlockDamageMP = curBlockDamage;
                        break;
                    }
                }
            } else if (mode.getInput() == 2.0) {
                lastCurBlockDamageMP = 0.0F;
            }
        }
    }

    public String getInfo() {
        double v = multiplier.getInput();
        String val = ((double)((int) v) == v) ? (int) v + "" : String.valueOf(v);
        return val + multiplier.getSuffix();
    }
}