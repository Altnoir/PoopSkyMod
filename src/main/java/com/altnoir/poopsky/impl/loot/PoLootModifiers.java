package com.altnoir.poopsky.impl.loot;

import com.altnoir.poopsky.PoopSky;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public final class PoLootModifiers {
    private static final DeferredRegister<Codec<? extends IGlobalLootModifier>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, PoopSky.MOD_ID);
    public static void register(IEventBus bus) { SERIALIZERS.register("add_table", () -> AddTable.CODEC); SERIALIZERS.register(bus); }
    public static final class AddTable extends LootModifier {
        private static final Codec<AddTable> CODEC = RecordCodecBuilder.create(instance -> codecStart(instance)
                .and(ResourceLocation.CODEC.fieldOf("table").forGetter(modifier -> modifier.table)).apply(instance, AddTable::new));
        private final ResourceLocation table;
        AddTable(LootItemCondition[] conditions, ResourceLocation table) { super(conditions); this.table = table; }
        @Override protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
            context.getLevel().getServer().getLootData().getLootTable(table).getRandomItemsRaw(context, generatedLoot::add);
            return generatedLoot;
        }
        @Override public Codec<? extends IGlobalLootModifier> codec() { return CODEC; }
    }
}
