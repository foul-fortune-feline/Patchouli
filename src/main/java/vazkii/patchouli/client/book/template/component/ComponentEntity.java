package vazkii.patchouli.client.book.template.component;

import com.google.gson.annotations.SerializedName;
import com.mojang.blaze3d.vertex.MatrixStack;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.book.BookContentsBuilder;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.client.book.page.PageEntity;
import vazkii.patchouli.client.book.template.TemplateComponent;
import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.util.EntityUtil;

import java.util.function.Function;
import java.util.function.UnaryOperator;

public class ComponentEntity extends TemplateComponent {

	@SerializedName("entity") public IVariable entityId;

	@SerializedName("render_size") float renderSize = 100;

	boolean rotate = true;
	@SerializedName("default_rotation") float defaultRotation = -45f;

	transient boolean errored;
	transient Entity entity;
	transient Function<Level, Entity> creator;
	transient float renderScale, offset;

	@Override
	public void build(BookContentsBuilder builder, BookPage page, BookEntry entry, int pageNum) {
		creator = EntityUtil.loadEntity(entityId.asString());
	}

	@Override
	public void onDisplayed(BookPage page, GuiBookEntry parent, int left, int top) {
		loadEntity(page.mc.level);
	}

	@Override
	public void render(MatrixStack ms, BookPage page, int mouseX, int mouseY, float pticks) {
		if (errored) {
			page.fontRenderer.drawShadow(ms, I18n.get("patchouli.gui.lexicon.loading_error"), x, y, 0xFF0000);
		}

		if (entity != null) {
			float rotation = rotate ? ClientTicker.total : defaultRotation;
			PageEntity.renderEntity(ms, entity, page.mc.level, x, y, rotation, renderScale, offset);
		}
	}

	@Override
	public void onVariablesAvailable(UnaryOperator<IVariable> lookup) {
		super.onVariablesAvailable(lookup);
		entityId = lookup.apply(entityId);
	}

	private void loadEntity(Level world) {
		if (!errored && (entity == null || !entity.isAlive())) {
			try {
				entity = creator.apply(world);
				float width = entity.getBbWidth();
				float height = entity.getBbHeight();

				float entitySize = Math.max(width, height);
				entitySize = Math.max(1F, entitySize);

				renderScale = renderSize / entitySize * 0.8F;
				offset = Math.max(height, entitySize) * 0.5F;
			} catch (Exception e) {
				errored = true;
				Patchouli.LOGGER.error("Failed to load entity", e);
			}
		}
	}

}
