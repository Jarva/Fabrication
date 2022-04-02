package com.unascribed.fabrication.mixin.f_balance.interrupting_damage;

import com.unascribed.fabrication.FabConf;
import com.unascribed.fabrication.interfaces.InterruptableRangedMob;
import com.unascribed.fabrication.support.ConfigPredicates;
import com.unascribed.fabrication.support.EligibleIf;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
@EligibleIf(configAvailable="*.interrupting_damage")
public abstract class MixinLivingEntity {

	@Shadow
	public abstract boolean isUsingItem();

	@Shadow
	public abstract void stopUsingItem();

	@Inject(at=@At("HEAD"), method="damage(Lnet/minecraft/entity/damage/DamageSource;F)Z")
	public void interruptUsage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		if (!(FabConf.isEnabled("*.interrupting_damage") && amount >= 2 && ConfigPredicates.shouldRun("*.interrupting_damage", (LivingEntity)(Object)this))) return;
		if (this instanceof InterruptableRangedMob) ((InterruptableRangedMob)this).fabrication$interruptRangedMob();
		if (isUsingItem()) stopUsingItem();
	}

}