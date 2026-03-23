package net.mcreator.concoction.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.handlers.FoodAftertasteHandler;
import net.mcreator.concoction.item.food.passive.FoodPassiveEffectType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = ConcoctionMod.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class AftertasteCommand {
    private static final SimpleCommandExceptionType TARGET_NOT_LIVING = new SimpleCommandExceptionType(
            Component.literal("Target must be a living entity.")
    );

    private AftertasteCommand() {
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        var root = Commands.literal("aftertaste")
                .requires(source -> source.hasPermission(2));

        root.then(Commands.argument("target", EntityArgument.entity())
                .then(Commands.literal("add")
                        .then(Commands.argument("type", StringArgumentType.word())
                                .suggests(AftertasteCommand::suggestAftertastes)
                                .executes(context -> addAftertaste(
                                        context,
                                        StringArgumentType.getString(context, "type"),
                                        getTarget(context)
                                ))))
                .then(Commands.literal("remove")
                        .then(Commands.argument("type", StringArgumentType.word())
                                .suggests(AftertasteCommand::suggestAftertastes)
                                .executes(context -> removeAftertaste(
                                        context,
                                        StringArgumentType.getString(context, "type"),
                                        getTarget(context)
                                ))))
                .then(Commands.literal("clear")
                        .executes(context -> clearAftertastes(context, getTarget(context)))));

        event.getDispatcher().register(root);
    }

    private static int addAftertaste(CommandContext<CommandSourceStack> context, String name, LivingEntity target) {
        FoodPassiveEffectType type = parseAftertaste(name);
        boolean changed = FoodAftertasteHandler.addPermanentAftertaste(target, type);
        context.getSource().sendSuccess(
                () -> changed
                        ? Component.literal("Added permanent aftertaste to ")
                                .append(target.getDisplayName())
                                .append(Component.literal(": "))
                                .append(type.getTooltipTitle())
                        : Component.literal("Permanent aftertaste already present on ")
                                .append(target.getDisplayName())
                                .append(Component.literal(": "))
                                .append(type.getTooltipTitle()),
                false
        );
        return 1;
    }

    private static int removeAftertaste(CommandContext<CommandSourceStack> context, String name, LivingEntity target) {
        FoodPassiveEffectType type = parseAftertaste(name);
        boolean changed = FoodAftertasteHandler.removeAftertasteCompletely(target, type);
        context.getSource().sendSuccess(
                () -> changed
                        ? Component.literal("Removed aftertaste from ")
                                .append(target.getDisplayName())
                                .append(Component.literal(": "))
                                .append(type.getTooltipTitle())
                        : Component.literal("Aftertaste was not active on ")
                                .append(target.getDisplayName())
                                .append(Component.literal(": "))
                                .append(type.getTooltipTitle()),
                false
        );
        return 1;
    }

    private static int clearAftertastes(CommandContext<CommandSourceStack> context, LivingEntity target) {
        FoodAftertasteHandler.clearAllAftertastes(target);
        context.getSource().sendSuccess(
                () -> Component.literal("Cleared all aftertastes and food memory for ").append(target.getDisplayName()),
                false
        );
        return 1;
    }

    private static LivingEntity getTarget(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(context, "target");
        if (entity instanceof LivingEntity livingEntity) {
            return livingEntity;
        }
        throw TARGET_NOT_LIVING.create();
    }

    private static FoodPassiveEffectType parseAftertaste(String name) {
        FoodPassiveEffectType type = FoodPassiveEffectType.getByName(name);
        if (!type.isAftertaste()) {
            throw new IllegalArgumentException("Not an aftertaste: " + name);
        }
        return type;
    }

    private static CompletableFuture<Suggestions> suggestAftertastes(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(
                Arrays.stream(FoodPassiveEffectType.values())
                        .filter(FoodPassiveEffectType::isAftertaste)
                        .map(FoodPassiveEffectType::getSerializedName),
                builder
        );
    }
}
