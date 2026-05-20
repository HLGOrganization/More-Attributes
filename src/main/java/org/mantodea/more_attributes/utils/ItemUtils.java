package org.mantodea.more_attributes.utils;

import net.dries007.tfc.common.capabilities.size.ItemSizeManager;
import net.dries007.tfc.common.capabilities.size.Size;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class ItemUtils {
    public static Map<String, Integer> ItemWeights = new HashMap<>();

    public static int getWeight(ItemStack stack) {
        var key = ForgeRegistries.ITEMS.getKey(stack.getItem());

        if (key == null)
            return 0;

        var weight = 0;

        if (ItemWeights.containsKey(key.toString()))
            weight = ItemWeights.get(key.toString());
        else
            weight = 64 / ItemSizeManager.get(stack).getWeight(stack).stackSize;

        weight *= stack.getCount();

        var size = ItemSizeManager.get(stack).getSize(stack);
        if (size == Size.NORMAL)
            weight *= 2;
        if (size == Size.LARGE)
            weight *= 3;
        if (size == Size.VERY_LARGE)
            weight *= 4;
        if (size == Size.HUGE)
            weight *= 8;

        if (ModUtils.checkModLoaded("backpacked_tfc"))
        {
            if (stack.getItem() instanceof net.magafinz.backpackedtfc.item.BackpackItem) {
                var handler = stack.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve().orElse(null);

                if (handler != null) {
                    var slots = handler.getSlots();

                    for (var i = 0; i < slots; i++) {
                        var itemStack = handler.getStackInSlot(i);

                        weight += getWeight(itemStack);
                    }
                }
            }
        }

        if (ModUtils.checkModLoaded("sophisticatedbackpacks"))
        {
            if (stack.getItem() instanceof net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem) {
                var itemHandler = stack.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve().orElse(null);

                if (itemHandler != null) {
                    var slots = itemHandler.getSlots();

                    for (var i = 0; i < slots; i++) {
                        var itemStack = itemHandler.getStackInSlot(i);

                        weight += getWeight(itemStack);
                    }
                }

                var fluidHandler = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).resolve().orElse(null);
            }
        }

        return weight;
    }
}
