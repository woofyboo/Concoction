package net.mcreator.concoction.block;

import com.mojang.serialization.MapCodec;

public class BrownWovenCarpetBlock extends AbstractWovenCarpetBlock {
	public static final MapCodec<BrownWovenCarpetBlock> CODEC = simpleCodec(BrownWovenCarpetBlock::new);

	public BrownWovenCarpetBlock() {
		super();
	}

	private BrownWovenCarpetBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected MapCodec<BrownWovenCarpetBlock> codec() {
		return CODEC;
	}
}
