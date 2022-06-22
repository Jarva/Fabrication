package com.unascribed.fabrication.mixin.e_mechanics.obsidian_tears;

import com.unascribed.fabrication.FabConf;
import net.minecraft.network.encryption.PlayerPublicKey;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.unascribed.fabrication.support.injection.FabInject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;
import com.unascribed.fabrication.support.EligibleIf;

import net.minecraft.block.Blocks;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(ServerPlayerEntity.class)
@EligibleIf(configAvailable="*.obsidian_tears")
public abstract class MixinServerPlayerEntity extends PlayerEntity {

	public MixinServerPlayerEntity(World world, BlockPos pos, float yaw, GameProfile gameProfile, @Nullable PlayerPublicKey publicKey) {
		super(world, pos, yaw, gameProfile, publicKey);
	}

	@FabInject(at=@At("TAIL"), method="copyFrom(Lnet/minecraft/server/network/ServerPlayerEntity;Z)V")
	public void copyFrom(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
		if (!FabConf.isEnabled("*.obsidian_tears")) return;
		if (!alive) {
			ServerPlayerEntity self = (ServerPlayerEntity)(Object)this;
			if (oldPlayer.getSpawnPointPosition() != null && world.getRegistryKey().equals(oldPlayer.getSpawnPointDimension())
					&& world.getBlockState(oldPlayer.getSpawnPointPosition()).getBlock() == Blocks.CRYING_OBSIDIAN) {
				NbtCompound hunger = new NbtCompound();
				self.getHungerManager().writeNbt(hunger);
				hunger.putFloat("foodSaturationLevel", 0);
				hunger.putInt("foodLevel", 15);
				self.getHungerManager().readNbt(hunger);
				self.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 15*20, 0));
				self.setHealth(getHealth()*0.5f);
			}
		}
	}

}
