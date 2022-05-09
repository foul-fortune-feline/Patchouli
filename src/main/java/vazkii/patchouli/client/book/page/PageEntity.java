package vazkii.patchouli.client.book.page;

import com.google.gson.annotations.SerializedName;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;
import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.book.BookContentsBuilder;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.client.book.page.abstr.PageWithText;
import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.util.EntityUtil;

import java.util.function.Function;

public class PageEntity extends PageWithText {

	@SerializedName("entity") public String entityId;

	float scale = 1F;
	@SerializedName("offset") float extraOffset = 0F;
	String name;

	boolean rotate = true;
	@SerializedName("default_rotation") float defaultRotation = -45f;

	transient boolean errored;
	transient Entity entity;
	transient Function<World, Entity> creator;
	transient float renderScale, offset;

	@Override
	public void build(BookEntry entry, BookContentsBuilder builder, int pageNum) {
		super.build(entry, builder, pageNum);

		creator = EntityUtil.loadEntity(entityId);
	}

	@Override
	public void onDisplayed(GuiBookEntry parent, int left, int top) {
		super.onDisplayed(parent, left, top);

		loadEntity(parent.getMinecraft().world);
	}

	@Override
	public int getTextHeight() {
		return 115;
	}

	@Override
	public void render(MatrixStack ms, int mouseX, int mouseY, float pticks) {
		int x = GuiBook.PAGE_WIDTH / 2 - 53;
		int y = 7;
		RenderSystem.enableBlend();
		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
		GuiBook.drawFromTexture(ms, book, x, y, 405, 149, 106, 106);

		if (name == null || name.isEmpty()) {
			if (entity != null) {
				parent.drawCenteredStringNoShadow(ms, entity.getName(), GuiBook.PAGE_WIDTH / 2, 0, book.headerColor);
			}
		} else {
			parent.drawCenteredStringNoShadow(ms, name, GuiBook.PAGE_WIDTH / 2, 0, book.headerColor);
		}

		if (errored) {
			textRenderer.drawWithShadow(ms, I18n.translate("patchouli.gui.lexicon.loading_error"), 58, 60, 0xFF0000);
		}

		if (entity != null) {
			float rotation = rotate ? ClientTicker.total : defaultRotation;
			renderEntity(ms, entity, parent.getMinecraft().world, 58, 60, rotation, renderScale, offset);
		}

		super.render(ms, mouseX, mouseY, pticks);
	}

	public static void renderEntity(MatrixStack ms, Entity entity, World world, float x, float y, float rotation, float renderScale, float offset) {
		entity.world = world;

		ms.push();
		ms.translate(x, y, 50);
		ms.scale(renderScale, renderScale, renderScale);
		ms.translate(0, offset, 0);
		ms.multiply(new Quaternion(Vec3f.POSITIVE_Z, 180, true));
		ms.multiply(new Quaternion(Vec3f.POSITIVE_Y, rotation, true));
		EntityRenderDispatcher erd = MinecraftClient.getInstance().getEntityRenderDispatcher();
		VertexConsumerProvider immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
		erd.setRenderShadows(false);
		erd.render(entity, 0, 0, 0, 0, 1, ms, immediate, 0xF000F0);
		erd.setRenderShadows(true);
//		immediate.endBatch();
		ms.pop();
	}

	private void loadEntity(World world) {
		if (!errored && (entity == null || !entity.isAlive())) {
			try {
				entity = creator.apply(world);

				float width = entity.getWidth();
				float height = entity.getHeight();

				float entitySize = Math.max(1F, Math.max(width, height));

				renderScale = 100F / entitySize * 0.8F * scale;
				offset = Math.max(height, entitySize) * 0.5F + extraOffset;
			} catch (Exception e) {
				errored = true;
				Patchouli.LOGGER.error("Failed to load entity", e);
			}
		}
	}

}
