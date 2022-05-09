package vazkii.patchouli.client.book.template.component;

import com.google.gson.annotations.SerializedName;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
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
	transient Function<World, Entity> creator;
	transient float renderScale, offset;

	@Override
	public void build(BookContentsBuilder builder, BookPage page, BookEntry entry, int pageNum) {
		creator = EntityUtil.loadEntity(entityId.asString());
	}

	@Override
	public void onDisplayed(BookPage page, GuiBookEntry parent, int left, int top) {
		loadEntity(page.mc.world);
	}

	@Override
	public void render(MatrixStack ms, BookPage page, int mouseX, int mouseY, float pticks) {
		if (errored) {
			page.textRenderer.drawWithShadow(ms, I18n.translate("patchouli.gui.lexicon.loading_error"), x, y, 0xFF0000);
		}

		if (entity != null) {
			float rotation = rotate ? ClientTicker.total : defaultRotation;
			PageEntity.renderEntity(ms, entity, page.mc.world, x, y, rotation, renderScale, offset);
		}
	}

	@Override
	public void onVariablesAvailable(UnaryOperator<IVariable> lookup) {
		super.onVariablesAvailable(lookup);
		entityId = lookup.apply(entityId);
	}

	private void loadEntity(World world) {
		if (!errored && (entity == null || !entity.isAlive())) {
			try {
				entity = creator.apply(world);
				float width = entity.getWidth();
				float height = entity.getHeight();

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
