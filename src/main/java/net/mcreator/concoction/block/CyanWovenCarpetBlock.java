package net.mcreator.concoction.block;

import com.mojang.serialization.MapCodec;

public class CyanWovenCarpetBlock extends AbstractWovenCarpetBlock {
	public static final MapCodec<CyanWovenCarpetBlock> CODEC = simpleCodec(CyanWovenCarpetBlock::new);

	public CyanWovenCarpetBlock() {
		super();
	}

	private CyanWovenCarpetBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected MapCodec<CyanWovenCarpetBlock> codec() {
		return CODEC;
	}
}
