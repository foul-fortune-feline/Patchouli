package vazkii.patchouli.client.book.template.test;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.common.base.Patchouli;

import java.util.function.UnaryOperator;

public class ComponentCustomTest implements ICustomComponent {
	private transient int x, y;
	private transient String text = "";

	@Override
	public void build(int componentX, int componentY, int pageNum) {
		x = componentX;
		y = componentY;
		Patchouli.LOGGER.debug("Custom Component Test built at ({}, {}) page {}", componentX, componentY, pageNum);
	}

	@Override
	public void render(MatrixStack ms, IComponentRenderContext context, float pticks, int mouseX, int mouseY) {
		Text toRender = MutableText.of(new LiteralTextContent(text)).setStyle(context.getStyle());
		MinecraftClient.getInstance().textRenderer.drawWithShadow(ms, toRender, x, y, -1);
	}

	@Override
	public boolean mouseClicked(IComponentRenderContext context, double mouseX, double mouseY, int mouseButton) {
		Patchouli.LOGGER.debug("Custom Component Test clicked at ({}, {}) button {}", mouseX, mouseY, mouseButton);
		return false;
	}

	@Override
	public void onVariablesAvailable(UnaryOperator<IVariable> lookup) {
		text = lookup.apply(IVariable.wrap("First we eat #spaghet#, then we drink #pop#")).asString();
	}
}
