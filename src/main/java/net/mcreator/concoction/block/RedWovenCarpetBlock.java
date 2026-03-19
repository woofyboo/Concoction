package net.mcreator.concoction.block;

import com.mojang.serialization.MapCodec;

public class RedWovenCarpetBlock extends AbstractWovenCarpetBlock {
	public static final MapCodec<RedWovenCarpetBlock> CODEC = simpleCodec(RedWovenCarpetBlock::new);

	public RedWovenCarpetBlock() {
		super();
	}

	private RedWovenCarpetBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected MapCodec<RedWovenCarpetBlock> codec() {
		return CODEC;
	}
}
