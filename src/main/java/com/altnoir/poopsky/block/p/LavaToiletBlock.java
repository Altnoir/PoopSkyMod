package com.altnoir.poopsky.block.p;

import com.altnoir.poopsky.block.ToiletType;
import com.altnoir.poopsky.block.abs.AbstractToiletBlock;
import com.altnoir.poopsky.block.entity.ToiletBlockEntity;
import com.altnoir.poopsky.init.PToiletTypes;
import com.altnoir.poopsky.item.p.ToiletBlockItem;
import com.altnoir.poopsky.util.toiletUtil;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LavaToiletBlock extends BaseToiletLavaBlock {
    public static final MapCodec<LavaToiletBlock> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ToiletType.CODEC.fieldOf("default_toilet_type").forGetter(b -> b.defaultToiletType),
                    propertiesCodec()
            ).apply(instance, LavaToiletBlock::new)
    );

    public enum ToiletMode implements StringRepresentable {
        DEFAULT("default"),
        REDSTONE("redstone");

        private final String name;

        ToiletMode(String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return name;
        }
    }

    public static final EnumProperty<ToiletMode> TOILET_MODE = EnumProperty.create("toilet_mode", ToiletMode.class);
    private final ToiletType defaultToiletType;

    public LavaToiletBlock(ToiletType defaultToiletType, Properties properties) {
        super(properties);
        this.defaultToiletType = defaultToiletType;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(CONNECTION, AbstractToiletBlock.ToiletState.DEFAULT)
                .setValue(LAVA, false)
                .setValue(TOILET_MODE, ToiletMode.DEFAULT));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(TOILET_MODE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(LAVA, false);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moved) {
        super.onPlace(state, level, pos, oldState, moved);
        if (level.getBlockEntity(pos) instanceof ToiletBlockEntity be && be.getToiletType() == null) {
            be.setToiletType(defaultToiletType);
        }
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        boolean isGolden = toiletUtil.isGoldenToilet(level, pos);
        toiletUtil.lavaToiletStepOn(level, pos, state, entity, isGolden);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.getItem() instanceof BlockItem blockItem) {
            ToiletType newType = ToiletType.bySourceBlock(blockItem.getBlock());
            if (newType != null && newType.category() == ToiletType.Category.HARD) {
                if (!state.getValue(LAVA)) {
                    if (level.getBlockEntity(pos) instanceof ToiletBlockEntity be) {
                        ToiletType currentType = be.getToiletType();
                        if (currentType != newType) {
                            be.setToiletType(newType);

                            SoundType sound = blockItem.getBlock().defaultBlockState().getSoundType();
                            level.playSound(null, pos, sound.getPlaceSound(), SoundSource.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);

                            stack.consume(1, player);
                            if (!player.getAbilities().instabuild) {
                                Block oldBlock = currentType.sourceBlock();
                                if (oldBlock != null) {
                                    ItemEntity itemEntity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.72, pos.getZ() + 0.5, new ItemStack(oldBlock));
                                    itemEntity.setDefaultPickUpDelay();
                                    level.addFreshEntity(itemEntity);
                                }
                            }

                            return ItemInteractionResult.sidedSuccess(level.isClientSide);
                        }
                    }
                }
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        ToiletType type = getToiletTypeFromBE(level, pos);
        return withType(this, type != null ? type : defaultToiletType);
    }

    public static ItemStack withType(Block block, ToiletType toiletType) {
        return ToiletBlockItem.withType(block, toiletType);
    }

    public BlockState applyVariant(BlockState state, ToiletType toiletType) {
        if (toiletType != null && toiletType.category() == ToiletType.Category.HARD) {
            ToiletMode mode = toiletType == PToiletTypes.REDSTONE ? ToiletMode.REDSTONE : ToiletMode.DEFAULT;
            return state.setValue(TOILET_MODE, mode);
        }
        return state;
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return state.getValue(TOILET_MODE) == ToiletMode.REDSTONE;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.getValue(TOILET_MODE) == ToiletMode.REDSTONE ? 15 : 0;
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        ToiletType type = getToiletTypeFromBE(level, pos);
        if (type != null && (type == PToiletTypes.NETHERITE || type == PToiletTypes.OBSIDIAN || type == PToiletTypes.CRYING_OBSIDIAN)) {
            float hardness = 50.0F;
            int i = EventHooks.doPlayerHarvestCheck(player, state, level, pos) ? 30 : 100;
            return player.getDigSpeed(state, pos) / hardness / (float) i;
        }
        return super.getDestroyProgress(state, player, level, pos);
    }

    @Override
    public MapColor getMapColor(BlockState state, BlockGetter level, BlockPos pos, MapColor defaultColor) {
        return MapColor.STONE;
    }

    @Override
    public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
        ToiletType type = getToiletTypeFromBE(level, pos);
        if (type != null) {
            Block sourceBlock = type.sourceBlock();
            if (sourceBlock != null) {
                return sourceBlock.defaultBlockState().getSoundType();
            }
        }
        return super.getSoundType(state, level, pos, entity);
    }

    @Nullable
    private ToiletType getToiletTypeFromBE(BlockGetter level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof ToiletBlockEntity be) {
            return be.getToiletType();
        }
        return null;
    }
}