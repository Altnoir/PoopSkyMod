package com.altnoir.poopsky.compat.maid;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.compat.maid.toilet.DefecateTask;
import com.altnoir.poopsky.compat.maid.toilet.ToiletSensor;
import com.github.tartaricacid.touhoulittlemaid.api.ILittleMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.ExtraMaidBrainManager;
import com.github.tartaricacid.touhoulittlemaid.entity.task.TaskManager;
import com.tterrag.registrate.fabric.registry.DeferredHolder;
import com.tterrag.registrate.fabric.registry.DeferredRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;

import java.util.Optional;

public class MaidPlugin implements ILittleMaid {
    public static DeferredHolder<MemoryModuleType<?>, MemoryModuleType<BlockPos>> TOILET_MEMORY;
    public static DeferredHolder<SensorType<?>, SensorType<ToiletSensor>> TOILET_SENSOR;

    public static void registry() {
        var MEMORY = DeferredRegister.create(BuiltInRegistries.MEMORY_MODULE_TYPE, PoopSky.MOD_ID);
        var SENSOR = DeferredRegister.create(BuiltInRegistries.SENSOR_TYPE, PoopSky.MOD_ID);

        TOILET_MEMORY = MEMORY.register("toilet_memory", resourceLocation -> new MemoryModuleType<>(Optional.of(BlockPos.CODEC)));
        TOILET_SENSOR = SENSOR.register("toilet_sensor", resourceLocation -> new SensorType<>(ToiletSensor::new));
        SENSOR.register();
        MEMORY.register();
    }

    @Override
    public void addExtraMaidBrain(ExtraMaidBrainManager manager) {
        manager.addExtraMaidBrain(new ExtraMaidBrain());
    }

    @Override
    public void addMaidTask(TaskManager manager) {
        manager.add(new DefecateTask());
    }
}
