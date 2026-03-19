package net.mcreator.concoction.block;

import com.mojang.serialization.MapCodec;

public class GreenWovenCarpetBlock extends AbstractWovenCarpetBlock {
	public static final MapCodec<GreenWovenCarpetBlock> CODEC = simpleCodec(GreenWovenCarpetBlock::new);

	public GreenWovenCarpetBlock() {
		super();
	}

	private GreenWovenCarpetBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected MapCodec<GreenWovenCarpetBlock> codec() {
		return CODEC;
	}
}
