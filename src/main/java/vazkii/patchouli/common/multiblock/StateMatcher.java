package vazkii.patchouli.common.multiblock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import vazkii.patchouli.api.IStateMatcher;
import vazkii.patchouli.api.TriPredicate;

import java.util.Objects;
import java.util.function.Predicate;

public class StateMatcher implements IStateMatcher {

	public static final StateMatcher ANY = displayOnly(Blocks.AIR.getDefaultState());
	public static final StateMatcher AIR = fromPredicate(Blocks.AIR.getDefaultState(), (w, p, s) -> s.isAir());

	private final BlockState displayState;
	private final TriPredicate<WorldAccess, BlockPos, BlockState> statePredicate;

	private StateMatcher(BlockState displayState, TriPredicate<WorldAccess, BlockPos, BlockState> statePredicate) {
		this.displayState = displayState;
		this.statePredicate = statePredicate;
	}

	public static StateMatcher fromPredicate(BlockState display, Predicate<BlockState> predicate) {
		return new StateMatcher(display, (world, pos, state) -> predicate.test(state));
	}

	public static StateMatcher fromPredicate(Block display, Predicate<BlockState> predicate) {
		return fromPredicate(display.getDefaultState(), predicate);
	}

	public static StateMatcher fromPredicate(BlockState display, TriPredicate<WorldAccess, BlockPos, BlockState> predicate) {
		return new StateMatcher(display, predicate);
	}

	public static StateMatcher fromPredicate(Block display, TriPredicate<WorldAccess, BlockPos, BlockState> predicate) {
		return new StateMatcher(display.getDefaultState(), predicate);
	}

	public static StateMatcher fromState(BlockState displayState, boolean strict) {
		return fromPredicate(displayState,
				strict ? ((state) -> state == displayState)
						: ((state) -> state.getBlock() == displayState.getBlock()));
	}

	public static StateMatcher fromStateWithFilter(BlockState state, Predicate<Property<?>> filter) {
		return fromPredicate(state, state1 -> {
			if (state.getBlock() != state1.getBlock()) {
				return false;
			}
			return state1.getProperties()
					.stream()
					.filter(filter)
					.allMatch(property -> state1.contains(property) &&
							state.contains(property) &&
							Objects.equals(state.get(property), state1.get(property)));
		});
	}

	public static StateMatcher fromState(BlockState displayState) {
		return fromState(displayState, true);
	}

	public static StateMatcher fromBlockLoose(Block block) {
		return fromState(block.getDefaultState(), false);
	}

	public static StateMatcher fromBlockStrict(Block block) {
		return fromState(block.getDefaultState(), true);
	}

	public static StateMatcher displayOnly(BlockState state) {
		return new StateMatcher(state, (w, p, s) -> true);
	}

	public static StateMatcher displayOnly(Block block) {
		return displayOnly(block.getDefaultState());
	}

	@Override
	public BlockState getDisplayedState(int ticks) {
		return displayState;
	}

	@Override
	public TriPredicate<WorldAccess, BlockPos, BlockState> getStatePredicate() {
		return statePredicate;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		StateMatcher that = (StateMatcher) o;
		return statePredicate.equals(that.statePredicate);
	}

	@Override
	public int hashCode() {
		return Objects.hash(statePredicate);
	}
}
