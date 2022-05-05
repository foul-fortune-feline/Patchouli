package vazkii.patchouli.client.book.template.component;

import com.mojang.blaze3d.vertex.MatrixStack;

import vazkii.patchouli.client.book.BookContentsBuilder;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.template.TemplateComponent;

public class ComponentSeparator extends TemplateComponent {

	@Override
	public void build(BookContentsBuilder builder, BookPage page, BookEntry entry, int pageNum) {
		if (x == -1) {
			x = 0;
		}
		if (y == -1) {
			y = 12;
		}
	}

	@Override
	public void render(MatrixStack ms, BookPage page, int mouseX, int mouseY, float pticks) {
		GuiBook.drawSeparator(ms, page.book, x, y);
	}

}
