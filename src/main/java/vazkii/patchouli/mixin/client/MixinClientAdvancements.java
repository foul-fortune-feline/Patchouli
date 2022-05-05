package vazkii.patchouli.mixin.client;

import net.minecraft.network.packet.s2c.play.AdvancementUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import vazkii.patchouli.client.base.ClientAdvancements;

@Mixin(net.minecraft.client.network.ClientAdvancementManager.class)
public abstract class MixinClientAdvancements {

	@Inject(at = @At("RETURN"), method = "onAdvancements")
	public void patchouli_onSync(AdvancementUpdateS2CPacket packet, CallbackInfo ci) {
		ClientAdvancements.onClientPacket();
	}
}
