package vazkii.patchouli.common.item;

import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.recipe.ShapedBookRecipe;
import vazkii.patchouli.common.recipe.ShapelessBookRecipe;

public class PatchouliItems {

	public static final Identifier BOOK_ID = new Identifier(Patchouli.MOD_ID, "guide_book");
	public static final Item BOOK = new ItemModBook();

	public static void init() {
		registerItem();
		registerRecipeSerializers();
	}

	private static void registerItem() {
		Registry.register(Registry.ITEM, BOOK_ID, BOOK);
	}

	private static void registerRecipeSerializers() {
		Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(Patchouli.MOD_ID, "shaped_book_recipe"), ShapedBookRecipe.SERIALIZER);
		Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(Patchouli.MOD_ID, "shapeless_book_recipe"), ShapelessBookRecipe.SERIALIZER);
	}
}
