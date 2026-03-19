package net.mcreator.concoction.block;

import com.mojang.serialization.MapCodec;

public class GrayWovenCarpetBlock extends AbstractWovenCarpetBlock {
	public static final MapCodec<GrayWovenCarpetBlock> CODEC = simpleCodec(GrayWovenCarpetBlock::new);

	public GrayWovenCarpetBlock() {
		super();
	}

	private GrayWovenCarpetBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected MapCodec<GrayWovenCarpetBlock> codec() {
		return CODEC;
	}
}
