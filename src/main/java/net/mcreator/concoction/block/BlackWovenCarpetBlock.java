package net.mcreator.concoction.block;

import com.mojang.serialization.MapCodec;

public class BlackWovenCarpetBlock extends AbstractWovenCarpetBlock {
	public static final MapCodec<BlackWovenCarpetBlock> CODEC = simpleCodec(BlackWovenCarpetBlock::new);

	public BlackWovenCarpetBlock() {
		super();
	}

	private BlackWovenCarpetBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected MapCodec<BlackWovenCarpetBlock> codec() {
		return CODEC;
	}
}
