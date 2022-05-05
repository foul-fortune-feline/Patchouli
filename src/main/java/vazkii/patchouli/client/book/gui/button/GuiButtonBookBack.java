package vazkii.patchouli.client.book.gui.button;

import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Formatting;
import vazkii.patchouli.client.book.gui.GuiBook;

public class GuiButtonBookBack extends GuiButtonBook {

	public GuiButtonBookBack(GuiBook parent, int x, int y) {
		super(parent, x, y, 308, 0, 18, 9, parent::canSeeBackButton, parent::handleButtonBack,
				MutableText.of(new TranslatableTextContent("patchouli.gui.lexicon.button.back")),
				MutableText.of(new TranslatableTextContent("patchouli.gui.lexicon.button.back.info")).setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
	}

}
