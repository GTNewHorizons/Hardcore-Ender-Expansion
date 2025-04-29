package chylex.hee.mixins.early.minecraft;

import chylex.hee.block.BlockDragonEggCustom;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDragonEgg;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Block.class)
public class MixinBlock {
    @ModifyArg(
        method = "registerBlocks",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/RegistryNamespaced;addObject(ILjava/lang/String;Ljava/lang/Object;)V"),
        slice = @Slice(
            from = @At(value = "CONSTANT", args = "intValue=122"),
            to = @At(value = "CONSTANT", args = "intValue=123")))
    private static Object hee$replaceDragonEgg(Object block) {
        return new BlockDragonEggCustom();
    }
}
