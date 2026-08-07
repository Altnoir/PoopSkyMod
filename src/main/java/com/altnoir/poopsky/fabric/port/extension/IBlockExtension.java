package com.altnoir.poopsky.fabric.port.extension;

import com.altnoir.poopsky.fabric.port.util.ItemAbilities;
import com.altnoir.poopsky.fabric.port.util.ItemAbility;
import com.altnoir.poopsky.fabric.port.util.ItemUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.pathfinder.PathType;
import org.jetbrains.annotations.Nullable;

public interface IBlockExtension {
    private Block self() {
        return (Block) this;
    }

    /**
     * Sensitive version of getSoundType
     *
     * @param state  The state
     * @param level  The level
     * @param pos    The position. Note that the level may not necessarily have {@code state} here!
     * @param entity The entity that is breaking/stepping on/placing/hitting/falling on this block, or null if no entity is in this context
     * @return A SoundType to use
     */
    default SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
        return state.getSoundType();
    }

    /**
     * Called when A user uses the creative pick block button on this block
     *
     * @return A ItemStack to add to the player's inventory, empty itemstack if nothing should be added.
     */
    default ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, Player player) {
        return self().getCloneItemStack(level, pos, state);
    }

    /**
     * @param state The state
     * @return true if the block is sticky block which used for pull or push adjacent blocks (use by piston)
     */
    default boolean isStickyBlock(BlockState state) {
        return state.getBlock() == Blocks.SLIME_BLOCK || state.getBlock() == Blocks.HONEY_BLOCK;
    }

    default boolean canStickTo(BlockState state, BlockState other) {
        return !(state.is(Blocks.HONEY_BLOCK) && other.is(Blocks.SLIME_BLOCK))
                && !(state.is(Blocks.SLIME_BLOCK) && other.is(Blocks.HONEY_BLOCK));
    }

    /**
     * Chance that fire will spread and consume this block.
     * 300 being a 100% chance, 0, being a 0% chance.
     *
     * @param state     The current state
     * @param level     The current level
     * @param pos       Block position in level
     * @param direction The direction that the fire is coming from
     * @return A number ranging from 0 to 300 relating used to determine if the block will be consumed by fire
     */
    default int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return ((FireBlock) Blocks.FIRE).getBurnOdds(state);
    }

    /**
     * Called when fire is updating, checks if a block face can catch fire.
     *
     *
     * @param state     The current state
     * @param level     The current level
     * @param pos       Block position in level
     * @param direction The direction that the fire is coming from
     * @return True if the face can be on fire, false otherwise.
     */
    default boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.getFlammability(level, pos, direction) > 0;
    }

    /**
     * Called when fire is updating on a neighbor block.
     * The higher the number returned, the faster fire will spread around this block.
     *
     * @param state     The current state
     * @param level     The current level
     * @param pos       Block position in level
     * @param direction The direction that the fire is coming from
     * @return A number that is used to determine the speed of fire growth around the block
     */
    default int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return ((FireBlock) Blocks.FIRE).getIgniteOdds(state);
    }

    /**
     * Returns the {@link MapColor} shown on the map.
     *
     * @param state        The state of this block
     * @param level        The level this block is in
     * @param pos          The blocks position in the level
     * @param defaultColor The {@code MapColor} configured for the given {@code BlockState} in the {@link net.minecraft.world.level.block.state.BlockBehaviour.Properties}
     */
    default MapColor getMapColor(BlockState state, BlockGetter level, BlockPos pos, MapColor defaultColor) {
        return defaultColor;
    }

    /**
     * Gets the path type of this block when an entity is pathfinding. When
     * {@code null}, uses vanilla behavior.
     *
     * @param state the state of the block
     * @param level the level which contains this block
     * @param pos   the position of the block
     * @param mob   the mob currently pathfinding, may be {@code null}
     * @return the path type of this block
     */
    @Nullable
    default PathType getBlockPathType(BlockState state, BlockGetter level, BlockPos pos, @Nullable Mob mob) {
        return state.getBlock() == Blocks.LAVA ? PathType.LAVA : state.isBurning(level, pos) ? PathType.DAMAGE_FIRE : null;
    }

    /**
     * Determines if this block should set fire and deal fire damage
     * to entities coming into contact with it.
     *
     * @param level The current level
     * @param pos   Block position in level
     * @return True if the block should deal damage
     */
    default boolean isBurning(BlockState state, BlockGetter level, BlockPos pos) {
        return this == Blocks.FIRE || this == Blocks.LAVA;
    }

    /**
     * If the block is flammable, this is called when it gets lit on fire.
     *
     * @param state     The current state
     * @param level     The current level
     * @param pos       Block position in level
     * @param direction The direction that the fire is coming from
     * @param igniter   The entity that lit the fire
     */
    default void onCaughtFire(BlockState state, Level level, BlockPos pos, @Nullable Direction direction, @Nullable LivingEntity igniter) {}

    /**
     * Determines if this block should drop loot when exploded.
     */
    default boolean canDropFromExplosion(BlockState state, BlockGetter level, BlockPos pos, Explosion explosion) {
        return state.getBlock().dropFromExplosion(explosion);
    }

    /**
     * Returns the state that this block should transform into when right-clicked by a tool.
     * For example: Used to determine if {@link ItemAbilities#AXE_STRIP an axe can strip},
     * {@link ItemAbilities#SHOVEL_FLATTEN a shovel can path}, or {@link ItemAbilities#HOE_TILL a hoe can till}.
     * Returns {@code null} if nothing should happen.
     *
     * @param state       The current state
     * @param context     The use on context that the action was performed in
     * @param itemAbility The action being performed by the tool
     * @param simulate    If {@code true}, no actions that modify the world in any way should be performed. If {@code false}, the world may be modified.
     * @return The resulting state after the action has been performed
     */
    @Nullable
    default BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
        ItemStack itemStack = context.getItemInHand();
        if (!itemStack.canPerformAction(itemAbility))
            return null;


        if (ItemAbilities.AXE_STRIP == itemAbility) {
            return ItemUtils.getAxeStrippingState(state);
        } else if (ItemAbilities.AXE_SCRAPE == itemAbility) {
            return WeatheringCopper.getPrevious(state).orElse(null);
        } else /*if (ItemAbilities.AXE_WAX_OFF == itemAbility) {
            Block waxOffBlock = DataMapHooks.getBlockUnwaxed(state.getBlock());
            return Optional.ofNullable(waxOffBlock).map(block -> block.withPropertiesOf(state)).orElse(null);
        } else*/ if (ItemAbilities.SHOVEL_FLATTEN == itemAbility) {
            return ItemUtils.getShovelPathingState(state);
        } else if (ItemAbilities.HOE_TILL == itemAbility) {
            // Logic copied from HoeItem#TILLABLES; needs to be kept in sync during updating
            Block block = state.getBlock();
            if (block == Blocks.ROOTED_DIRT) {
                if (!simulate && !context.getLevel().isClientSide) {
                    Block.popResourceFromFace(context.getLevel(), context.getClickedPos(), context.getClickedFace(), new ItemStack(Items.HANGING_ROOTS));
                }
                return Blocks.DIRT.defaultBlockState();
            } else if ((block == Blocks.GRASS_BLOCK || block == Blocks.DIRT_PATH || block == Blocks.DIRT || block == Blocks.COARSE_DIRT) &&
                    context.getLevel().getBlockState(context.getClickedPos().above()).isAir()) {
                return block == Blocks.COARSE_DIRT ? Blocks.DIRT.defaultBlockState() : Blocks.FARMLAND.defaultBlockState();
            }
        } else if (ItemAbilities.SHEARS_TRIM == itemAbility) {
            if (state.getBlock() instanceof GrowingPlantHeadBlock growingPlant && !growingPlant.isMaxAge(state)) {
                if (!simulate)
                    context.getLevel().playSound(context.getPlayer(), context.getClickedPos(), SoundEvents.GROWING_PLANT_CROP, SoundSource.BLOCKS, 1.0F, 1.0F);
                return growingPlant.getMaxAgeState(state);
            }
        } else if (ItemAbilities.SHOVEL_DOUSE == itemAbility) {
            if (state.getBlock() instanceof CampfireBlock && state.getValue(CampfireBlock.LIT)) {
                if (!simulate) {
                    CampfireBlock.dowse(context.getPlayer(), context.getLevel(), context.getClickedPos(), state);
                }
                return state.setValue(CampfireBlock.LIT, Boolean.valueOf(false));
            }
        } else if (ItemAbilities.FIRESTARTER_LIGHT == itemAbility) {
            if (CampfireBlock.canLight(state) || CandleBlock.canLight(state) || CandleCakeBlock.canLight(state)) {
                return state.setValue(BlockStateProperties.LIT, Boolean.valueOf(true));
            }
        }

        return null;
    }

    /**
     * Allows a block to override the standard EntityLivingBase.updateFallState
     * particles, this is a server side method that spawns particles with
     * WorldServer.spawnParticle.
     *
     * @param level             The current server level
     * @param pos               The position of the block.
     * @param state2            The state at the specific level/pos
     * @param entity            The entity that hit landed on the block
     * @param numberOfParticles That vanilla level have spawned
     * @return True to prevent vanilla landing particles from spawning
     */
    default boolean addLandingEffects(BlockState state1, ServerLevel level, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles) {
        return false;
    }
}
