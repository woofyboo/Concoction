package net.mcreator.concoction.block;

import com.mojang.serialization.MapCodec;

public class WhiteWovenCarpetBlock extends AbstractWovenCarpetBlock {
	public static final MapCodec<WhiteWovenCarpetBlock> CODEC = simpleCodec(WhiteWovenCarpetBlock::new);

	public WhiteWovenCarpetBlock() {
		super();
	}

	private WhiteWovenCarpetBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected MapCodec<WhiteWovenCarpetBlock> codec() {
		return CODEC;
	}
}
