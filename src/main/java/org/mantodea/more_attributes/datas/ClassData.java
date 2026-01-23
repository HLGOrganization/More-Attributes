package org.mantodea.more_attributes.datas;

import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassData extends ConditionalContent {
    public ClassData() {
        for (var attribute : AttributeLoader.Attributes) {
            attributes.put(attribute.name, 0);
        }
    }

    public String name = "";

    public Map<String, Integer> attributes = new HashMap<>();

    public List<ItemStack> startItems = new ArrayList<>();

    public static JsonDeserializer<ClassData> deserializer = (json, typeOfT, context) -> {
        var jsonObject = json.getAsJsonObject();
        var res = new ClassData();
        if (jsonObject.has("name")) {
            res.name = jsonObject.get("name").getAsString();
        }
        if (jsonObject.has("attributes")) {
            JsonObject attrObject = jsonObject.getAsJsonObject("attributes");
            for (Map.Entry<String, JsonElement> entry : attrObject.entrySet()) {
                res.attributes.put(entry.getKey(), entry.getValue().getAsInt());
            }
        }
        if (jsonObject.has("startItems")) {
            JsonArray itemsArray = jsonObject.getAsJsonArray("startItems");
            for (JsonElement itemElement : itemsArray) {
                var itemObject = itemElement.getAsJsonObject();
                if (!ForgeRegistries.ITEMS.containsKey(ResourceLocation.parse(GsonHelper.getAsString(itemObject, "item"))))
                    continue;
                res.startItems.add(CraftingHelper.getItemStack(itemElement.getAsJsonObject(), true));
            }
        }
        return res;
    };

    public static JsonSerializer<ClassData> serializer = (src, typeOfSrc, context) -> {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("name", src.name);

        if (src.attributes != null && !src.attributes.isEmpty()) {
            JsonObject attrObject = new JsonObject();
            for (Map.Entry<String, Integer> entry : src.attributes.entrySet()) {
                attrObject.addProperty(entry.getKey(), entry.getValue());
            }
            jsonObject.add("attributes", attrObject);
        }

        if (src.startItems != null) {
            JsonArray itemsArray = new JsonArray();
            for (var item : src.startItems) {
                JsonObject json = new JsonObject();
                json.addProperty("item", ForgeRegistries.ITEMS.getKey(item.getItem()).toString());
                json.addProperty("count", item.getCount());

                if (item.hasTag()) {
                    json.addProperty("nbt", item.getTag().toString());
                }
                itemsArray.add(json);
            }
            jsonObject.add("startItems", itemsArray);
        }

        return jsonObject;
    };
}
