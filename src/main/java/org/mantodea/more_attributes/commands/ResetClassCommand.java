package org.mantodea.more_attributes.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import org.mantodea.more_attributes.datas.ClassData;
import org.mantodea.more_attributes.messages.AttributesChannel;
import org.mantodea.more_attributes.messages.SyncClassToClientMessage;
import org.mantodea.more_attributes.utils.ClassUtils;

public class ResetClassCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("more_attributes")
            .then(
                Commands.literal("reset_class")
                .then(
                    Commands.argument("player", EntityArgument.player())
                    .executes(context -> {
                        ServerPlayer player = EntityArgument.getPlayer(context, "player");
                        ClassUtils.setPlayerClass(player, new ClassData());
                        AttributesChannel.sendToClient(new SyncClassToClientMessage(new ClassData()), player);
                        return 0;
                    })
                )
            )
        );
    }
}
