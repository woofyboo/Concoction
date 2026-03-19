package net.mcreator.concoction.block;

import com.mojang.serialization.MapCodec;

public class LightBlueWovenCarpetBlock extends AbstractWovenCarpetBlock {
	public static final MapCodec<LightBlueWovenCarpetBlock> CODEC = simpleCodec(LightBlueWovenCarpetBlock::new);

	public LightBlueWovenCarpetBlock() {
		super();
	}

	private LightBlueWovenCarpetBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected MapCodec<LightBlueWovenCarpetBlock> codec() {
		return CODEC;
	}
}
