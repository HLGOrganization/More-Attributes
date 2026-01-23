package org.mantodea.more_attributes.commands;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.mantodea.more_attributes.MoreAttributes;

@Mod.EventBusSubscriber(modid = MoreAttributes.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommandRegister {
    @SubscribeEvent
    public static void registerCommand(RegisterCommandsEvent event){
        ResetClassCommand.register(event.getDispatcher());
    }
}
