package net.mcreator.concoction.block;

import com.mojang.serialization.MapCodec;

public class BlueWovenCarpetBlock extends AbstractWovenCarpetBlock {
	public static final MapCodec<BlueWovenCarpetBlock> CODEC = simpleCodec(BlueWovenCarpetBlock::new);

	public BlueWovenCarpetBlock() {
		super();
	}

	private BlueWovenCarpetBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected MapCodec<BlueWovenCarpetBlock> codec() {
		return CODEC;
	}
}
