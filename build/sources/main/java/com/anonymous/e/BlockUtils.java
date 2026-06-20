package com.anonymous.e;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;

public class BlockUtils {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static float getBlockHardness(Block block, ItemStack itemStack, boolean ignoreSlow, boolean ignoreGround) {
        float hardness = block.getBlockHardness(mc.theWorld, null);
        if (hardness < 0.0F) {
            return 0.0F;
        }

        boolean canHarvest = block.getMaterial().isToolNotRequired()
                || (itemStack != null && itemStack.canHarvestBlock(block));

        float efficiency = getToolDigEfficiency(itemStack, block, ignoreSlow, ignoreGround);

        return canHarvest
                ? efficiency / hardness / 30.0F
                : efficiency / hardness / 100.0F;
    }

    private static float getToolDigEfficiency(ItemStack itemStack, Block block, boolean ignoreSlow, boolean ignoreGround) {
        float n = (itemStack == null) ? 1.0F : itemStack.getItem().getStrVsBlock(itemStack, block);

        if (n > 1.0F) {
            int effLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, itemStack);
            if (effLevel > 0 && itemStack != null) {
                n += (float) (effLevel * effLevel + 1);
            }
        }

        if (mc.thePlayer.isPotionActive(Potion.digSpeed)) {
            n *= 1.0F + (float) (mc.thePlayer.getActivePotionEffect(Potion.digSpeed).getAmplifier() + 1) * 0.2F;
        }

        if (!ignoreSlow) {
            if (mc.thePlayer.isPotionActive(Potion.digSlowdown)) {
                float slowMult;
                switch (mc.thePlayer.getActivePotionEffect(Potion.digSlowdown).getAmplifier()) {
                    case 0:  slowMult = 0.3F;     break;
                    case 1:  slowMult = 0.09F;    break;
                    case 2:  slowMult = 0.0027F;  break;
                    default: slowMult = 8.1E-4F;  break;
                }
                n *= slowMult;
            }

            if (mc.thePlayer.isInsideOfMaterial(Material.water)
                    && !EnchantmentHelper.getAquaAffinityModifier(mc.thePlayer)) {
                n /= 5.0F;
            }

            if (!mc.thePlayer.onGround && !ignoreGround) {
                n /= 5.0F;
            }
        }

        return n;
    }

    public static boolean nullCheck() {
        return mc.thePlayer != null && mc.theWorld != null;
    }
}