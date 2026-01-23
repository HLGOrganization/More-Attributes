package org.mantodea.more_attributes.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.mantodea.more_attributes.MoreAttributes;
import org.mantodea.more_attributes.datas.ClassData;
import org.mantodea.more_attributes.datas.ClassLoader;
import org.mantodea.more_attributes.ui.SelectClassScreen;

import java.util.List;

public class ClassUtils {
    public static List<MutableComponent> getClassDescription(String className, Font font) {
        String key = LangUtils.getClassDescriptionKey(className);

        MutableComponent c = Component.translatable(key);

        String description = c.getString();

        List<String> lines = StringUtils.splitStringByWidth(description, 150, font);

        Style sung = Style.EMPTY.withFont(new ResourceLocation("more_attributes:chusung"));

        return lines.stream().map(str -> Component.literal(str).withStyle(sung)).toList();
    }

    public static boolean hasSelectClass(Player player) {
        var cap = player.getCapability(MoreAttributes.PLAYER_CLASS).resolve().orElse(null);

        if (cap != null) {
            return !cap.getClassName().isEmpty();
        }

        return false;
    }

    public static ClassData getPlayerClass(Player player) {
        var cap = player.getCapability(MoreAttributes.PLAYER_CLASS).resolve().orElse(null);

        if (cap != null) {
            return cap.getClassData();
        }

        return new ClassData();
    }

    public static void setPlayerClass(Player player, ClassData classData) {
        player.getCapability(MoreAttributes.PLAYER_CLASS).resolve().ifPresent(cap -> {
            boolean first_time = cap.getClassName().isEmpty();
            cap.setClass(classData);
            if (!(player instanceof ServerPlayer)) {
                TryOpenSelectScreen(player);
            } else {
                ModifierUtils.DetailModifiers.Level.rebuildModifiers(player);
                if (first_time)
                    player.setHealth(player.getMaxHealth());
            }
        });
    }

    @OnlyIn(Dist.CLIENT)
    public static void TryOpenSelectScreen(Player player)
    {
        if (!ClassUtils.hasSelectClass(player) && !player.isDeadOrDying() && !ClassLoader.Classes.isEmpty()) {
            Minecraft.getInstance().setScreen(new SelectClassScreen());
        }
    }
}
