package com.altnoir.poopsky.compat.maid.toilet;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.init.PoItems;
import com.github.tartaricacid.touhoulittlemaid.api.task.IMaidTask;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DefecateTask implements IMaidTask {
    public static final Identifier UID = PoopSky.loc("defecate");

    @Override
    public Identifier getUid() {
        return UID;
    }

    @Override
    public ItemStack getIcon() {
        return PoItems.POOP.asStack();
    }

    @Override
    public @Nullable SoundEvent getAmbientSound(EntityMaid entityMaid) {
        return null;
    }

    @Override
    public List<Pair<Integer, BehaviorControl<? super EntityMaid>>> createBrainTasks(EntityMaid entityMaid) {
        return Lists.newArrayList(new Pair<>(0, new DefecateBehavior()));
    }

    @Override
    public boolean workPointTask(EntityMaid entityMaid) {
        return true;
    }
}