package org.mantodea.more_attributes.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.mantodea.more_attributes.datas.AttributeData;
import org.mantodea.more_attributes.datas.ClassData;
import org.mantodea.more_attributes.datas.DetailData;
import org.mantodea.more_attributes.utils.*;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

public class ShowClassScreen extends Screen {

    public int page;

    public int pageMax;

    public int posX;

    public int posY;

    public int backgroundWidth = 400;

    public int backgroundHeight = 200;

    public int attrPerPage = 20;

    public Coordinates coordinates;

    public ResourceLocation classIcon;

    public MutableComponent className;

    public List<MutableComponent> descriptions;

    public ClassData classData;

    public Style sung = Style.EMPTY.withFont(new ResourceLocation("more_attributes:chusung"));

    public ShowClassScreen() {
        super(Component.empty());
        super.minecraft = Minecraft.getInstance();

        page = 0;

        pageMax = AttributeUtils.getAllDetailAttributes().size() / attrPerPage + 2;
    }

    @Override
    public void init() {
        if (minecraft != null) {
            posX = (super.width - backgroundWidth) / 2;

            posY = (super.height - backgroundHeight) /2;

            coordinates = new Coordinates(posX, posY, backgroundWidth, backgroundHeight, font);

            updateClass();

            updateButtons();

            ModifierUtils.DetailModifiers.Level.rebuildModifiers(minecraft.player);
        }
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(graphics);

        super.render(graphics, mouseX, mouseY, partialTicks);

        if (page == 0) {
            renderClassIcon(graphics);

            renderClassName(graphics);

            renderClassDescription(graphics);

            renderLevelAttributes(graphics, mouseX, mouseY);
        }
        else
        {
            renderDetailAttributes(graphics);
        }
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics graphics) {
        super.renderBackground(graphics);

        graphics.blit(ResourceLocationUtils.GUI.Background, posX, posY, 0, 0F, 0F, backgroundWidth, backgroundHeight, backgroundWidth, backgroundHeight);
        if (page == 0)
            graphics.blit(ResourceLocationUtils.GUI.ClassIconBox, coordinates.classIconBoxPosX, coordinates.classIconBoxPosY, 0, 0, 130, 87, 130, 87);
    }

    public void renderClassIcon(GuiGraphics graphics)
    {
        graphics.blit(classIcon, coordinates.classIconBoxPosX, coordinates.classIconBoxPosY, 0F, 0F, 130, 81, 130, 81);
    }

    public void renderClassName(GuiGraphics graphics)
    {
        int lineHeight = font.lineHeight;

        int classNamePosX = posX + 83 + (60 - font.width(className.getVisualOrderText())) / 2;
        int classNamePosY = posY + 92 + (15 - lineHeight) / 2;

        RenderUtils.renderBorderScaleText(graphics, font, className, classNamePosX, classNamePosY, 0x603B38, 0xF0DCB7, 1.5f);
    }

    public void renderClassDescription(GuiGraphics graphics)
    {
        int descriptionPosX = coordinates.descriptionPosX;

        int descriptionPosY = coordinates.descriptionPosY;

        for(MutableComponent description : descriptions)
        {
            RenderUtils.renderBorderScaleText(graphics, font, description, descriptionPosX, descriptionPosY, 0x603B38, 0xF0DCB7, 1);
            descriptionPosY += font.lineHeight;
        }
    }

    public void renderLevelAttributes(GuiGraphics graphics, int mouseX, int mouseY) {
        int lineHeight = font.lineHeight;

        int attributePosX = coordinates.startAttributesPosX;

        int attributePosY = coordinates.startAttributesPosY;

        int attributeLevelPosX = attributePosX + 30;

        MutableComponent detail = Component.translatable("more_attributes.ui.detail").withStyle(sung);

        int detailPosX = attributePosX + 100;

        int detailWidth = font.width(detail.getVisualOrderText());

        for (Map.Entry<String, Integer> entry : classData.attributes.entrySet())
        {
            String name = entry.getKey();

            AttributeData data = AttributeUtils.getAttributeData(name);

            if(data == null || !ModUtils.checkCondition(data.displayCondition))
                continue;

            int level = entry.getValue();

            ResourceLocation icon = ResourceLocationUtils.GUI.getAttributeIcon(name);

            graphics.blit(icon, attributePosX - 16, attributePosY - 5, 0, 0f, 0f, 12, 12, 12, 12);

            RenderUtils.renderBorderScaleText(graphics, font, Component.translatable(LangUtils.getAttributeNameKey(name)).withStyle(sung), attributePosX, attributePosY, 0x603B38, 0xF0DCB7, 1);

            RenderUtils.renderBorderScaleText(graphics, font, Component.literal(String.valueOf(level)).withStyle(sung), attributeLevelPosX, attributePosY, 0x603B38, 0xF0DCB7, 1);

            RenderUtils.renderBorderScaleText(graphics, font, detail, detailPosX, attributePosY, 0x603B38, 0xF0DCB7, 1);

            if(mouseX >= detailPosX && mouseX <= detailWidth + detailPosX && mouseY >= attributePosY && mouseY <= attributePosY + lineHeight)
            {
                List<Component> details = AttributeUtils.getAttributeDetails(data, level);

                graphics.renderComponentTooltip(font, details, mouseX, mouseY);
            }

            attributePosY += lineHeight + 4;
        }
    }

    public void renderDetailAttributes(GuiGraphics graphics) {
        if (minecraft == null || page == 0 || minecraft.player == null) return;

        int startIndex = attrPerPage * (page - 1);

        int index = attrPerPage * (page - 1);

        var entrySet = AttributeUtils.getAllDetailAttributes().entrySet().stream().toList();

        int attributePosX = coordinates.startAttributesPosX;

        int attributePosY = coordinates.startAttributesPosY;

        while (index < entrySet.size() && index < startIndex + attrPerPage)
        {
            var entry = entrySet.get(index - startIndex);

            if (index - startIndex == attrPerPage / 2)
                attributePosY = coordinates.startAttributesPosY;

            String key = entry.getKey();

            String mod = key.contains(":") ? key.split(":")[0] : "more_attributes";

            String name = key.contains(":") ? key.split(":")[1] : key;

            DetailData data = AttributeUtils.getDetailData(mod, name);

            var display = LangUtils.getDetailNameKey(mod, name,"display");

            var instance = minecraft.player.getAttribute(entry.getValue());

            double val = instance.getValue();

            String str;
            if (name.equals("equip_load_max")) {
                double baseVal = instance.getBaseValue();
                double extra = val - baseVal;
                if (extra > 0.5) {
                    str = new DecimalFormat("0").format(baseVal) + "(+" + new DecimalFormat("0").format(extra) + ")";
                } else if (extra < -0.5) {
                    str = new DecimalFormat("0").format(baseVal) + "(-" + new DecimalFormat("0").format(-extra) + ")";
                } else {
                    str = new DecimalFormat("0").format(val);
                }
            } else {
                boolean percent = !data.displayAsPercentage;
                str = new DecimalFormat("0.00").format(val * (percent ? 1 : 100)).replace(".", ". ") + (percent ? "" : "%");
            }

            int drawX = index - startIndex < attrPerPage / 2 ? attributePosX - 176 : attributePosX;

            RenderUtils.renderBorderScaleText(graphics, font, Component.translatable(display, str).withStyle(sung), drawX, attributePosY, 0x603B38, 0xF0DCB7, 1);

            attributePosY += font.lineHeight + 5;
            index++;
        }
    }

    public void updateClass()
    {
        if (minecraft != null) {
            if (minecraft.player != null) {
                classData = ClassUtils.getPlayerClass(minecraft.player);

                className = Component.translatable(LangUtils.getClassNameKey(classData.name)).withStyle(sung);

                classIcon = ResourceLocationUtils.GUI.getClassIcon(classData.name);

                descriptions = ClassUtils.getClassDescription(classData.name, font);
            }
        }
    }

    public void updateButtons() {

        if (page > 0)
        {
            ImageButton previous = new ImageButton(coordinates.previousPagePosX, coordinates.turnPagePosY, 17, 24,
                    Component.empty(),
                    ResourceLocationUtils.GUI.PreviousPage,
                    ResourceLocationUtils.GUI.PreviousPageHover
            );
            previous.setPress(b -> previousPage());
            addRenderableWidget(previous);
        }

        if (page < pageMax - 1)
        {
            ImageButton next = new ImageButton(coordinates.nextPagePosX, coordinates.turnPagePosY, 17, 24,
                    Component.empty(),
                    ResourceLocationUtils.GUI.NextPage,
                    ResourceLocationUtils.GUI.NextPageHover
            );
            next.setPress(b -> nextPage());
            addRenderableWidget(next);
        }
    }

    public void previousPage()
    {
        if(page > 0)
        {
            page--;
            clearWidgets();

            updateClass();

            updateButtons();
        }
    }

    public void nextPage()
    {
        if(page < pageMax - 1)
        {
            page++;
            clearWidgets();

            updateClass();

            updateButtons();
        }
    }
}
