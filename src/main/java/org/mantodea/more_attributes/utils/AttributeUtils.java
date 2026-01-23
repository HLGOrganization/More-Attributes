package org.mantodea.more_attributes.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.mantodea.more_attributes.MoreAttributes;
import org.mantodea.more_attributes.datas.AttributeData;
import org.mantodea.more_attributes.datas.AttributeLoader;
import org.mantodea.more_attributes.datas.DetailData;
import org.mantodea.more_attributes.datas.DetailLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AttributeUtils {
    public static Attribute rangedDetail(String name, double defaultVal, double min, double max) {
        Attribute attr = new RangedAttribute(name, defaultVal, min, max);

        MyModAttributes.put(name, attr);

        return attr;
    }

    public static HashMap<String, Attribute> MyModAttributes = new HashMap<>();

    public static HashMap<String, Attribute> OtherModDetailAttributes = new HashMap<>();

    public static Map<String, Attribute> getAllDetailAttributes() {
        return Stream.concat(MyModAttributes.entrySet().stream(),  OtherModDetailAttributes.entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static ResourceLocation resourceLocationBuilder(String name)
    {
        return new ResourceLocation(MoreAttributes.MODID, name);
    }

    public static void register()
    {
        for (var pair : MyModAttributes.entrySet())
        {
            ForgeRegistries.ATTRIBUTES.register(resourceLocationBuilder(pair.getKey()), pair.getValue());
        }
    }

    public static void registerPlayerAttribute(EntityAttributeModificationEvent event)
    {
        for (var attr : MyModAttributes.values())
        {
            event.add(EntityType.PLAYER, attr);
        }
    }

    public static AttributeData getAttributeData(String attributeName)
    {
        return AttributeLoader.Attributes.stream().filter(attr -> attr.name.equals(attributeName)).findFirst().orElse(null);
    }

    public static DetailData getDetailData(String modName, String detailName)
    {
        return DetailLoader.Details.stream().filter(detail -> detail.name.equals(detailName) && detail.mod.contains(modName)).findFirst().orElse(null);
    }

    public static List<Component> getAttributeDetails(AttributeData data, int level)
    {
        var list = ModifierUtils.getComponentsForLevel(data.modifiers);

        list.add(0, Component.translatable("more_attributes.ui.base_value", Integer.toString(data.baseLevel)));

        list.add(1, Component.translatable("more_attributes.ui.base_value_tip").withStyle(Style.EMPTY.withColor(0xFF0000)));

        list.add(2, Component.empty());

        list.add(3, Component.translatable("more_attributes.ui.per_level"));

        return list;
    }
}
