package org.mantodea.more_attributes.events;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.mantodea.more_attributes.MoreAttributes;
import org.mantodea.more_attributes.capability.PlayerClassCapabilityProvider;
import org.mantodea.more_attributes.messages.AttributesChannel;
import org.mantodea.more_attributes.messages.SyncClassToClientMessage;
import org.mantodea.more_attributes.messages.SyncDataToClientMessage;
import org.mantodea.more_attributes.IMAPlayer;
import org.mantodea.more_attributes.utils.ModifierUtils;
import top.theillusivec4.curios.api.event.CurioChangeEvent;

import javax.annotation.ParametersAreNonnullByDefault;

@Mod.EventBusSubscriber(modid = MoreAttributes.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerEvents {
    @SubscribeEvent
    public static void onEquipChange(LivingEquipmentChangeEvent event) {
        updateEquipLoad(event.getEntity());
    }

    @SubscribeEvent
    public static void onCurioChange(CurioChangeEvent event) {
        updateEquipLoad(event.getEntity());
    }

    @SubscribeEvent
    public static void tick(TickEvent.PlayerTickEvent event) {
        if (!SyncDataToClientMessage.hasSync)
            return;

        if (event.phase == TickEvent.Phase.END) {
            Player player = event.player;

            ModifierUtils.DetailModifiers.Hands.rebuildModifiers(player);

            ModifierUtils.DetailModifiers.EquipLoad.rebuildModifier(player);
        }
    }

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        Entity entity = event.getObject();

        if (entity instanceof Player) {
            event.addCapability(ResourceLocation.fromNamespaceAndPath(MoreAttributes.MODID, "class"), new PlayerClassCapabilityProvider());
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            var originalPlayer = event.getOriginal();
            originalPlayer.reviveCaps();

            var originalCap = originalPlayer.getCapability(MoreAttributes.PLAYER_CLASS).resolve().orElse(null);

            var cloneCap = event.getEntity().getCapability(MoreAttributes.PLAYER_CLASS).resolve().orElse(null);

            if (originalCap != null && cloneCap != null) {
                cloneCap.deserializeNBT(originalCap.serializeNBT());
            }

            originalPlayer.invalidateCaps();

            ModifierUtils.DetailModifiers.Level.rebuildModifiers(event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        event.getEntity().getCapability(MoreAttributes.PLAYER_CLASS).ifPresent(cap -> {
            var data = cap.getClassData();

            if (data != null && event.getEntity() instanceof ServerPlayer serverPlayer) {
                serverPlayer.getServer().execute(() -> {
                    if (serverPlayer.isAddedToWorld()) {
                        AttributesChannel.sendToClient(new SyncClassToClientMessage(data), serverPlayer);
                    }
                });
                serverPlayer.setHealth(serverPlayer.getMaxHealth());
                serverPlayer.inventoryMenu.addSlotListener(((IMAPlayer)serverPlayer).ma$getInventoryListener());
            }
        });
    }

    public static void updateEquipLoad(Entity player) {
        if (player instanceof ServerPlayer serverPlayer)
            ModifierUtils.DetailModifiers.EquipLoad.rebuildModifier(serverPlayer);
    }

    @ParametersAreNonnullByDefault
    public static class MoreAttributesInventoryListener implements ContainerListener {
        public final Player player;

        public MoreAttributesInventoryListener(Player p) {
            this.player = p;
        }

        public void slotChanged(AbstractContainerMenu pContainerToSend, int pSlotInd, ItemStack pStack) {
            updateEquipLoad(player);
        }

        public void dataChanged(AbstractContainerMenu pContainerMenu, int pDataSlotIndex, int pValue) {
        }
    }
}
