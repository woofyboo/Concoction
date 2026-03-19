package net.mcreator.concoction.block;

import com.mojang.serialization.MapCodec;

public class PinkWovenCarpetBlock extends AbstractWovenCarpetBlock {
	public static final MapCodec<PinkWovenCarpetBlock> CODEC = simpleCodec(PinkWovenCarpetBlock::new);

	public PinkWovenCarpetBlock() {
		super();
	}

	private PinkWovenCarpetBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected MapCodec<PinkWovenCarpetBlock> codec() {
		return CODEC;
	}
}
