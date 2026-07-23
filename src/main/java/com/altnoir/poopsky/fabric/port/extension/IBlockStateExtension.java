package com.altnoir.poopsky.fabric.port.extension;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import org.jetbrains.annotations.Nullable;

public interface IBlockStateExtension {
    private BlockState self() {
        return (BlockState) this;
    }

    /**
     * Sensitive version of getSoundType
     *
     * @param level  The level
     * @param pos    The position. Note that the level may not necessarily have {@code state} here!
     * @param entity The entity that is breaking/stepping on/placing/hitting/falling on this block, or null if no entity is in this context
     * @return A SoundType to use
     */
    default SoundType getSoundType(LevelReader level, BlockPos pos, @Nullable Entity entity) {
        return self().getBlock().getSoundType(self(), level, pos, entity);
    }

    /**
     * @return true if the block is sticky block which used for pull or push adjacent blocks (use by piston)
     */
    default boolean isStickyBlock() {
        return self().getBlock().isStickyBlock(self());
    }

    /**
     * Chance that fire will spread and consume this block.
     * 300 being a 100% chance, 0, being a 0% chance.
     *
     * @param level The current level
     * @param pos   Block position in level
     * @param face  The face that the fire is coming from
     * @return A number ranging from 0 to 300 relating used to determine if the block will be consumed by fire
     */
    default int getFlammability(BlockGetter level, BlockPos pos, Direction face) {
        return self().getBlock().getFlammability(self(), level, pos, face);
    }

    /**
     * Called when fire is updating, checks if a block face can catch fire.
     *
     * @param level The current level
     * @param pos   Block position in level
     * @param face  The face that the fire is coming from
     * @return True if the face can be on fire, false otherwise.
     */
    default boolean isFlammable(BlockGetter level, BlockPos pos, Direction face) {
        return self().getBlock().isFlammable(self(), level, pos, face);
    }

    /**
     * Called when fire is updating on a neighbor block.
     * The higher the number returned, the faster fire will spread around this block.
     *
     * @param level The current level
     * @param pos   Block position in level
     * @param face  The face that the fire is coming from
     * @return A number that is used to determine the speed of fire growth around the block
     */
    default int getFireSpreadSpeed(BlockGetter level, BlockPos pos, Direction face) {
        return self().getBlock().getFireSpreadSpeed(self(), level, pos, face);
    }

    /**
     * Determines if this block should set fire and deal fire damage
     * to entities coming into contact with it.
     *
     * @param level The current level
     * @param pos   Block position in level
     * @return True if the block should deal damage
     */
    default boolean isBurning(BlockGetter level, BlockPos pos) {
        return self().getBlock().isBurning(self(), level, pos);
    }

    /**
     * Gets the path type of this block when an entity is pathfinding. When
     * {@code null}, uses vanilla behavior.
     *
     * @param level the level which contains this block
     * @param pos   the position of the block
     * @param mob   the mob currently pathfinding, may be {@code null}
     * @return the path type of this block
     */
    @Nullable
    default PathType getBlockPathType(BlockGetter level, BlockPos pos, @Nullable Mob mob) {
        return self().getBlock().getBlockPathType(self(), level, pos, mob);
    }

    /**
     * If the block is flammable, this is called when it gets lit on fire.
     *
     * @param level   The current level
     * @param pos     Block position in level
     * @param face    The face that the fire is coming from
     * @param igniter The entity that lit the fire
     */
    default void onCaughtFire(Level level, BlockPos pos, @Nullable Direction face, @Nullable LivingEntity igniter) {
        self().getBlock().onCaughtFire(self(), level, pos, face, igniter);
    }

    /**
     * Determines if this block should drop loot when exploded.
     */
    default boolean canDropFromExplosion(BlockGetter level, BlockPos pos, Explosion explosion) {
        return self().getBlock().canDropFromExplosion(self(), level, pos, explosion);
    }

    /**
     * Allows a block to override the standard EntityLivingBase.updateFallState
     * particles, this is a server side method that spawns particles with
     * WorldServer.spawnParticle.
     *
     * @param level             The current server level
     * @param pos               The position of the block.
     * @param state2            The state at the specific world/pos
     * @param entity            The entity that hit landed on the block
     * @param numberOfParticles That vanilla world have spawned
     * @return True to prevent vanilla landing particles from spawning
     */
    default boolean addLandingEffects(ServerLevel level, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles) {
        return self().getBlock().addLandingEffects(self(), level, pos, state2, entity, numberOfParticles);
    }
}
