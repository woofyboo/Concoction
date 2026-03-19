package net.mcreator.concoction.block;

import com.mojang.serialization.MapCodec;

public class PurpleWovenCarpetBlock extends AbstractWovenCarpetBlock {
	public static final MapCodec<PurpleWovenCarpetBlock> CODEC = simpleCodec(PurpleWovenCarpetBlock::new);

	public PurpleWovenCarpetBlock() {
		super();
	}

	private PurpleWovenCarpetBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected MapCodec<PurpleWovenCarpetBlock> codec() {
		return CODEC;
	}
}
