package io.github.haykam821.diceyheights.game.player;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableSet;

import io.github.haykam821.diceyheights.game.ItemSpawnStrategy;
import io.github.haykam821.diceyheights.game.map.DiceyHeightsMap;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameMode;
import xyz.nucleoid.plasmid.util.InventoryUtil;

public class PlayerEntry {
	private ServerPlayerEntity alivePlayer;

	private final TeamEntry team;

	private final Vec3d pillarPos;
	private final float pillarYaw;

	public PlayerEntry(ServerPlayerEntity player, TeamEntry team, Vec3d pillarPos, float pillarYaw) {
		this.alivePlayer = player;

		this.team = team;

		this.pillarPos = pillarPos;
		this.pillarYaw = pillarYaw;
	}

	/**
	 * {@return the player entity, or {@code null} if the player has been eliminated}
	 */
	public ServerPlayerEntity getAlivePlayer() {
		return this.alivePlayer;
	}

	public void clearAlivePlayer() {
		this.alivePlayer = null;
	}

	public TeamEntry getTeam() {
		return this.team;
	}

	public Vec3d getPillarPos() {
		return this.pillarPos;
	}

	public void spawn(DiceyHeightsMap map, ServerWorld world, Random random, int ticksUntilNextItem) {
		map.placePillar(world, random, this);

		this.reset(GameMode.SURVIVAL);
		this.teleportToPillar(world, true);

		this.alivePlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, ticksUntilNextItem, 127, true, false));
		this.alivePlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, ticksUntilNextItem, 128, true, false));
	}

	public void tick(ServerWorld world, ItemSpawnStrategy strategy, int ticksUntilNextItem, boolean beforeFirstItem) {
		if (beforeFirstItem && ticksUntilNextItem % 5 == 0) {
			this.teleportToPillar(world, false);
		}

		if (this.isPillarApplicable(strategy)) {
			spawnPillarItemSpawnParticles(world, this.pillarPos);
		}
	}

	public void giveItemStack(ServerWorld world, ItemSpawnStrategy strategy, Supplier<ItemStack> stackSupplier) {
		if (this.isPillarApplicable(strategy)) {
			ItemStack stack = stackSupplier.get();

			if (!stack.isEmpty()) {
				ItemEntity entity = new ItemEntity(world, this.pillarPos.getX(), this.pillarPos.getY(), this.pillarPos.getZ(), stack);
				world.spawnEntity(entity);
			}
		} else if (strategy == ItemSpawnStrategy.DIRECT && this.alivePlayer != null) {
			this.alivePlayer.giveItemStack(stackSupplier.get());
		}
	}

	private void teleportToPillar(ServerWorld world, boolean rotate) {
		this.alivePlayer.setVelocity(Vec3d.ZERO);
		this.alivePlayer.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(this.alivePlayer));

		this.alivePlayer.fallDistance = 0;

		if (rotate) {
			this.alivePlayer.teleport(world, this.pillarPos.getX(), this.pillarPos.getY(), this.pillarPos.getZ(), this.pillarYaw, 0);
		} else {
			Set<PositionFlag> flags = ImmutableSet.of(PositionFlag.X_ROT, PositionFlag.Y_ROT);
			this.alivePlayer.networkHandler.requestTeleport(this.pillarPos.getX(), this.pillarPos.getY(), this.pillarPos.getZ(), this.alivePlayer.getYaw(), this.alivePlayer.getPitch(), flags);
		}
	}

	private boolean isPillarApplicable(ItemSpawnStrategy strategy) {
		if (strategy == ItemSpawnStrategy.AT_PILLAR) return true;
		if (strategy == ItemSpawnStrategy.AT_PILLAR_WHEN_ALIVE && this.alivePlayer != null) return true;

		return false;
	}

	public Text getWinMessage() {
		return Text.translatable("text.diceyheights.win", this.alivePlayer.getDisplayName()).formatted(Formatting.GOLD);
	}

	public Text getEliminationMessage() {
		return Text.translatable("text.diceyheights.eliminated", this.alivePlayer.getDisplayName()).formatted(Formatting.RED);
	}

	public void reset(GameMode gameMode) {
		this.alivePlayer.changeGameMode(gameMode);

		this.alivePlayer.clearStatusEffects();
		InventoryUtil.clear(this.alivePlayer);

		// https://bugs.mojang.com/browse/MC-99785
		Box leashBox = Box.of(this.alivePlayer.getPos(), 7, 7, 7);
		List<MobEntity> leashEntities = this.alivePlayer.getWorld().getNonSpectatingEntities(MobEntity.class, leashBox);

		for (MobEntity entity : leashEntities) {
			if (entity.isLeashed() && entity.getHoldingEntity() == this.alivePlayer) {
				entity.detachLeash(true, true);
			}
		}
	}

	@Override
	public String toString() {
		return "PlayerEntry{alivePlayer=" + this.alivePlayer + ", pillarPos=" + this.pillarPos + ", pillarYaw=" + this.pillarYaw + "}";
	}

	private static void spawnPillarItemSpawnParticles(ServerWorld world, Vec3d pos) {
		// This effect could be improved
		ParticleEffect particle = new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.WHITE_WOOL.getDefaultState());
		world.spawnParticles(particle, pos.getX(), pos.getY(), pos.getZ(), 0, 0, 1, 0, 1);
	}
}
