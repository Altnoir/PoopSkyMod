package com.altnoir.poopsky.compat.create.content.logistics;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.compat.create.PSRecipeTypes;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttributeType;
import com.simibubi.create.content.logistics.item.filter.attribute.SingletonItemAttribute;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.BiPredicate;

public class PSItemAttributes {

    public static final DeferredRegister<ItemAttributeType> ITEM_ATTRIBUTE_TYPES =
            DeferredRegister.create(CreateBuiltInRegistries.ITEM_ATTRIBUTE_TYPE, PoopSky.MOD_ID);

    public static final DeferredHolder<ItemAttributeType, ItemAttributeType> CAN_BE_DIGESTED =
            ITEM_ATTRIBUTE_TYPES.register("can_be_digested",
                    () -> singleton("can_be_digested", (s, w) -> testRecipe(s, w, PSRecipeTypes.DIGESTING.getType()))
            );

    private static <T extends Recipe<SingleRecipeInput>> boolean testRecipe(ItemStack s, Level w, RecipeType<T> type) {
        return w.getRecipeManager()
                .getRecipeFor(type, new SingleRecipeInput(s.copy()), w)
                .isPresent();
    }

    private static ItemAttributeType singleton(String id, BiPredicate<ItemStack, Level> predicate) {
        return new SingletonItemAttribute.Type(type -> new SingletonItemAttribute(type, predicate, id));
    }

    public static void register(IEventBus modEventBus) {
        ITEM_ATTRIBUTE_TYPES.register(modEventBus);
    }
}