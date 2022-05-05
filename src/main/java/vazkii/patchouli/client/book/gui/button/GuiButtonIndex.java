package vazkii.patchouli.client.book.gui.button;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableTextContent;
import vazkii.patchouli.client.book.gui.GuiBook;

public class GuiButtonIndex extends GuiButtonCategory {

	public GuiButtonIndex(GuiBook parent, int x, int y, ButtonWidget.PressAction onPress) {
		super(parent, x, y, parent.book.getIcon(), MutableText.of(new TranslatableTextContent("patchouli.gui.lexicon.index")), onPress);
	}

}
