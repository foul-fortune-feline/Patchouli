package vazkii.patchouli.client.book.template.variable;

import com.google.gson.JsonElement;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.text.Text;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableSerializer;
import vazkii.patchouli.api.VariableHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class VariableHelperImpl implements VariableHelper {

	public VariableHelperImpl() {
		registerSerializer(new ItemStackVariableSerializer(), ItemStack.class);
		registerSerializer(new ItemStackArrayVariableSerializer(), ItemStack[].class);
		registerSerializer(new IngredientVariableSerializer(), Ingredient.class);
		registerSerializer(new TextComponentVariableSerializer(), Text.class);
	}

	public Map<Class<?>, IVariableSerializer<?>> serializers = new HashMap<>();

	@Override
	public <T> IVariableSerializer<T> serializerForClass(Class<T> clazz) {
		@SuppressWarnings("unchecked")
		IVariableSerializer<T> serializer = (IVariableSerializer<T>) serializers.get(clazz);
		if (serializer == null && clazz.isArray()) {
			Class<?> componentType = clazz.getComponentType();
			IVariableSerializer<?> componentSerializer = serializerForClass(componentType);
			if (componentSerializer != null) {
				@SuppressWarnings({ "unchecked", "rawtypes" })
				IVariableSerializer<T> arraySerializer = (IVariableSerializer<T>) new GenericArrayVariableSerializer(componentSerializer, componentType);
				serializers.put(clazz, arraySerializer);
				return arraySerializer;
			}
		}
		return serializer;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> IVariable createFromObject(T object) {
		Class<?> clazz = object.getClass();
		for (Entry<Class<?>, IVariableSerializer<?>> e : serializers.entrySet()) {
			if (e.getKey().isAssignableFrom(clazz)) {
				return create(((IVariableSerializer<T>) e.getValue()).toJson(object), clazz);
			}
		}

		throw new IllegalArgumentException(String.format("Can't serialize object %s of type %s to IVariable", object, clazz));
	}

	@Override
	public IVariable createFromJson(JsonElement elem) {
		return create(elem, null);
	}

	private IVariable create(JsonElement elem, Class<?> originator) {
		return new Variable(elem, originator);
	}

	@Override
	public <T> VariableHelper registerSerializer(IVariableSerializer<T> serializer, Class<T> clazz) {
		serializers.put(clazz, serializer);
		return this;
	}

}
