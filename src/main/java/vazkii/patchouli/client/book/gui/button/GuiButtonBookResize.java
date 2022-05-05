package vazkii.patchouli.client.book.gui.button;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Formatting;
import vazkii.patchouli.client.base.PersistentData;
import vazkii.patchouli.client.book.gui.GuiBook;

import java.util.Arrays;
import java.util.List;

public class GuiButtonBookResize extends GuiButtonBook {

	final boolean uiscale;

	public GuiButtonBookResize(GuiBook parent, int x, int y, boolean uiscale, ButtonWidget.PressAction onPress) {
		super(parent, x, y, 330, 9, 11, 11, onPress,
				MutableText.of(new TranslatableTextContent("patchouli.gui.lexicon.button.resize")));
		this.uiscale = uiscale;
	}

	@Override
	public List<Text> getTooltip() {
		return !uiscale ? tooltip : Arrays.asList(
				tooltip.get(0),
				MutableText.of(new TranslatableTextContent("patchouli.gui.lexicon.button.resize.size" + PersistentData.data.bookGuiScale))
						.setStyle(Style.EMPTY.withFormatting(Formatting.GRAY)));
	}

}
