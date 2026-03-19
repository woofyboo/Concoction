package net.mcreator.concoction.block;

import com.mojang.serialization.MapCodec;

public class WanderingTraderCarpetBlock extends AbstractWovenCarpetBlock {
	public static final MapCodec<WanderingTraderCarpetBlock> CODEC = simpleCodec(WanderingTraderCarpetBlock::new);

	public WanderingTraderCarpetBlock() {
		super();
	}

	private WanderingTraderCarpetBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected MapCodec<WanderingTraderCarpetBlock> codec() {
		return CODEC;
	}
}
