package org.mantodea.more_attributes.datas;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.mantodea.more_attributes.MoreAttributes;
import org.mantodea.more_attributes.configs.MoreAttributesConfig;
import org.mantodea.more_attributes.utils.ModUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ClassLoader extends SimpleJsonResourceReloadListener {
    public static Gson GSON = new GsonBuilder().create();

    public static List<ClassData> Classes = new ArrayList<>();

    public ClassLoader() {
        super(GSON, "classes");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        Classes.clear();

        for(JsonElement jsonElement : map.values()) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(ClassData.class, ClassData.deserializer)
                    .create();
            ClassData data = gson.fromJson(jsonElement, ClassData.class);

            if(data == null || !ModUtils.checkCondition(data.displayCondition))
                continue;

            boolean illegal = false;

            for(Map.Entry<String, Integer> entry : data.attributes.entrySet())
            {
                if(!data.attributes.containsKey(entry.getKey()))
                {
                    MoreAttributes.LOGGER.error("Couldn't find attribute " + entry.getKey());
                    illegal = true;
                    break;
                }
            }

            if (!illegal)
                Classes.add(data);
        }

        sortClasses();
    }

    private static void sortClasses() {
        List<String> order = MoreAttributesConfig.Common.Instance.classOrder.get();

        if (order.isEmpty())
            return;

        Map<String, ClassData> byName = new LinkedHashMap<>();
        for (var cls : Classes) {
            byName.put(cls.name, cls);
        }

        List<ClassData> sorted = new ArrayList<>();

        for (var name : order) {
            ClassData data = byName.remove(name);
            if (data != null) {
                sorted.add(data);
            }
        }

        sorted.addAll(byName.values());

        Classes = sorted;
    }
}
