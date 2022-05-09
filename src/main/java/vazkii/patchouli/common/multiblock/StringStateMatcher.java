package vazkii.patchouli.common.multiblock;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Either;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.argument.BlockArgumentParser;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.WorldAccess;
import vazkii.patchouli.api.IStateMatcher;
import vazkii.patchouli.api.TriPredicate;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

public class StringStateMatcher {
	public static IStateMatcher fromString(String s) throws CommandSyntaxException {
		s = s.trim();
		if (s.equals("ANY")) {
			return StateMatcher.ANY;
		}
		if (s.equals("AIR")) {
			return StateMatcher.AIR;
		}

		// c.f. BlockPredicateArgumentType. Similar, but doesn't use vanilla's weird caching class.
		Either<BlockArgumentParser.BlockResult, BlockArgumentParser.TagResult> parser = BlockArgumentParser.blockOrTag(Registry.BLOCK, new StringReader(s), true);

		if (parser.left().isPresent()) {
			BlockArgumentParser.BlockResult blockResult = parser.left().get();
			return new ExactMatcher(blockResult.blockState(), blockResult.properties());
		} else {
			BlockArgumentParser.TagResult tagResult = parser.right().get();
			return new TagMatcher(tagResult.tag(), tagResult.vagueProperties());
		}
	}

	private static class ExactMatcher implements IStateMatcher {
		private final BlockState state;
		private final Map<Property<?>, Comparable<?>> props;

		private ExactMatcher(BlockState state, Map<Property<?>, Comparable<?>> props) {
			this.state = state;
			this.props = props;
		}

		@Override
		public BlockState getDisplayedState(int ticks) {
			return state;
		}

		@Override
		public TriPredicate<WorldAccess, BlockPos, BlockState> getStatePredicate() {
			return (w, p, s) -> state.getBlock() == s.getBlock() && checkProps(s);
		}

		private boolean checkProps(BlockState state) {
			for (Entry<Property<?>, Comparable<?>> e : props.entrySet()) {
				if (!state.get(e.getKey()).equals(e.getValue())) {
					return false;
				}
			}
			return true;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			ExactMatcher that = (ExactMatcher) o;
			return Objects.equals(state, that.state) &&
					Objects.equals(props, that.props);
		}

		@Override
		public int hashCode() {
			return Objects.hash(state, props);
		}
	}

	private static class TagMatcher implements IStateMatcher {
		private final List<Block> tag;
		private final Map<String, String> props;

		private TagMatcher(RegistryEntryList<Block> tag, Map<String, String> props) {
			this.tag = tag.stream().map(RegistryEntry::value).toList();
			this.props = props;
		}

		@Override
		public BlockState getDisplayedState(int ticks) {
			if (tag.isEmpty()) {
				return Blocks.BEDROCK.getDefaultState(); // show something impossible
			} else {
				int idx = (ticks / 20) % tag.size();
				return tag.get(idx).getDefaultState();
			}
		}

		@Override
		public TriPredicate<WorldAccess, BlockPos, BlockState> getStatePredicate() {
			return (w, p, s) -> tag.contains(s.getBlock()) && checkProps(s);
		}

		private boolean checkProps(BlockState state) {
			for (Entry<String, String> entry : props.entrySet()) {
				Property<?> prop = state.getBlock().getStateManager().getProperty(entry.getKey());
				if (prop == null) {
					return false;
				}

				Comparable<?> value = prop.parse(entry.getValue()).orElse(null);
				if (value == null) {
					return false;
				}

				if (!state.get(prop).equals(value)) {
					return false;
				}
			}
			return true;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			TagMatcher that = (TagMatcher) o;
			return Objects.equals(tag, that.tag) &&
					Objects.equals(props, that.props);
		}

		@Override
		public int hashCode() {
			return Objects.hash(tag, props);
		}
	}
}
