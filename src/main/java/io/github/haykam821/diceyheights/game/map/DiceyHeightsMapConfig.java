package io.github.haykam821.diceyheights.game.map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public record DiceyHeightsMapConfig(
	IntProvider waitingPlatformSize,
	IntProvider waitingPlatformHeight,
	BlockStateProvider waitingPlatformProvider,
	IntProvider radius,
	IntProvider pillarHeight,
	BlockStateProvider pillarProvider,
	int maxHeight
) {
	public static final DiceyHeightsMapConfig DEFAULT = new DiceyHeightsMapConfig(
		ConstantIntProvider.create(5),
		ConstantIntProvider.create(24),
		BlockStateProvider.of(Blocks.MAGENTA_CONCRETE),
		ConstantIntProvider.create(12),
		ConstantIntProvider.create(32),
		BlockStateProvider.of(Blocks.BEDROCK),
		64
	);

	public static final Codec<DiceyHeightsMapConfig> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
			IntProvider.NON_NEGATIVE_CODEC.optionalFieldOf("waiting_platform_size", DEFAULT.waitingPlatformSize()).forGetter(DiceyHeightsMapConfig::waitingPlatformSize),
			IntProvider.POSITIVE_CODEC.optionalFieldOf("waiting_platform_height", DEFAULT.waitingPlatformHeight()).forGetter(DiceyHeightsMapConfig::waitingPlatformHeight),
			BlockStateProvider.TYPE_CODEC.optionalFieldOf("waiting_platform_provider", DEFAULT.waitingPlatformProvider()).forGetter(DiceyHeightsMapConfig::waitingPlatformProvider),
			IntProvider.NON_NEGATIVE_CODEC.optionalFieldOf("radius", DEFAULT.radius()).forGetter(DiceyHeightsMapConfig::radius),
			IntProvider.POSITIVE_CODEC.optionalFieldOf("pillar_height", DEFAULT.pillarHeight()).forGetter(DiceyHeightsMapConfig::pillarHeight),
			BlockStateProvider.TYPE_CODEC.optionalFieldOf("pillar_provider", DEFAULT.pillarProvider()).forGetter(DiceyHeightsMapConfig::pillarProvider),
			Codec.INT.optionalFieldOf("max_height", DEFAULT.maxHeight()).forGetter(DiceyHeightsMapConfig::maxHeight)
		).apply(instance, DiceyHeightsMapConfig::new);
	});
}
