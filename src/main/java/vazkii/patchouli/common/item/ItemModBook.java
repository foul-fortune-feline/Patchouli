package vazkii.patchouli.common.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.*;
import net.minecraft.util.*;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.common.base.PatchouliSounds;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;

import javax.annotation.Nullable;
import java.util.List;

public class ItemModBook extends Item {

	private static final String TAG_BOOK = "patchouli:book";

	public ItemModBook() {
		super(new Item.Settings()
				.maxCount(1)
				.group(ItemGroup.MISC));
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

	public static ItemStack forBook(Identifier book) {
		ItemStack stack = new ItemStack(PatchouliItems.BOOK);

		NbtCompound cmp = new NbtCompound();
		cmp.putString(TAG_BOOK, book.toString());
		stack.setNbt(cmp);

		return stack;
	}

	@Override
	public void appendStacks(ItemGroup tab, DefaultedList<ItemStack> items) {
		String tabName = tab.getName();
		BookRegistry.INSTANCE.books.values().forEach(b -> {
			if (!b.noBook && !b.isExtension && (tab == ItemGroup.SEARCH || b.creativeTab.equals(tabName))) {
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
	public Text getName(ItemStack stack) {
		Book book = getBook(stack);
		if (book != null) {
			return MutableText.of(new TranslatableTextContent(book.name));
		}

		return super.getName(stack);
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World worldIn, List<Text> tooltip, TooltipContext flagIn) {
		super.appendTooltip(stack, worldIn, tooltip, flagIn);

		Identifier rl = getBookId(stack);
		if (flagIn.isAdvanced()) {
			tooltip.add(MutableText.of(new LiteralTextContent("Book ID: " + rl))
					.setStyle(Style.EMPTY.withFormatting(Formatting.GRAY)));
		}

		Book book = getBook(stack);
		if (book != null && !book.getContents().isErrored()) {
			tooltip.add(book.getSubtitle().setStyle(Style.EMPTY.withFormatting(Formatting.GRAY)));
		} else if (book == null) {
			if (rl == null) {
				tooltip.add(MutableText.of(new TranslatableTextContent("item.patchouli.guide_book.undefined"))
						.setStyle(Style.EMPTY.withFormatting(Formatting.DARK_GRAY)));
			} else {
				tooltip.add(MutableText.of(new TranslatableTextContent("item.patchouli.guide_book.invalid", rl))
						.setStyle(Style.EMPTY.withFormatting(Formatting.DARK_GRAY)));
			}
		}
	}

	@Override
	public TypedActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
		ItemStack stack = playerIn.getStackInHand(handIn);
		Book book = getBook(stack);
		if (book == null) {
			return new TypedActionResult<>(ActionResult.FAIL, stack);
		}

		if (playerIn instanceof ServerPlayerEntity) {
			PatchouliAPI.get().openBookGUI((ServerPlayerEntity) playerIn, book.id);

			// This plays the sound to others nearby, playing to the actual opening player handled from the packet
			SoundEvent sfx = PatchouliSounds.getSound(book.openSound, PatchouliSounds.BOOK_OPEN);
			playerIn.playSound(sfx, 1F, (float) (0.7 + Math.random() * 0.4));
		}

		return new TypedActionResult<>(ActionResult.SUCCESS, stack);
	}

}
