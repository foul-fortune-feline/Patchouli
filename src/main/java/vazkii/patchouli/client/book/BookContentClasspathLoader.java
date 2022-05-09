package vazkii.patchouli.client.book;

import net.fabricmc.loader.api.ModContainer;
import net.minecraft.util.Identifier;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.Nullable;
import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.BiFunction;

public final class BookContentClasspathLoader implements BookContentLoader {
	public static final BookContentClasspathLoader INSTANCE = new BookContentClasspathLoader();

	private BookContentClasspathLoader() {}

	private BiFunction<Path, Path, Boolean> pred(String modId, List<Identifier> list) {
		return (root, file) -> {
			Path rel = root.relativize(file);
			String relName = rel.toString();
			if (relName.endsWith(".json")) {
				relName = FilenameUtils.removeExtension(FilenameUtils.separatorsToUnix(relName));
				Identifier res = new Identifier(modId, relName);
				list.add(res);
			}

			return true;
		};
	}

	@Override
	public void findFiles(Book book, String dir, List<Identifier> list) {
		ModContainer mod = book.owner;
		String id = mod.getMetadata().getId();
		BookRegistry.findFiles(mod, String.format("data/%s/%s/%s/%s/%s", id, BookRegistry.BOOKS_LOCATION, book.id.getPath(), BookContentsBuilder.DEFAULT_LANG, dir), path -> true, pred(id, list), false);
	}

	@Nullable
	@Override
	public InputStream loadJson(Book book, Identifier resloc, @Nullable Identifier fallback) {
		String path = "data/" + resloc.getNamespace() + "/" + resloc.getPath();
		Patchouli.LOGGER.debug("Loading {}", path);

		try {
			return Files.newInputStream(book.owner.findPath(path).orElseThrow());
		} catch (IOException ex) {
			if (fallback != null) {
				return loadJson(book, fallback, null);
			} else {
				Patchouli.LOGGER.warn("Failed to load {}.", resloc, ex);
				return null;
			}
		}
	}
}
