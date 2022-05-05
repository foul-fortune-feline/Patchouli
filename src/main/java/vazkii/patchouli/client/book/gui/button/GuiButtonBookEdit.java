package vazkii.patchouli.client.book.gui.button;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Formatting;
import vazkii.patchouli.client.book.gui.GuiBook;

public class GuiButtonBookEdit extends GuiButtonBook {

	public GuiButtonBookEdit(GuiBook parent, int x, int y, ButtonWidget.PressAction onPress) {
		super(parent, x, y, 308, 9, 11, 11, onPress,
				MutableText.of(new TranslatableTextContent("patchouli.gui.lexicon.button.editor")),
				MutableText.of(new TranslatableTextContent("patchouli.gui.lexicon.button.editor.info"))
						.setStyle(Style.EMPTY.withFormatting(Formatting.GRAY)));
	}

}
