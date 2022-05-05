package vazkii.patchouli.client.book.gui.button;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Formatting;
import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.base.PersistentData;
import vazkii.patchouli.client.book.gui.GuiBook;

public class GuiButtonBookEye extends GuiButtonBook {

	public GuiButtonBookEye(GuiBook parent, int x, int y, ButtonWidget.PressAction onPress) {
		super(parent, x, y, 308, 31, 11, 11, onPress,
				MutableText.of(new TranslatableTextContent("patchouli.gui.lexicon.button.visualize")),
				MutableText.of(new TranslatableTextContent("patchouli.gui.lexicon.button.visualize.info"))
						.setStyle(Style.EMPTY.withFormatting(Formatting.GRAY)));
	}

	@Override
	public void renderButton(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
		super.renderButton(ms, mouseX, mouseY, partialTicks);

		if (!PersistentData.data.clickedVisualize && (ClientTicker.ticksInGame) % 20 < 10) {
			parent.getMinecraft().textRenderer.drawWithShadow(ms, "!", x, y, 0xFF3333);
		}
	}

}
