package vazkii.patchouli.mixin.client;

import net.minecraft.client.MinecraftClient;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import vazkii.patchouli.client.base.ClientAdvancements;

@Mixin(MinecraftClient.class)
public class MixinMinecraft {
	@Inject(at = @At("HEAD"), method = "close()V")
	public void patchouli_onLogout(CallbackInfo info) {
		ClientAdvancements.playerLogout();
	}

}
