package io.github.haykam821.diceyheights.game.map;

import java.util.Set;

import io.github.haykam821.diceyheights.game.player.PlayerEntry;
import net.minecraft.block.BlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import xyz.nucleoid.map_templates.BlockBounds;
import xyz.nucleoid.map_templates.MapTemplate;
import xyz.nucleoid.plasmid.api.game.world.generator.TemplateChunkGenerator;

public class DiceyHeightsMap {
	private final DiceyHeightsMapConfig config;

	public static final int START_Y = 0;

	private final BlockBounds waitingPlatformBounds;
	private final int radius;

	private final MapTemplate template;

	public DiceyHeightsMap(DiceyHeightsMapConfig config, Random random) {
		this.config = config;

		int waitingPlatformY = START_Y + config.waitingPlatformHeight().get(random);
		this.waitingPlatformBounds = BlockBounds.of(-5, waitingPlatformY, -5, 5, waitingPlatformY, 5);

		this.radius = config.radius().get(random);

		this.template = MapTemplate.createEmpty();
		this.placeWaitingPlatform(random);
	}

	private void placeWaitingPlatform(Random random) {
		for (BlockPos pos : this.waitingPlatformBounds) {
			BlockState state = this.config.waitingPlatformProvider().get(random, pos);
			this.template.setBlockState(pos, state);
		}
	}

	public void removeWaitingPlatform(ServerWorld world) {
		for (BlockPos pos : this.waitingPlatformBounds) {
			world.breakBlock(pos, false);
		}
	}

	/**
	 * Determines the position of a player's pillar for a given angle.
	 * @param angle the angle of the pillar in radians
	 * @return the spawn position for players on top of the pillar
	 */
	public Vec3d getPillarPos(Random random, float angle) {
		double x = 0.5 + Math.cos(angle) * this.radius;
		double z = 0.5 + Math.sin(angle) * this.radius;

		int height = this.config.pillarHeight().get(random);

		return Vec3d.ofBottomCenter(BlockPos.ofFloored(x, START_Y + height, z));
	}

	/**
	 * Places a pillar according to a player's pillar position.
	 */
	public void placePillar(ServerWorld world, Random random, PlayerEntry player) {
		Vec3d pillarPos = player.getPillarPos();
		BlockPos bottomPos = BlockPos.ofFloored(pillarPos.getX(), START_Y, pillarPos.getZ());
		BlockPos.Mutable pos = bottomPos.mutableCopy();

		while (pos.getY() < pillarPos.getY()) {
			BlockState state = this.getPillarBlock(random, pos, player);
			world.setBlockState(pos, state);

			pos.move(Direction.UP);
		}
	}

	private BlockState getPillarBlock(Random random, BlockPos pos, PlayerEntry player) {
		if (pos.getY() == START_Y && player.getTeam() != null) {
			return player.getTeam().getBlock();
		}

		return this.config.pillarProvider().get(random, pos);
	}

	public Vec3d getWaitingSpawnPos() {
		return this.waitingPlatformBounds.centerTop();
	}

	public ChunkGenerator createGenerator(MinecraftServer server) {
		return new TemplateChunkGenerator(server, this.template);
	}

	public void teleportToWaitingSpawn(ServerPlayerEntity player) {
		player.teleport(player.getServerWorld(), this.getWaitingSpawnPos().getX(), this.getWaitingSpawnPos().getY(), this.getWaitingSpawnPos().getZ(), Set.of(), 0, 0, true);
	}

	public boolean isOutOfBounds(ServerPlayerEntity player) {
		return player.getBodyY(1) < START_Y;
	}
}
