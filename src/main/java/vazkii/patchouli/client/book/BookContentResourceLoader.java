package vazkii.patchouli.client.book;

import com.google.common.base.Preconditions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public final class BookContentResourceLoader implements BookContentLoader {
	public static final BookContentResourceLoader INSTANCE = new BookContentResourceLoader();

	private BookContentResourceLoader() {}

	@Override
	public void findFiles(Book book, String dir, List<Identifier> list) {
		String prefix = String.format("%s/%s/%s/%s", BookRegistry.BOOKS_LOCATION, book.id.getPath(), BookContentsBuilder.DEFAULT_LANG, dir);
		Collection<Identifier> files = MinecraftClient.getInstance().getResourceManager()
				.findAllResources(prefix, p -> p.getPath().endsWith(".json")).keySet();

		files.stream()
				.distinct()
				.filter(file -> file.getNamespace().equals(book.id.getNamespace()))
				.map(file -> {
					// caller expects list to contain logical id's, not file paths.
					// we end up going from path -> id -> back to path, but it's okay as a transitional measure
					Preconditions.checkArgument(file.getPath().startsWith(prefix));
					Preconditions.checkArgument(file.getPath().endsWith(".json"));
					String newPath = file.getPath().substring(prefix.length(), file.getPath().length() - ".json".length());
					// Vanilla expects `prefix` above to not have a trailing slash, so we
					// have to remove it ourselves from the path
					if (newPath.startsWith("/")) {
						newPath = newPath.substring(1);
					}
					return new Identifier(file.getNamespace(), newPath);
				})
				.forEach(list::add);
	}

	@Nullable
	@Override
	public InputStream loadJson(Book book, Identifier file, @Nullable Identifier fallback) {
		Patchouli.LOGGER.debug("Loading {}", file);
		ResourceManager manager = MinecraftClient.getInstance().getResourceManager();
		try {
			Optional<Resource> r = manager.getResource(file);
			if (r.isPresent()) {
				return r.get().getInputStream();
			}

			if (fallback != null) {
				r = manager.getResource(file);
				Patchouli.LOGGER.error("Attempting load resource: {}", r.isPresent() ? r.get().getInputStream() : null);
				return r.isPresent() ? r.get().getInputStream() : null;
			}
			return null;
		} catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}
	}
}
