package com.altnoir.poopsky.content.block.p;

import com.altnoir.poopsky.content.block.PigstyPart;
import com.altnoir.poopsky.content.block.entity.PigstyBlockEntity;
import com.altnoir.poopsky.impl.util.ToiletUtil;
import com.altnoir.poopsky.init.PoComponents;
import com.altnoir.poopsky.init.PoEffects;
import com.altnoir.poopsky.init.PoItems;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PigstyBlock extends HorizontalDirectionalBlock implements EntityBlock, SimpleWaterloggedBlock {
    public static final MapCodec<PigstyBlock> CODEC = simpleCodec(PigstyBlock::new);
    public static final EnumProperty<PigstyPart> PART = EnumProperty.create("part", PigstyPart.class);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final String PIGSTY_DATA = "poopsky_pigsty";
    private static final int PRODUCTION_INTERVAL = 20;
    private static final float HEAD_TURN_SPEED = 20.0F;

    public PigstyBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.SOUTH)
                .setValue(PART, PigstyPart.BACK)
                .setValue(WATERLOGGED, false));
    }

    public static BlockPos mainPos(BlockState state, BlockPos pos) {
        return state.getValue(PART) == PigstyPart.BACK ? pos : pos.relative(state.getValue(FACING));
    }

    private static void confine(Pig pig, BlockPos pos, Direction facing) {
        CompoundTag data = new CompoundTag();
        data.putLong("pos", pos.asLong());
        data.putFloat("head_yaw", facing.getOpposite().toYRot());
        data.putDouble("x", pos.getX() + 0.5 - facing.getStepX() * 0.5);
        data.putDouble("z", pos.getZ() + 0.5 - facing.getStepZ() * 0.5);
        data.putBoolean("no_ai", pig.isNoAi());
        data.putBoolean("no_gravity", pig.isNoGravity());
        data.putLong("last_production", pig.level().getGameTime());
        pig.getPersistentData().put(PIGSTY_DATA, data);
        pig.setNoAi(true);
        pig.setNoGravity(true);
        fixBodyRotation(pig, facing.getOpposite().toYRot());
        pig.setYHeadRot(facing.getOpposite().toYRot());
        pig.setPos(data.getDouble("x"), pos.getY() + 0.0625, data.getDouble("z"));
    }

    public static boolean isConfined(Pig pig) {
        if (!pig.level().isClientSide) return pig.getPersistentData().contains(PIGSTY_DATA);
        if (!pig.isNoAi() || !pig.isNoGravity()) return false;
        BlockPos pos = pig.blockPosition();
        BlockState state = pig.level().getBlockState(pos);
        if (!(state.getBlock() instanceof PigstyBlock)) return false;
        BlockPos main = mainPos(state, pos);
        Direction facing = state.getValue(FACING);
        double x = main.getX() + 0.5 - facing.getStepX() * 0.5;
        double z = main.getZ() + 0.5 - facing.getStepZ() * 0.5;
        return Math.abs(pig.getX() - x) < 0.1 && Math.abs(pig.getZ() - z) < 0.1
                && Math.abs(pig.getY() - (main.getY() + 0.0625)) < 0.1;
    }

    public static void tickPig(Pig pig) {
        if (pig.level().isClientSide) {
            fixClientBodyRotation(pig);
            return;
        }
        if (!isConfined(pig)) return;
        Level level = pig.level();
        CompoundTag data = pig.getPersistentData().getCompound(PIGSTY_DATA);
        BlockPos pos = BlockPos.of(data.getLong("pos"));
        pig.setDeltaMovement(0, 0, 0);
        pig.setPos(data.getDouble("x"), pos.getY() + 0.0625, data.getDouble("z"));
        if (!level.isLoaded(pos)) return;
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof PigstyBlock) || state.getValue(PART) != PigstyPart.BACK) {
            release(pig);
            return;
        }
        if (!pig.isAlive()) {
            release(pig);
            return;
        }
        pig.setNoAi(true);
        pig.setNoGravity(true);
        fixBodyRotation(pig, state.getValue(FACING).getOpposite().toYRot());
        float targetHeadYaw = pig.getYRot();
        float targetPitch = 0.0F;
        Player player = level.getNearestPlayer(pig, 8.0);
        if (player != null) {
            double dx = player.getX() - pig.getX();
            double dz = player.getZ() - pig.getZ();
            double dy = player.getEyeY() - pig.getEyeY();
            float yaw = (float) (Mth.atan2(dz, dx) * 180.0 / Math.PI) - 90.0F;
            float pitch = (float) -(Mth.atan2(dy, Math.sqrt(dx * dx + dz * dz)) * 180.0 / Math.PI);
            float headOffset = Mth.wrapDegrees(yaw - pig.getYRot());
            if (Math.abs(headOffset) <= pig.getMaxHeadYRot()) {
                targetHeadYaw = pig.getYRot() + headOffset;
                targetPitch = Mth.clamp(pitch, -40.0F, 40.0F);
            }
        }
        float headYaw = data.contains("head_yaw") ? data.getFloat("head_yaw") : pig.getYRot();
        float headPitch = data.getFloat("head_pitch");
        headYaw = approachAngle(headYaw, targetHeadYaw, HEAD_TURN_SPEED);
        headPitch = approachAngle(headPitch, targetPitch, HEAD_TURN_SPEED);
        data.putFloat("head_yaw", headYaw);
        data.putFloat("head_pitch", headPitch);
        pig.setYHeadRot(headYaw);
        pig.setXRot(headPitch);
        if (!data.contains("last_production")) {
            data.putLong("last_production", level.getGameTime());
        }
        int interval = pig.hasEffect(PoEffects.FECAL_INCONTINENCE) ? 1 : PRODUCTION_INTERVAL;
        if (level.getGameTime() - data.getLong("last_production") >= interval
                && level instanceof ServerLevel serverLevel && produce(serverLevel, pos, pig)) {
            data.putLong("last_production", level.getGameTime());
        }
    }

    private static void fixBodyRotation(Pig pig, float yaw) {
        pig.setYRot(yaw);
        pig.yRotO = yaw;
        pig.setYBodyRot(yaw);
        pig.yBodyRotO = yaw;
    }

    private static float approachAngle(float current, float target, float step) {
        return current + Mth.clamp(Mth.wrapDegrees(target - current), -step, step);
    }

    private static boolean produce(ServerLevel level, BlockPos pos, Pig pig) {
        PigstyBlockEntity[] pair = level.getBlockEntity(pos) instanceof PigstyBlockEntity pigsty ? pigsty.getPair() : null;
        if (pair == null) return false;
        boolean spicy = pig.hasEffect(PoEffects.INTESTINAL_SPASM);
        if (!PigstyBlockEntity.insertProduct(pair, spicy ? PoItems.CHILI_POOP.get() : PoItems.POOP.get())) {
            ToiletUtil.onPoop(level, pig, spicy, false, 0.05F, 0.5F, true);
        } else {
            ToiletUtil.playPoopEffects(level, pig.getX(), pig.getY() + 0.05, pig.getZ(), level.random.nextFloat() + 0.5F);
        }
        return true;
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART, WATERLOGGED);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PigstyBlockEntity(pos, state);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection();
        BlockPos other = context.getClickedPos().relative(facing);
        if (!context.getLevel().getWorldBorder().isWithinBounds(other)
                || !context.getLevel().getBlockState(other).canBeReplaced(context)) return null;
        boolean waterlogged = context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER;
        return defaultBlockState()
                .setValue(FACING, facing)
                .setValue(PART, PigstyPart.FRONT)
                .setValue(WATERLOGGED, waterlogged);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighbor,
                                     LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        Direction partner = state.getValue(PART) == PigstyPart.BACK
                ? state.getValue(FACING).getOpposite()
                : state.getValue(FACING);
        if (direction == partner) {
            return neighbor.is(this) && neighbor.getValue(PART) != state.getValue(PART)
                    && neighbor.getValue(FACING) == state.getValue(FACING)
                    ? state
                    : state.getFluidState().createLegacyBlock();
        }
        return super.updateShape(state, direction, neighbor, level, pos, neighborPos);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        Direction end = state.getValue(PART) == PigstyPart.FRONT ? facing.getOpposite() : facing;
        VoxelShape shape = Block.box(0, 0, 0, 16, 1, 16);
        if (facing.getAxis() == Direction.Axis.Z) {
            shape = Shapes.or(shape, Block.box(0, 1, 0, 2, 16, 16), Block.box(14, 1, 0, 16, 16, 16));
        } else {
            shape = Shapes.or(shape, Block.box(0, 1, 0, 16, 16, 2), Block.box(0, 1, 14, 16, 16, 16));
        }
        return Shapes.or(shape, switch (end) {
            case NORTH -> Block.box(0, 1, 0, 16, 16, 2);
            case SOUTH -> Block.box(0, 1, 14, 16, 16, 16);
            case WEST -> Block.box(0, 1, 0, 2, 16, 16);
            default -> Block.box(14, 1, 0, 16, 16, 16);
        });
    }

    @Override
    protected VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return Shapes.block();
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return stack.isEmpty()
                ? ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
                : ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
    }

    private static void release(Pig pig) {
        CompoundTag data = pig.getPersistentData().getCompound(PIGSTY_DATA);
        pig.setNoAi(data.getBoolean("no_ai"));
        pig.setNoGravity(data.getBoolean("no_gravity"));
        pig.getPersistentData().remove(PIGSTY_DATA);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof PigstyBlockEntity pigsty) {
            IItemHandler output = pigsty.getOutputHandler();
            for (int slot = 0; slot < output.getSlots(); slot++) {
                ItemStack stack = output.extractItem(slot, Integer.MAX_VALUE, false);
                if (stack.isEmpty()) continue;
                player.getInventory().placeItemBackInInventory(stack);
                break;
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        BlockPos back = pos.relative(state.getValue(FACING));
        BlockState backState = state.setValue(PART, PigstyPart.BACK)
                .setValue(WATERLOGGED, level.getFluidState(back).getType() == Fluids.WATER);
        level.setBlock(back, backState, 3);
        CustomData data = stack.get(PoComponents.PIGSTY_PIG);
        if (level.isClientSide || data == null) return;

        Entity entity = EntityType.loadEntityRecursive(data.copyTag(), level, loaded -> loaded);
        if (entity instanceof Pig pig && pig.isAlive()) {
            if (placer instanceof Player player && player.isCreative()) {
                pig.setUUID(UUID.randomUUID());
            }
            Direction facing = state.getValue(FACING);
            confine(pig, back, facing);
            if (level.addFreshEntity(pig)) {
                return;
            }
        }
        level.removeBlock(pos, false);
        if (!(placer instanceof Player player) || !player.isCreative()) {
            popResource(level, pos, stack.copyWithCount(1));
        }
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && player.isCreative() && state.getValue(PART) == PigstyPart.BACK) {
            Direction facing = state.getValue(FACING);
            BlockPos otherPos = pos.relative(facing.getOpposite());
            BlockState other = level.getBlockState(otherPos);
            if (other.is(this) && other.getValue(PART) == PigstyPart.FRONT && other.getValue(FACING) == facing) {
                level.setBlock(otherPos, other.getFluidState().createLegacyBlock(), 35);
                level.levelEvent(player, 2001, otherPos, Block.getId(other));
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    private static void fixClientBodyRotation(Pig pig) {
        if (!isConfined(pig)) return;
        BlockState state = pig.level().getBlockState(pig.blockPosition());
        Direction facing = state.getValue(FACING);
        fixBodyRotation(pig, facing.getOpposite().toYRot());
    }

    private static boolean belongsTo(Pig pig, BlockPos pos) {
        return pig.isAlive() && isConfined(pig)
                && BlockPos.of(pig.getPersistentData().getCompound(PIGSTY_DATA).getLong("pos")).equals(pos);
    }

    private static java.util.List<Pig> findPigs(Level level, BlockPos pos) {
        return level.getEntitiesOfClass(Pig.class, new AABB(pos).inflate(2.0), pig -> belongsTo(pig, pos));
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!level.isClientSide && !state.is(newState.getBlock())) {
            BlockPos main = mainPos(state, pos);
            for (Pig pig : findPigs(level, main)) release(pig);
            if (level.getBlockEntity(pos) instanceof PigstyBlockEntity pigsty) pigsty.dropContents(level, pos);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }
}
