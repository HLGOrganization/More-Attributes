package org.mantodea.more_attributes;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.mantodea.more_attributes.attributes.DetailAttributes;
import org.mantodea.more_attributes.capability.IPlayerClassCapability;
import org.mantodea.more_attributes.configs.MoreAttributesConfig;
import org.mantodea.more_attributes.datas.AttributeLoader;
import org.mantodea.more_attributes.datas.ClassLoader;
import org.mantodea.more_attributes.datas.DetailLoader;
import org.mantodea.more_attributes.datas.ItemModifierLoader;
import org.mantodea.more_attributes.messages.AttributesChannel;
import org.mantodea.more_attributes.utils.AttributeUtils;
import org.slf4j.Logger;

@Mod(MoreAttributes.MODID)
public class MoreAttributes {
    public static final String MODID = "more_attributes";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static Capability<IPlayerClassCapability> PLAYER_CLASS = CapabilityManager.get(new CapabilityToken<>() {});

    public MoreAttributes() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, MoreAttributesConfig.Common.CommonSpec);
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        DetailAttributes.triggerClassLoader();
        AttributeUtils.register();
        AttributesChannel.RegisterMessages();
        modEventBus.addListener(AttributeUtils::registerPlayerAttribute);
        MinecraftForge.EVENT_BUS.addListener(this::reloadListenerEvent);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void reloadListenerEvent(AddReloadListenerEvent event) {
        event.addListener(new AttributeLoader());
        event.addListener(new ClassLoader());
        event.addListener(new DetailLoader());
        event.addListener(new ItemModifierLoader());
    }
}
