package org.mantodea.more_attributes.events;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.mantodea.more_attributes.MoreAttributes;
import org.mantodea.more_attributes.attributes.DetailAttributes;
import org.mantodea.more_attributes.datas.*;
import org.mantodea.more_attributes.datas.ClassLoader;
import org.mantodea.more_attributes.messages.AttributesChannel;
import org.mantodea.more_attributes.messages.SyncDataToClientMessage;
import org.mantodea.more_attributes.IMAPlayer;
import org.mantodea.more_attributes.utils.ModifierUtils;

import java.util.Objects;

@Mod.EventBusSubscriber(modid = MoreAttributes.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonEvents {
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();

        if (player instanceof ServerPlayer serverPlayer)
        {
            var cap = serverPlayer.getCapability(MoreAttributes.PLAYER_CLASS).resolve().orElse(null);

            var classData = cap == null ? new ClassData() : cap.getClassData();

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(ClassData.class, ClassData.serializer)
                    .create();

            JsonArray classes = gson.toJsonTree(ClassLoader.Classes).getAsJsonArray();

            JsonArray attributes = gson.toJsonTree(AttributeLoader.Attributes).getAsJsonArray();

            JsonArray details = gson.toJsonTree(DetailLoader.Details).getAsJsonArray();

            JsonArray itemModifiers = gson.toJsonTree(ItemModifierLoader.Modifiers).getAsJsonArray();

            JsonArray array = new JsonArray();

            array.add(gson.toJsonTree(classData));
            array.add(classes);
            array.add(attributes);
            array.add(details);
            array.add(itemModifiers);

            AttributesChannel.sendToClient(new SyncDataToClientMessage(array), serverPlayer);

            ModifierUtils.DetailModifiers.Level.rebuildModifiers(player);

            serverPlayer.inventoryMenu.addSlotListener(((IMAPlayer)serverPlayer).ma$getInventoryListener());
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEntityJump(LivingEvent.LivingJumpEvent event) {
        if (event.getEntity() instanceof Player player) {
            double currentLoad = Objects.requireNonNull(player.getAttribute(DetailAttributes.EquipLoadCurrent)).getValue();

            double maxLoad = Objects.requireNonNull(player.getAttribute(DetailAttributes.EquipLoadMax)).getValue();

            if (currentLoad > maxLoad * 3) {
                player.setDeltaMovement(player.getDeltaMovement().x, -1, player.getDeltaMovement().z);

                player.resetFallDistance();
            }
        }
    }
}
