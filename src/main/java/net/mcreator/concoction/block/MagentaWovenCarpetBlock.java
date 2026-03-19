package net.mcreator.concoction.block;

import com.mojang.serialization.MapCodec;

public class MagentaWovenCarpetBlock extends AbstractWovenCarpetBlock {
	public static final MapCodec<MagentaWovenCarpetBlock> CODEC = simpleCodec(MagentaWovenCarpetBlock::new);

	public MagentaWovenCarpetBlock() {
		super();
	}

	private MagentaWovenCarpetBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected MapCodec<MagentaWovenCarpetBlock> codec() {
		return CODEC;
	}
}
