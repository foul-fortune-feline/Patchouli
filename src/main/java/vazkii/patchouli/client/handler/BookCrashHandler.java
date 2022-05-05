package vazkii.patchouli.client.handler;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.crash.CrashReport;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.client.book.gui.GuiBookEntryList;
import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.book.Book;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.function.Supplier;

public class BookCrashHandler implements Supplier<String> {
	private static final String INDENT = "\n\t\t";
	private static final String LABEL = "Patchouli open book context";

	public static void appendToCrashReport(CrashReport report) {
		var mc = MinecraftClient.getInstance();
		if (mc == null || !(mc.currentScreen instanceof GuiBook)) {
			return;
		}
		try {
			report.addElement("detail").add(LABEL, new BookCrashHandler());
		} catch (Exception e) {
			Patchouli.LOGGER.fatal("Failed to extend crash report system info", e);
		}
	}

	@Override
	public String get() {
		Screen screen = MinecraftClient.getInstance().currentScreen;
		if (!(screen instanceof GuiBook gui)) {
			return "n/a";
		}
		Book book = gui.book;
		StringBuilder builder = new StringBuilder(INDENT);

		builder.append("Open book: ").append(book.id);
		if (gui instanceof GuiBookEntry entry) {
			builder.append(INDENT).append("Current entry: ").append(entry.getEntry().getId());
		} else if (gui instanceof GuiBookEntryList list) {
			builder.append(INDENT).append("Search query: ").append(list.getSearchQuery());
		}
		builder.append(INDENT).append("Current page spread: ").append(gui.getSpread());
		if (book.getContents().isErrored()) {
			Exception ex = book.getContents().getException();
			builder.append(INDENT).append("Book loading error: ");
			try (StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw)) {
				ex.printStackTrace(pw);
				builder.append(sw.toString().replaceAll("\n", INDENT));
			} catch (IOException ignored) {}
		}
		return builder.toString();
	}
}
