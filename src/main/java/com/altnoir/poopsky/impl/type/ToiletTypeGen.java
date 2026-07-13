package com.altnoir.poopsky.impl.type;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.block.ToiletType;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class ToiletTypeGen implements DataProvider {
    private final PackOutput.PathProvider pathProvider;

    public ToiletTypeGen(PackOutput packOutput) {
        this.pathProvider = packOutput.createPathProvider(
                PackOutput.Target.DATA_PACK,
                PoopSky.MOD_ID + "_data/toilet_type"
        );
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        // 先生成所有已注册类型的 JSON 文件
        CompletableFuture<?>[] futures = ToiletType.getAll().values().stream()
                .map(type -> generateType(cachedOutput, type))
                .toArray(CompletableFuture[]::new);
        return CompletableFuture.allOf(futures);
    }

    private CompletableFuture<?> generateType(CachedOutput cachedOutput, ToiletType type) {
        JsonObject json = new JsonObject();

        // source_block
        Block sourceBlock = type.sourceBlock();
        if (sourceBlock != null) {
            json.addProperty("source_block", BuiltInRegistries.BLOCK.getKey(sourceBlock).toString());
        } else {
            json.add("source_block", null);
        }

        // category
        json.addProperty("category", type.category() == ToiletType.Category.WOOD ? "wood" : "hard");

        // display_name (有 source_block 时自动读取，不显式写出)
        if (sourceBlock == null) {
            var displayNameResult = ComponentSerialization.CODEC.encodeStart(JsonOps.INSTANCE, type.displayName());
            displayNameResult.ifSuccess(displayNameJson -> json.add("display_name", displayNameJson));
        }

        // texture (optional)
        if (type.texture() != null) {
            json.addProperty("texture", type.texture());
        }

        // hardness（木头类不写，运行时自动从 source_block 的 destroyTime 读取）
        if (type.category() != ToiletType.Category.WOOD) {
            json.addProperty("hardness", type.hardness());
        }

        // is_redstone
        json.addProperty("is_redstone", type.isRedstone());

        // is_golden
        json.addProperty("is_golden", type.isGolden());

        // name_key (始终写出，用于自定义名称或默认指向源方块翻译键)
        json.addProperty("name_key", type.nameKey());

        Path outputPath = this.pathProvider.json(ResourceLocation.fromNamespaceAndPath(PoopSky.MOD_ID, type.id()));
        return DataProvider.saveStable(cachedOutput, json, outputPath);
    }

    @Override
    public String getName() {
        return "Toilet Types";
    }
}