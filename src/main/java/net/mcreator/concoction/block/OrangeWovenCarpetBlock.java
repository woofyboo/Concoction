package net.mcreator.concoction.block;

import com.mojang.serialization.MapCodec;

public class OrangeWovenCarpetBlock extends AbstractWovenCarpetBlock {
	public static final MapCodec<OrangeWovenCarpetBlock> CODEC = simpleCodec(OrangeWovenCarpetBlock::new);

	public OrangeWovenCarpetBlock() {
		super();
	}

	private OrangeWovenCarpetBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected MapCodec<OrangeWovenCarpetBlock> codec() {
		return CODEC;
	}
}
