package org.mantodea.more_attributes.mixins.vanilla;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import org.mantodea.more_attributes.IMAPlayer;
import org.mantodea.more_attributes.attributes.DetailAttributes;
import org.mantodea.more_attributes.events.PlayerEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Player.class)
public class PlayerMixin implements IMAPlayer {
    @ModifyVariable(method = "causeFoodExhaustion", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float causeFoodExhaustion(final float exhaustion) {
        if(exhaustion < 0) return exhaustion;

        Player player = (Player)(Object)this;

        AttributeInstance instance = player.getAttribute(DetailAttributes.HungerConsumption);

        if(instance == null) return exhaustion;

        return (float) instance.getValue() * exhaustion;
    }

    @Unique
    private PlayerEvents.MoreAttributesInventoryListener ma$inventoryListener;

    @Override
    public PlayerEvents.MoreAttributesInventoryListener ma$getInventoryListener() {
        if (ma$inventoryListener == null) {
            ma$inventoryListener = new PlayerEvents.MoreAttributesInventoryListener((Player)(Object)this);
        }
        return ma$inventoryListener;
    }
}
