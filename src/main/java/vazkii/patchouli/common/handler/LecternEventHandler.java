package vazkii.patchouli.common.handler;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.PlayerManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.util.ItemStackUtil;

public class LecternEventHandler {

	public static InteractionResult rightClick(Player player, Level world, InteractionHand hand, BlockHitResult hit) {
		BlockPos pos = hit.getBlockPos();
		BlockState state = world.getBlockState(pos);
		BlockEntity tileEntity = world.getBlockEntity(pos);
		if (tileEntity instanceof LecternBlockEntity lectern) {
			if (state.getValue(LecternBlock.HAS_BOOK)) {
				if (player.isSecondaryUseActive()) {
					takeBook(player, lectern);
				} else {
					Book book = ItemStackUtil.getBookFromStack(lectern.getBook());
					if (book != null) {
						if (!world.isClientSide) {
							PatchouliAPI.get().openBookGUI((PlayerManager) player, book.id);
						}
						return InteractionResult.SUCCESS;
					}
				}
			} else {
				ItemStack stack = player.getItemInHand(hand);
				if (ItemStackUtil.getBookFromStack(stack) != null) {
					if (LecternBlock.tryPlaceBook(player, world, pos, state, stack)) {
						return InteractionResult.SUCCESS;
					}
				}
			}
		}
		return InteractionResult.PASS;
	}

	private static void takeBook(Player player, LecternBlockEntity tileEntity) {
		ItemStack itemstack = tileEntity.getBook();
		tileEntity.setBook(ItemStack.EMPTY);
		LecternBlock.resetBookState(tileEntity.getLevel(), tileEntity.getBlockPos(), tileEntity.getBlockState(), false);
		if (!player.getInventory().add(itemstack)) {
			player.drop(itemstack, false);
		}
	}
}
