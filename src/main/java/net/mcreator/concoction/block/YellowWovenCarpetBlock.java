package net.mcreator.concoction.block;

import com.mojang.serialization.MapCodec;

public class YellowWovenCarpetBlock extends AbstractWovenCarpetBlock {
	public static final MapCodec<YellowWovenCarpetBlock> CODEC = simpleCodec(YellowWovenCarpetBlock::new);

	public YellowWovenCarpetBlock() {
		super();
	}

	private YellowWovenCarpetBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected MapCodec<YellowWovenCarpetBlock> codec() {
		return CODEC;
	}
}
