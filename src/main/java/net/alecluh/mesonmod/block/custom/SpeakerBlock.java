package net.alecluh.mesonmod.block.custom;

import com.mojang.serialization.MapCodec;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.Direction;

public class SpeakerBlock extends HorizontalFacingBlock {

    public static final MapCodec<SpeakerBlock> CODEC = Block.createCodec(SpeakerBlock::new);

    public SpeakerBlock(Settings settings) {
		super(settings);
		setDefaultState(getDefaultState().with(FACING, Direction.NORTH));
	}

    @Override
	protected MapCodec<? extends SpeakerBlock> getCodec() {
		return CODEC;
	}
 
	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

    @Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return super.getPlacementState(ctx).with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
	}

}
