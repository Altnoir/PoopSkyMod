package com.altnoir.poopsky.compat.maid;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.compat.maid.toilet.ToiletSensor;
import com.altnoir.poopsky.compat.maid.toilet.UseToiletTask;
import com.github.tartaricacid.touhoulittlemaid.api.ILittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.LittleMaidExtension;
import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.ExtraMaidBrainManager;
import com.github.tartaricacid.touhoulittlemaid.entity.task.TaskManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Optional;

@LittleMaidExtension
public class MaidPlugin implements ILittleMaid {
    public static DeferredHolder<MemoryModuleType<?>, MemoryModuleType<BlockPos>> TOILET_MEMORY;
    public static DeferredHolder<SensorType<?>, SensorType<ToiletSensor>> TOILET_SENSOR;

    public static void registry(IEventBus bus) {
        var MEMORY = DeferredRegister.create(BuiltInRegistries.MEMORY_MODULE_TYPE, PoopSky.MOD_ID);
        var SENSOR = DeferredRegister.create(BuiltInRegistries.SENSOR_TYPE, PoopSky.MOD_ID);

        TOILET_MEMORY = MEMORY.register("toilet_memory", resourceLocation -> new MemoryModuleType<>(Optional.of(BlockPos.CODEC)));
        TOILET_SENSOR = SENSOR.register("toilet_sensor", resourceLocation -> new SensorType<>(ToiletSensor::new));
        SENSOR.register(bus);
        MEMORY.register(bus);
    }

    @Override
    public void addExtraMaidBrain(ExtraMaidBrainManager manager) {
        manager.addExtraMaidBrain(new ExtraMaidBrain());
    }

    @Override
    public void addMaidTask(TaskManager manager) {
        manager.add(new UseToiletTask());
    }
}
