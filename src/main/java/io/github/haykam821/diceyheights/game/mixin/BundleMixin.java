package io.github.haykam821.diceyheights.game.mixin;

import io.github.haykam821.diceyheights.game.DiceyHeightsConfig;
import io.github.haykam821.diceyheights.game.map.DiceyHeightsMapConfig;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.apache.commons.lang3.math.Fraction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.nucleoid.plasmid.api.game.GameSpaceManager;

@Mixin(BundleContentsComponent.class)
public class BundleMixin {
    @Inject(method = "getOccupancy(Lnet/minecraft/item/ItemStack;)Lorg/apache/commons/lang3/math/Fraction;", at = @At("HEAD"), cancellable = true)
    private static void adjustOccupancy(ItemStack stack, CallbackInfoReturnable<Fraction> cir) {
        int slotSize = 4;

        if (stack.getMaxCount() == 1) {
            cir.setReturnValue(Fraction.getFraction(slotSize, 64));
        }
    }
}
