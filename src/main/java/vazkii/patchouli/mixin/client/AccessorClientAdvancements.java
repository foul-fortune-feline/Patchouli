package vazkii.patchouli.mixin.client;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementManager;
import net.minecraft.advancement.AdvancementProgress;
//import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.client.network.ClientAdvancementManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ClientAdvancementManager.class)
public interface AccessorClientAdvancements {
	@Accessor("advancementProgresses")
	Map<Advancement, AdvancementProgress> getProgress();
}
