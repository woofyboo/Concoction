package net.mcreator.concoction.block;

import com.mojang.serialization.MapCodec;

public class LightGrayWovenCarpetBlock extends AbstractWovenCarpetBlock {
	public static final MapCodec<LightGrayWovenCarpetBlock> CODEC = simpleCodec(LightGrayWovenCarpetBlock::new);

	public LightGrayWovenCarpetBlock() {
		super();
	}

	private LightGrayWovenCarpetBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected MapCodec<LightGrayWovenCarpetBlock> codec() {
		return CODEC;
	}
}
