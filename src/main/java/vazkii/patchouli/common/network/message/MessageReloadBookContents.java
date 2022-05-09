package vazkii.patchouli.common.network.message;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.common.base.Patchouli;

public class MessageReloadBookContents {
	public static final Identifier ID = new Identifier(Patchouli.MOD_ID, "reload_books");

	public static void sendToAll(MinecraftServer server) {
		PlayerLookup.all(server).forEach(MessageReloadBookContents::send);
	}

	public static void send(ServerPlayerEntity player) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.EMPTY_BUFFER);
		ServerPlayNetworking.send(player, ID, buf);
	}

	public static void handle(MinecraftClient client, ClientPlayPacketListener handler, PacketByteBuf buf, PacketSender responseSender) {
		client.submit(() -> ClientBookRegistry.INSTANCE.reload(false));
	}
}
