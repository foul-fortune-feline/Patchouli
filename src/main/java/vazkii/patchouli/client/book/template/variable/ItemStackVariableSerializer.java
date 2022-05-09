package vazkii.patchouli.client.book.template.variable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;
import vazkii.patchouli.api.IVariableSerializer;
import vazkii.patchouli.common.util.ItemStackUtil;

public class ItemStackVariableSerializer implements IVariableSerializer<ItemStack> {
	@Override
	public ItemStack fromJson(JsonElement json) {
		if (json.isJsonNull()) {
			return ItemStack.EMPTY;
		}
		if (json.isJsonPrimitive()) {
			return ItemStackUtil.loadStackFromString(json.getAsString());
		}
		if (json.isJsonObject()) {
			return ItemStackUtil.loadStackFromJson(json.getAsJsonObject());
		}
		throw new IllegalArgumentException("Can't make an ItemStack from an array!");
	}

	@Override
	public JsonElement toJson(ItemStack stack) {
		// Adapted from net.minecraftforge.common.crafting.StackList::toJson
		JsonObject ret = new JsonObject();
		ret.addProperty("item", Registry.ITEM.getId(stack.getItem()).toString());
		if (stack.getCount() != 1) {
			ret.addProperty("count", stack.getCount());
		}
		if (stack.getNbt() != null) {
			ret.addProperty("nbt", stack.getNbt().toString());
		}
		return ret;
	}

}
