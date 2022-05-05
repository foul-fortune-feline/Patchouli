package vazkii.patchouli.common.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.common.base.PatchouliSounds;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;

import java.util.List;

public class ItemModBook extends Item {

	private static final String TAG_BOOK = "patchouli:book";

	public ItemModBook() {
		super(new Item.Properties()
				.stacksTo(1)
				.tab(CreativeModeTab.TAB_MISC));
	}

	public static float getCompletion(ItemStack stack) {
		Book book = getBook(stack);
		float progression = 0F; // default incomplete

		if (book != null) {
			int totalEntries = 0;
			int unlockedEntries = 0;

			for (BookEntry entry : book.getContents().entries.values()) {
				if (!entry.isSecret()) {
					totalEntries++;
					if (!entry.isLocked()) {
						unlockedEntries++;
					}
				}
			}

			progression = ((float) unlockedEntries) / Math.max(1f, (float) totalEntries);
		}

		return progression;
	}

	public static ItemStack forBook(Book book) {
		return forBook(book.id);
	}

	public static ItemStack forBook(ResourceLocation book) {
		ItemStack stack = new ItemStack(PatchouliItems.BOOK);

		CompoundTag cmp = new CompoundTag();
		cmp.putString(TAG_BOOK, book.toString());
		stack.setTag(cmp);

		return stack;
	}

	@Override
	public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> items) {
		String tabName = tab.getRecipeFolderName();
		BookRegistry.INSTANCE.books.values().forEach(b -> {
			if (!b.noBook && !b.isExtension && (tab == CreativeModeTab.TAB_SEARCH || b.creativeTab.equals(tabName))) {
				items.add(forBook(b));
			}
		});
	}

	public static Book getBook(ItemStack stack) {
		Identifier res = getBookId(stack);
		if (res == null) {
			return null;
		}
		return BookRegistry.INSTANCE.books.get(res);
	}

	private static Identifier getBookId(ItemStack stack) {
		if (!stack.hasNbt() || !stack.getNbt().contains(TAG_BOOK)) {
			return null;
		}

		String bookStr = stack.getNbt().getString(TAG_BOOK);
		return Identifier.tryParse(bookStr);
	}

	/* TODO fabric
	@Override
	public String getCreatorModId(ItemStack itemStack) {
		Book book = getBook(itemStack);
		if (book != null) {
			return book.owner.getModId();
		}
	
		return super.getCreatorModId(itemStack);
	}
	*/

	@Override
	public Component getName(ItemStack stack) {
		Book book = getBook(stack);
		if (book != null) {
			return new TranslatableComponent(book.name);
		}

		return super.getName(stack);
	}

	@Override
	public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);

		ResourceLocation rl = getBookId(stack);
		if (flagIn.isAdvanced()) {
			tooltip.add(new TextComponent("Book ID: " + rl).withStyle(ChatFormatting.GRAY));
		}

		Book book = getBook(stack);
		if (book != null && !book.getContents().isErrored()) {
			tooltip.add(book.getSubtitle().withStyle(ChatFormatting.GRAY));
		} else if (book == null) {
			if (rl == null) {
				tooltip.add(new TranslatableComponent("item.patchouli.guide_book.undefined")
						.withStyle(ChatFormatting.DARK_GRAY));
			} else {
				tooltip.add(new TranslatableComponent("item.patchouli.guide_book.invalid", rl)
						.withStyle(ChatFormatting.DARK_GRAY));
			}
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
		ItemStack stack = playerIn.getItemInHand(handIn);
		Book book = getBook(stack);
		if (book == null) {
			return new InteractionResultHolder<>(InteractionResult.FAIL, stack);
		}

		if (playerIn instanceof PlayerManager) {
			PatchouliAPI.get().openBookGUI((PlayerManager) playerIn, book.id);

			// This plays the sound to others nearby, playing to the actual opening player handled from the packet
			SoundEvent sfx = PatchouliSounds.getSound(book.openSound, PatchouliSounds.BOOK_OPEN);
			playerIn.playSound(sfx, 1F, (float) (0.7 + Math.random() * 0.4));
		}

		return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
	}

}
