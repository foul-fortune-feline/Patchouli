package vazkii.patchouli.client.book.template.variable;

import com.google.gson.JsonElement;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import vazkii.patchouli.api.IVariableSerializer;

public class TextComponentVariableSerializer implements IVariableSerializer<Text> {
	@Override
	public Text fromJson(JsonElement json) {
		if (json.isJsonNull()) {
			return MutableText.of(new LiteralTextContent(""));
		}
		if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()) {
			return MutableText.of(new LiteralTextContent(json.getAsString()));
		}
		return Text.Serializer.fromJson(json);
	}

	@Override
	public JsonElement toJson(Text stack) {
		return Text.Serializer.toJsonTree(stack);
	}
}
