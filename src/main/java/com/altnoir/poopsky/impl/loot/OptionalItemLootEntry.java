package com.altnoir.poopsky.impl.loot;

import com.altnoir.poopsky.init.PoLootFunctions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.function.Consumer;

/** 延迟查找可选联动物品，未安装对应模组时不产出，也不会使整张掉落表加载失败。 */
public final class OptionalItemLootEntry extends LootPoolSingletonContainer {
    private final ResourceLocation itemId;

    private OptionalItemLootEntry(ResourceLocation itemId, int weight, int quality,
                                  LootItemCondition[] conditions, LootItemFunction[] functions) {
        super(weight, quality, conditions, functions);
        this.itemId = itemId;
    }

    @Override
    public LootPoolEntryType getType() {
        return PoLootFunctions.OPTIONAL_ITEM.get();
    }

    @Override
    protected void createItemStack(Consumer<ItemStack> output, LootContext context) {
        BuiltInRegistries.ITEM.getOptional(itemId).ifPresent(item -> output.accept(new ItemStack(item)));
    }

    public static final class Serializer extends LootPoolSingletonContainer.Serializer<OptionalItemLootEntry> {
        @Override
        public void serializeCustom(JsonObject json, OptionalItemLootEntry entry, JsonSerializationContext context) {
            super.serializeCustom(json, entry, context);
            json.addProperty("name", entry.itemId.toString());
        }

        @Override
        protected OptionalItemLootEntry deserialize(JsonObject json, JsonDeserializationContext context,
                                                      int weight, int quality, LootItemCondition[] conditions,
                                                      LootItemFunction[] functions) {
            ResourceLocation itemId = ResourceLocation.tryParse(GsonHelper.getAsString(json, "name"));
            if (itemId == null) throw new com.google.gson.JsonSyntaxException("Invalid optional item id");
            return new OptionalItemLootEntry(itemId, weight, quality, conditions, functions);
        }
    }
}
