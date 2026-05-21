package org.mantodea.more_attributes.events;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;
import org.mantodea.more_attributes.MoreAttributes;
import org.mantodea.more_attributes.attributes.DetailAttributes;
import org.mantodea.more_attributes.datas.ClassLoader;
import org.mantodea.more_attributes.ui.SelectClassScreen;
import org.mantodea.more_attributes.ui.ShowClassScreen;
import org.mantodea.more_attributes.utils.ClassUtils;
import org.mantodea.more_attributes.utils.ResourceLocationUtils;

@Mod.EventBusSubscriber(modid = MoreAttributes.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {
    public static final KeyMapping OpenUI = new KeyMapping(
        "key.more_attributes.open_ui",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_H,
        "key.categories." + MoreAttributes.MODID
    );

    @SubscribeEvent
    public static void registerKeyMappingsEvent(RegisterKeyMappingsEvent event) {
        event.register(OpenUI);
    }

    @Mod.EventBusSubscriber(modid = MoreAttributes.MODID, value = Dist.CLIENT)
    public static class InputEvents {
        @SubscribeEvent
        public static void inputEvent(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) return;

            Minecraft minecraft = Minecraft.getInstance();
            Player player = (Player) minecraft.player;

            if (player == null || minecraft.screen instanceof SelectClassScreen || minecraft.screen instanceof ShowClassScreen || ClassLoader.Classes.isEmpty()) return;

            while(OpenUI.consumeClick())
            {
                Screen screen = ClassUtils.hasSelectClass(player) ? new ShowClassScreen() : new SelectClassScreen();

                minecraft.setScreen(screen);
            }
        }

        @SubscribeEvent
        public static void onScreenRender(ScreenEvent.Render.Post event) {
            if (!(event.getScreen() instanceof InventoryScreen)) return;

            Minecraft minecraft = Minecraft.getInstance();
            Player player = minecraft.player;
            if (player == null || player.isCreative() || player.isSpectator()) return;

            AttributeInstance currentAttr = player.getAttribute(DetailAttributes.EquipLoadCurrent);
            AttributeInstance maxAttr = player.getAttribute(DetailAttributes.EquipLoadMax);
            if (currentAttr == null || maxAttr == null) return;

            double currentLoad = currentAttr.getValue();
            double maxLoad = maxAttr.getValue();
            if (maxLoad <= 0) return;

            int percent = (int) (currentLoad / maxLoad * 100);

            GuiGraphics graphics = event.getGuiGraphics();
            Font font = minecraft.font;

            InventoryScreen screen = (InventoryScreen) event.getScreen();
            int leftPos = screen.getGuiLeft();
            int topPos = screen.getGuiTop();

            // 图标位置：合成格和装备栏之间
            // 原版背包：leftPos+80 是合成格右边缘，leftPos+98 是装备栏左边缘
            // 中间位置约 leftPos+89
            int iconWidth = 14;
            int iconHeight = 9;
            int iconX = leftPos + 78;
            int iconY = topPos + 10;

            // 渲染底板 weight_0
            graphics.blit(ResourceLocationUtils.GUI.Weight0, iconX, iconY, 0, 0, iconWidth, iconHeight, iconWidth, iconHeight);

            // 根据负重百分比从下到上叠加覆盖层
            if (percent > 0) {
                double overlayHeight;
                ResourceLocation overlay;

                if (percent <= 100) {
                    overlay = ResourceLocationUtils.GUI.Weight1;
                    overlayHeight = iconHeight * (percent / 100.0);
                } else if (percent <= 200) {
                    overlay = ResourceLocationUtils.GUI.Weight2;
                    overlayHeight = iconHeight * ((percent - 100) / 100.0);
                } else {
                    overlay = ResourceLocationUtils.GUI.Weight3;
                    overlayHeight = percent >= 300 ? iconHeight : iconHeight * ((percent - 200) / 100.0);
                }

                // 先渲染完整的上一层作为底色
                if (percent > 100 && percent <= 200) {
                    graphics.blit(ResourceLocationUtils.GUI.Weight1, iconX, iconY, 0, 0, iconWidth, iconHeight, iconWidth, iconHeight);
                } else if (percent > 200) {
                    graphics.blit(ResourceLocationUtils.GUI.Weight2, iconX, iconY, 0, 0, iconWidth, iconHeight, iconWidth, iconHeight);
                }

                // 从下到上渲染当前层，使用内边距实现亚像素平滑
                int fullHeight = (int) overlayHeight;
                double partialHeight = overlayHeight - fullHeight;
                int offsetY = iconHeight - fullHeight;

                // 渲染完整像素行
                if (fullHeight > 0) {
                    graphics.blit(overlay, iconX, iconY + offsetY, 0, offsetY, iconWidth, fullHeight, iconWidth, iconHeight);
                }

                // 渲染最顶部一行带透明度的亚像素行
                if (partialHeight > 0.01 && offsetY > 0) {
                    int alpha = (int) (partialHeight * 255);
                    RenderSystem.enableBlend();
                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha / 255.0F);
                    graphics.blit(overlay, iconX, iconY + offsetY - 1, 0, offsetY - 1, iconWidth, 1, iconWidth, iconHeight);
                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                    RenderSystem.disableBlend();
                }
            }

            // 鼠标悬停提示
            int mouseX = (int) (minecraft.mouseHandler.xpos() * minecraft.getWindow().getGuiScaledWidth() / minecraft.getWindow().getScreenWidth());
            int mouseY = (int) (minecraft.mouseHandler.ypos() * minecraft.getWindow().getGuiScaledHeight() / minecraft.getWindow().getScreenHeight());

            if (mouseX >= iconX && mouseX <= iconX + iconWidth && mouseY >= iconY && mouseY <= iconY + iconHeight) {
                String statusKey = getLoadStatusKey(percent);
                MutableComponent tooltip = Component.translatable(statusKey);
                graphics.renderComponentTooltip(font, List.of(tooltip), mouseX, mouseY);
            }
        }

        private static String getLoadStatusKey(int percent) {
            if (percent <= 0) return "more_attributes.ui.load_status.none";
            if (percent <= 50) return "more_attributes.ui.load_status.light";
            if (percent <= 100) return "more_attributes.ui.load_status.full";
            if (percent <= 150) return "more_attributes.ui.load_status.overweight";
            if (percent <= 200) return "more_attributes.ui.load_status.heavy";
            if (percent <= 250) return "more_attributes.ui.load_status.extreme";
            if (percent <= 300) return "more_attributes.ui.load_status.immobile";
            return "more_attributes.ui.load_status.crushed";
        }
    }
}
