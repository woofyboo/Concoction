package net.mcreator.concoction.block;

import com.mojang.serialization.MapCodec;

public class LimeWovenCarpetBlock extends AbstractWovenCarpetBlock {
	public static final MapCodec<LimeWovenCarpetBlock> CODEC = simpleCodec(LimeWovenCarpetBlock::new);

	public LimeWovenCarpetBlock() {
		super();
	}

	private LimeWovenCarpetBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected MapCodec<LimeWovenCarpetBlock> codec() {
		return CODEC;
	}
}
