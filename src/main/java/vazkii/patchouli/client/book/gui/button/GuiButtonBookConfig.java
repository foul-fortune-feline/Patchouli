package vazkii.patchouli.client.book.gui.button;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableTextContent;
import vazkii.patchouli.client.book.gui.GuiBook;

public class GuiButtonBookConfig extends GuiButtonBook {

	public GuiButtonBookConfig(GuiBook parent, int x, int y, ButtonWidget.PressAction onPress) {
		super(parent, x, y, 308, 20, 11, 11, onPress,
				MutableText.of(new TranslatableTextContent("patchouli.gui.lexicon.button.config")));
	}

}
