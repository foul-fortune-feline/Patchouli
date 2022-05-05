package vazkii.patchouli.client.book;

import com.google.common.collect.Streams;
import com.google.gson.annotations.SerializedName;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Identifier;
import vazkii.patchouli.common.base.PatchouliConfig;
import vazkii.patchouli.common.book.Book;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class BookCategory extends AbstractReadStateHolder implements Comparable<BookCategory> {

	private String name, description, parent, flag;
	@SerializedName("icon") private String iconRaw;
	private int sortnum;
	private boolean secret = false;

	private transient Book book, trueProvider;
	private transient BookCategory parentCategory;
	private transient List<BookCategory> children = new ArrayList<>();
	private transient List<BookEntry> entries = new ArrayList<>();
	private transient boolean locked;
	private transient BookIcon icon = null;
	private transient Identifier id;

	private transient boolean built;

	public Text getName() {
		return book.i18n ? MutableText.of(new TranslatableTextContent(name)) : MutableText.of(new LiteralTextContent(name));
	}

	public String getDescription() {
		return description;
	}

	public BookIcon getIcon() {
		if (icon == null) {
			icon = BookIcon.from(iconRaw);
		}

		return icon;
	}

	public void addEntry(BookEntry entry) {
		this.entries.add(entry);
	}

	public void addChildCategory(BookCategory category) {
		children.add(category);
	}

	public List<BookEntry> getEntries() {
		return entries;
	}

	public BookCategory getParentCategory() {
		return parentCategory;
	}

	public void updateLockStatus(boolean rootOnly) {
		if (rootOnly && !isRootCategory()) {
			return;
		}

		children.forEach((c) -> c.updateLockStatus(false));

		boolean wasLocked = locked;

		updateLocked: {
			locked = !children.isEmpty() || !entries.isEmpty(); // empty categories are unlocked by default
			for (BookCategory c : children) {
				if (!c.isLocked()) {
					locked = false;
					break updateLocked;
				}
			}

			for (BookEntry e : entries) {
				if (!e.isLocked()) {
					locked = false;
					break updateLocked;
				}
			}
		}

		if (!locked && wasLocked) {
			book.markUpdated();
		}
	}

	public boolean isSecret() {
		return secret;
	}

	public boolean shouldHide() {
		return isSecret() && isLocked();
	}

	public boolean isLocked() {
		return getBook().advancementsEnabled() && locked;
	}

	public boolean isRootCategory() {
		return parent == null || parent.isEmpty();
	}

	public Identifier getId() {
		return id;
	}

	public boolean canAdd() {
		return flag == null || flag.isEmpty() || PatchouliConfig.getConfigFlag(flag);
	}

	@Override
	public int compareTo(BookCategory o) {
		if (book.advancementsEnabled() && o.locked != this.locked) {
			return this.locked ? 1 : -1;
		}

		if (this.sortnum != o.sortnum) {
			return Integer.compare(this.sortnum, o.sortnum);
		}

		return this.getName().getString().compareTo(o.getName().getString());
	}

	public void setBook(Book book) {
		if (book.isExtension) {
			this.book = book.extensionTarget;
			trueProvider = book;
		} else {
			this.book = book;
		}
	}

	public void build(Identifier id, BookContentsBuilder builder) {
		if (built) {
			return;
		}

		this.id = id;

		if (!isRootCategory()) {
			if (parent.contains(":")) {
				parentCategory = builder.getCategory(new Identifier(parent));
			} else {
				String hint = String.format("`%s:%s`", book.getModNamespace(), parent);
				throw new IllegalArgumentException("`parent` must be fully qualified (domain:name). Hint: Try " + hint);
			}
		}

		if (parentCategory != null) {
			parentCategory.addChildCategory(this);
		}

		built = true;
	}

	public Book getBook() {
		return book;
	}

	public Book getTrueProvider() {
		return trueProvider;
	}

	public boolean isExtension() {
		return getTrueProvider() != null && getTrueProvider() != getBook();
	}

	@Override
	protected EntryDisplayState computeReadState() {
		Stream<EntryDisplayState> entryStream = entries.stream().filter(e -> !e.isLocked()).map(BookEntry::getReadState);
		Stream<EntryDisplayState> childrenStream = children.stream().map(BookCategory::getReadState);
		return mostImportantState(Streams.concat(entryStream, childrenStream));
	}

	@Override
	public void markReadStateDirty() {
		super.markReadStateDirty();

		if (parentCategory != null) {
			parentCategory.markReadStateDirty();
		} else {
			book.getContents().markReadStateDirty();
		}
	}

}
