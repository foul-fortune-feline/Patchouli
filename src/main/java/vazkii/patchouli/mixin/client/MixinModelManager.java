package vazkii.patchouli.mixin.client;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.patchouli.client.base.ClientInitializer;

import java.util.Map;

@Mixin(BakedModelManager.class)
public class MixinModelManager {
	@Shadow
	@Final private Map<Identifier, BakedModel> models;

	@Inject(at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/render/model/ModelLoader;getBakedModelMap()Ljava/util/Map;", shift = At.Shift.AFTER), method = "apply(Lnet/minecraft/client/render/model/ModelLoader;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)V")
	public void insertBookModel(ModelLoader loader, ResourceManager manager, Profiler profiler, CallbackInfo info) {
		ClientInitializer.replaceBookModel(loader, models);
	}
}
