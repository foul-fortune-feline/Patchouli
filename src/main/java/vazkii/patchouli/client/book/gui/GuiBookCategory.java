package vazkii.patchouli.client.book.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import vazkii.patchouli.client.book.BookCategory;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.button.GuiButtonCategory;
import vazkii.patchouli.common.book.Book;

import java.util.*;

public class GuiBookCategory extends GuiBookEntryList {

	private final BookCategory category;
	private int subcategoryButtonCount;

	public GuiBookCategory(Book book, BookCategory category) {
		super(book, category.getName());
		this.category = category;
	}

	@Override
	protected String getDescriptionText() {
		return category.getDescription();
	}

	@Override
	protected Collection<BookEntry> getEntries() {
		return category.getEntries();
	}

	@Override
	void drawForegroundElements(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
		super.drawForegroundElements(ms, mouseX, mouseY, partialTicks);
		if (getEntries().isEmpty() && subcategoryButtonCount <= 16 && subcategoryButtonCount > 0) {
			int bottomSeparator = TOP_PADDING + 37 + 24 * ((subcategoryButtonCount - 1) / 4 + 1);
			drawSeparator(ms, book, RIGHT_PAGE_X, bottomSeparator);
		}
	}

	@Override
	protected void addSubcategoryButtons() {
		int i = 0;
		List<BookCategory> categories = new ArrayList<>(book.getContents().categories.values());
		categories.removeIf(cat -> cat.getParentCategory() != category || cat.shouldHide());
		Collections.sort(categories);
		subcategoryButtonCount = categories.size();

		int baseX, baseY;
		boolean rightPageFree = getEntries().isEmpty();
		if (rightPageFree) {
			baseX = RIGHT_PAGE_X + 10;
			baseY = TOP_PADDING + 25;
		} else {
			baseX = LEFT_PAGE_X + 10;
			baseY = TOP_PADDING + PAGE_HEIGHT - (categories.size() / 4) * 20 - (!book.advancementsEnabled() ? 38 : 64);
		}

		for (BookCategory ocategory : categories) {
			int x = baseX + (i % 4) * 24;
			int y = baseY + (i / 4) * (rightPageFree ? 24 : 20);

			ButtonWidget button = new GuiButtonCategory(this, x, y, ocategory, this::handleButtonCategory);
			addDrawableChild(button);
			entryButtons.add(button);

			i++;
		}
	}

	@Override
	protected String getChapterListTitle() {
		if (getEntries().isEmpty() && subcategoryButtonCount > 0) {
			return I18n.translate("patchouli.gui.lexicon.categories");
		}
		return super.getChapterListTitle();
	}

	@Override
	protected String getNoEntryMessage() {
		if (subcategoryButtonCount > 0) {
			return "";
		}
		return super.getNoEntryMessage();
	}

	@Override
	protected TextFieldWidget createSearchBar() {
		TextFieldWidget widget = super.createSearchBar();
		if (getEntries().isEmpty()) {
			widget.active = false;
			widget.setEditable(false);
			widget.setVisible(false);
		}
		return widget;
	}

	@Override
	protected boolean doesEntryCountForProgress(BookEntry entry) {
		return entry.getCategory() == category;
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this || (obj instanceof GuiBookCategory && ((GuiBookCategory) obj).category == category && ((GuiBookCategory) obj).spread == spread);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(category) * 31 + Objects.hashCode(spread);
	}

	@Override
	public boolean canBeOpened() {
		return !category.isLocked() && !equals(MinecraftClient.getInstance().currentScreen);
	}

}
