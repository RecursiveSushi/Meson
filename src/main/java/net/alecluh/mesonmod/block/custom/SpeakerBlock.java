package net.alecluh.mesonmod.block.custom;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.MapCodec;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
//import net.minecraft.block.NoteBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
//import net.minecraft.block.RedstoneWireBlock;

public class SpeakerBlock extends HorizontalFacingBlock {

    public static final MapCodec<SpeakerBlock> CODEC = Block.createCodec(SpeakerBlock::new);
	public static final EnumProperty<NoteBlockInstrument> INSTRUMENT = Properties.INSTRUMENT;
	public static final BooleanProperty POWERED = Properties.POWERED;
	public static final IntProperty POWER = Properties.POWER;
	

	@Override
	protected MapCodec<? extends SpeakerBlock> getCodec() {
		return CODEC;
	}

    public SpeakerBlock(Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateManager
				.getDefaultState()
				.with(FACING, Direction.NORTH)
				.with(INSTRUMENT, NoteBlockInstrument.HARP)
				.with(POWERED, Boolean.valueOf(false))
				.with(POWER, Integer.valueOf(0))
				);
	}
 
	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, INSTRUMENT, POWERED, POWER);
	}

    @Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getStateWithInstrument(ctx.getWorld(), ctx.getBlockPos(), this.getDefaultState()).with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
	}
	

	private BlockState getStateWithInstrument(WorldAccess world, BlockPos pos, BlockState state) {
		NoteBlockInstrument noteBlockInstrument = world.getBlockState(pos.up()).getInstrument();
		if (noteBlockInstrument.isNotBaseBlock()) {
			return state.with(INSTRUMENT, noteBlockInstrument);
		} else {
			NoteBlockInstrument noteBlockInstrument2 = world.getBlockState(pos.offset(state.get(FACING).getOpposite())).getInstrument();
			NoteBlockInstrument noteBlockInstrument3 = noteBlockInstrument2.isNotBaseBlock() ? NoteBlockInstrument.HARP : noteBlockInstrument2;
			return state.with(INSTRUMENT, noteBlockInstrument3);
		}
	}

	@Override
	protected BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		boolean bl = direction.getAxis() == Direction.Axis.Y;
		return bl ? this.getStateWithInstrument(world, pos, state) : super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
		boolean bl = world.isReceivingRedstonePower(pos);
		if (bl != (Boolean)state.get(POWERED)) {
			if (bl) {
				this.playNote(null, state, world, pos);
			}

			world.setBlockState(pos, state.with(POWERED, Boolean.valueOf(bl)), Block.NOTIFY_ALL);
		}
	}

	private void playNote(@Nullable Entity entity, BlockState state, World world, BlockPos pos) {
		if (((NoteBlockInstrument)state.get(INSTRUMENT)).isNotBaseBlock() || world.getBlockState(pos.offset(state.get(FACING))).isAir()) {
			world.addSyncedBlockEvent(pos, this, 0, 0);
			world.emitGameEvent(entity, GameEvent.NOTE_BLOCK_PLAY, pos);
		}
	}

	public static float getNotePitch(int note) {
		return (float)Math.pow(2.0, (double)(note - 12) / 12.0);
	}

	@Override
	protected boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
		NoteBlockInstrument noteBlockInstrument = state.get(INSTRUMENT);
		float f;
		if (noteBlockInstrument.canBePitched()) {
			int i = 10 /*(Integer)state.get(NOTE)*/;
			f = getNotePitch(i);
			Direction facing = state.get(FACING);
			world.addParticle(ParticleTypes.NOTE, pos.getX() + 0.5 + facing.getOffsetX() * 0.725, pos.getY() + 0.5, pos.getZ() + 0.5 + facing.getOffsetZ() * 0.725, 0.0, 0.0, 0.0);
			//(double)i / 24.0
		} else {
			f = 1.0F;
		}


		RegistryEntry<SoundEvent> registryEntry;
		if (noteBlockInstrument.hasCustomSound()) {
			Identifier identifier = this.getCustomSound(world, pos);
			if (identifier == null) {
				return false;
			}

			registryEntry = RegistryEntry.of(SoundEvent.of(identifier));
		} else {
			registryEntry = noteBlockInstrument.getSound();
		}

		//Change sound to play in front of speaker instead of atop it, also figure out where the condition is that if block is above to block sound, and change that to in front of speaker
		world.playSound(
			null, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, registryEntry, SoundCategory.RECORDS, 3.0F, f, world.random.nextLong()
		);
		return true;
	}

	@Nullable
	private Identifier getCustomSound(World world, BlockPos pos) {
		return world.getBlockEntity(pos.up()) instanceof SkullBlockEntity skullBlockEntity ? skullBlockEntity.getNoteBlockSound() : null;
	}
}