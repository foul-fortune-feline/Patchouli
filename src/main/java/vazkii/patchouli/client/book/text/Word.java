package vazkii.patchouli.client.book.text;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.common.book.Book;

import java.util.List;
import java.util.function.Supplier;

/**
 * A {@code Word} is the smallest textual unit of rendering in Patchouli, and knows its
 * position, dimensions, and formatting.
 */
public class Word {
	private final Book book;
	private final GuiBook gui;
	private final Text text;
	private final List<Word> linkCluster;
	private final Supplier<Boolean> onClick;
	public final int x, y, width, height;

	public Word(GuiBook gui, Span span, MutableText text, int x, int y, int strWidth, List<Word> cluster) {
		this.book = gui.book;
		this.gui = gui;
		this.x = x;
		this.y = y;
		this.width = strWidth;
		this.height = 8;
		this.onClick = span.onClick;
		this.linkCluster = cluster;
		if (!span.tooltip.getString().isEmpty()) {
			text = text.setStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, span.tooltip)));
		}
		this.text = text;
	}

	public void render(MatrixStack ms, TextRenderer textRenderer, Style styleOverride, int mouseX, int mouseY) {
		MutableText toRender = text.copy().setStyle(styleOverride);
		if (isClusterHovered(mouseX, mouseY)) {
			if (onClick != null) {
				toRender.setStyle(Style.EMPTY.withColor(book.linkHoverColor));
			}

			gui.renderTextHoverEffect(ms, text.getStyle(), (int) gui.getRelativeX(mouseX), (int) gui.getRelativeY(mouseY));
		}

		textRenderer.draw(ms, toRender, x, y, -1);
	}

	public boolean click(double mouseX, double mouseY, int mouseButton) {
		if (onClick != null && mouseButton == 0 && isHovered(mouseX, mouseY)) {
			return onClick.get();
		}

		return false;
	}

	private boolean isHovered(double mouseX, double mouseY) {
		return gui.isMouseInRelativeRange(mouseX, mouseY, x, y, width, height);
	}

	private boolean isClusterHovered(double mouseX, double mouseY) {
		if (linkCluster == null) {
			return isHovered(mouseX, mouseY);
		}

		for (Word w : linkCluster) {
			if (w.isHovered(mouseX, mouseY)) {
				return true;
			}
		}

		return false;
	}
}
