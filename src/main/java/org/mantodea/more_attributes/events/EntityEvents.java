package org.mantodea.more_attributes.events;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.mantodea.more_attributes.MoreAttributes;
import org.mantodea.more_attributes.attributes.DetailAttributes;

import java.util.Random;

@Mod.EventBusSubscriber(modid = MoreAttributes.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EntityEvents {

    @SubscribeEvent
    public static void livingEntityDamage(LivingDamageEvent event) {
        Entity sourceEntity = event.getSource().getEntity();

        Entity entity = event.getEntity();

        if(sourceEntity instanceof Player player) {
            AttributeInstance meleeDamage = player.getAttribute(DetailAttributes.MeleeDamage);

            if (meleeDamage != null) {
                event.setAmount(event.getAmount() * (float) meleeDamage.getValue());
            }

            AttributeInstance criticalDamage = player.getAttribute(DetailAttributes.CriticalDamage);

            AttributeInstance criticalChance = player.getAttribute(DetailAttributes.CriticalChance);

            if(criticalDamage != null && criticalChance != null) {
                if (new Random().nextDouble() < criticalChance.getValue())
                {
                    event.setAmount(event.getAmount() * (float) criticalDamage.getValue());

                    int particleCount = (int) (15 * entity.getBbWidth() * entity.getBbWidth());

                    ServerLevel server = (ServerLevel) player.level();

                    server.sendParticles(
                        (ServerPlayer) player, ParticleTypes.CRIT, true,
                        entity.getX(), entity.getY() + entity.getBbHeight() / 2, entity.getZ(),
                        particleCount,
                        entity.getBbWidth() / 2, entity.getBbHeight() / 2, entity.getBbWidth() / 2,
                        0.2
                    );

                    server.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1.5F, 1.0F);
                }
            }
        }

        if(entity instanceof Player player) {
            if (player.isCreative() || player.isSpectator()) return;

            if (event.getSource().is(DamageTypeTags.IS_FALL)) {
                AttributeInstance currentAttr = player.getAttribute(DetailAttributes.EquipLoadCurrent);
                AttributeInstance maxAttr = player.getAttribute(DetailAttributes.EquipLoadMax);
                if (currentAttr != null && maxAttr != null) {
                    double maxLoad = maxAttr.getValue();
                    if (maxLoad > 0) {
                        int overPercent = (int) (currentAttr.getValue() / maxLoad * 100) - 100;
                        if (overPercent > 0) {
                            event.setAmount(event.getAmount() * (1.0f + overPercent / 100.0f));
                        }
                    }
                }
            }

            AttributeInstance damageReduction = player.getAttribute(DetailAttributes.DamageReduction);

            if (damageReduction != null) {
                event.setAmount(event.getAmount() * (float) (1 - damageReduction.getValue()));
            }
        }
    }
}
