package vazkii.patchouli.common.base;

import com.google.common.base.Preconditions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import org.apache.commons.io.IOUtils;

import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.api.IStateMatcher;
import vazkii.patchouli.api.IStyleStack;
import vazkii.patchouli.api.PatchouliAPI.IPatchouliAPI;
import vazkii.patchouli.client.book.BookContents;
import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.template.BookTemplate;
import vazkii.patchouli.client.book.text.BookTextParser;
import vazkii.patchouli.client.handler.MultiblockVisualizationHandler;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.item.ItemModBook;
import vazkii.patchouli.common.multiblock.DenseMultiblock;
import vazkii.patchouli.common.multiblock.MultiblockRegistry;
import vazkii.patchouli.common.multiblock.SparseMultiblock;
import vazkii.patchouli.common.multiblock.StateMatcher;
import vazkii.patchouli.xplat.IXplatAbstractions;

import javax.annotation.Nonnull;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class PatchouliAPIImpl implements IPatchouliAPI {

	private static void assertPhysicalClient() {
		Preconditions.checkState(
				IXplatAbstractions.INSTANCE.isPhysicalClient(),
				"Not on the physical client"
		);
	}

	@Override
	public boolean isStub() {
		return false;
	}

	@Override
	public void setConfigFlag(String flag, boolean value) {
		PatchouliConfig.setFlag(flag, value);
	}

	@Override
	public boolean getConfigFlag(String flag) {
		return PatchouliConfig.getConfigFlag(flag);
	}

	@Override
	public void openBookGUI(ServerPlayer player, ResourceLocation book) {
		IXplatAbstractions.INSTANCE.sendOpenBookGui(player, book, null, 0);
	}

	@Override
	public void openBookEntry(ServerPlayer player, ResourceLocation book, ResourceLocation entry, int page) {
		IXplatAbstractions.INSTANCE.sendOpenBookGui(player, book, entry, page);
	}

	@Override
	public void openBookGUI(ResourceLocation book) {
		assertPhysicalClient();
		ClientBookRegistry.INSTANCE.displayBookGui(book, null, 0);
	}

	@Override
	public void openBookEntry(ResourceLocation book, ResourceLocation entry, int page) {
		assertPhysicalClient();
		ClientBookRegistry.INSTANCE.displayBookGui(book, entry, page);
	}

	@Override
	public ResourceLocation getOpenBookGui() {
		assertPhysicalClient();
		Screen gui = Minecraft.getInstance().screen;
		if (gui instanceof GuiBook) {
			return ((GuiBook) gui).book.id;
		}
		return null;
	}

	@Nonnull
	@Override
	public Component getSubtitle(@Nonnull ResourceLocation bookId) {
		Book book = BookRegistry.INSTANCE.books.get(bookId);
		if (book == null) {
			throw new IllegalArgumentException("Book not found: " + bookId);
		}
		return book.getSubtitle();
	}

	@Override
	public void registerCommand(String name, Function<IStyleStack, String> command) {
		assertPhysicalClient();
		BookTextParser.register(command::apply, name);
	}

	@Override
	public void registerFunction(String name, BiFunction<String, IStyleStack, String> function) {
		assertPhysicalClient();
		BookTextParser.register(function::apply, name);
	}

	@Override
	public ItemStack getBookStack(ResourceLocation book) {
		return ItemModBook.forBook(book);
	}

	@Override
	public void registerTemplateAsBuiltin(ResourceLocation res, Supplier<InputStream> streamProvider) {
		assertPhysicalClient();
		InputStream testStream = streamProvider.get();
		if (testStream == null) {
			throw new NullPointerException("Stream provider can't return a null stream");
		}
		IOUtils.closeQuietly(testStream);

		Supplier<BookTemplate> prev = BookContents.addonTemplates.put(res, () -> {
			InputStream stream = streamProvider.get();
			InputStreamReader reader = new InputStreamReader(stream);
			return ClientBookRegistry.INSTANCE.gson.fromJson(reader, BookTemplate.class);
		});

		if (prev != null) {
			throw new IllegalArgumentException("Template " + res + " is already registered");
		}
	}

	@Override
	public IMultiblock getMultiblock(ResourceLocation res) {
		return MultiblockRegistry.MULTIBLOCKS.get(res);
	}

	@Override
	public IMultiblock registerMultiblock(ResourceLocation res, IMultiblock mb) {
		return MultiblockRegistry.registerMultiblock(res, mb);
	}

	@Override
	public IMultiblock getCurrentMultiblock() {
		assertPhysicalClient();
		return MultiblockVisualizationHandler.hasMultiblock ? MultiblockVisualizationHandler.getMultiblock() : null;
	}

	@Override
	public void showMultiblock(@Nonnull IMultiblock multiblock, @Nonnull Component displayName, @Nonnull BlockPos center, @Nonnull Rotation rotation) {
		assertPhysicalClient();
		MultiblockVisualizationHandler.setMultiblock(multiblock, displayName, null, false);
		MultiblockVisualizationHandler.anchorTo(center, rotation);
	}

	@Override
	public void clearMultiblock() {
		assertPhysicalClient();
		MultiblockVisualizationHandler.setMultiblock(null, null, null, false);
	}

	@Override
	public IMultiblock makeMultiblock(String[][] pattern, Object... targets) {
		return new DenseMultiblock(pattern, targets);
	}

	@Override
	public IMultiblock makeSparseMultiblock(Map<BlockPos, IStateMatcher> positions) {
		return new SparseMultiblock(positions);
	}

	@Override
	public IStateMatcher predicateMatcher(BlockState display, Predicate<BlockState> predicate) {
		return StateMatcher.fromPredicate(display, predicate);
	}

	@Override
	public IStateMatcher predicateMatcher(Block display, Predicate<BlockState> predicate) {
		return StateMatcher.fromPredicate(display, predicate);
	}

	@Override
	public IStateMatcher stateMatcher(BlockState state) {
		return StateMatcher.fromState(state);
	}

	@Override
	public IStateMatcher propertyMatcher(BlockState state, Property<?>... properties) {
		return StateMatcher.fromStateWithFilter(state, Arrays.asList(properties)::contains);
	}

	@Override
	public IStateMatcher looseBlockMatcher(Block block) {
		return StateMatcher.fromBlockLoose(block);
	}

	@Override
	public IStateMatcher strictBlockMatcher(Block block) {
		return StateMatcher.fromBlockStrict(block);
	}

	@Override
	public IStateMatcher displayOnlyMatcher(BlockState state) {
		return StateMatcher.displayOnly(state);
	}

	@Override
	public IStateMatcher displayOnlyMatcher(Block block) {
		return StateMatcher.displayOnly(block);
	}

	@Override
	public IStateMatcher airMatcher() {
		return StateMatcher.AIR;
	}

	@Override
	public IStateMatcher anyMatcher() {
		return StateMatcher.ANY;
	}

}
