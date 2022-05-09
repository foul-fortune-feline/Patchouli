package vazkii.patchouli.common.base;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestRunner;

public class PatchouliSmokeTest {
	@GameTest(structureName = FabricGameTest.EMPTY_STRUCTURE)
	public void doesItRun(TestRunner helper) {
		helper.run();
	}
}
