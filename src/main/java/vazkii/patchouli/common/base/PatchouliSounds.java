package vazkii.patchouli.common.base;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class PatchouliSounds {

	public static final SoundEvent BOOK_OPEN = new SoundEvent(new Identifier(Patchouli.MOD_ID, "book_open"));
	public static final SoundEvent BOOK_FLIP = new SoundEvent(new Identifier(Patchouli.MOD_ID, "book_flip"));

	public static void init() {
		registerSounds();
	}

	private static void registerSounds() {
		Registry.register(Registry.SOUND_EVENT, BOOK_OPEN.getId(), BOOK_OPEN);
		Registry.register(Registry.SOUND_EVENT, BOOK_FLIP.getId(), BOOK_FLIP);
	}

	public static SoundEvent getSound(Identifier key, SoundEvent fallback) {
		return Registry.SOUND_EVENT.getOrEmpty(key).orElse(fallback);
	}

}
