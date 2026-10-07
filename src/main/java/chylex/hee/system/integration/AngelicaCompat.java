package chylex.hee.system.integration;

import com.gtnewhorizons.angelica.api.EnderDragonBeams;
import com.gtnewhorizons.angelica.api.EyePassRenderer;

import chylex.hee.render.entity.RenderBossDragon;
import chylex.hee.render.entity.RenderMobAngryEnderman;
import chylex.hee.render.entity.RenderMobBabyEnderman;
import chylex.hee.render.entity.RenderMobHomelandEnderman;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class AngelicaCompat {

    private AngelicaCompat() {}

    public static void beginCrystalBeam() {
        EnderDragonBeams.beginCrystalBeam();
    }

    public static void endCrystalBeam() {
        EnderDragonBeams.endCrystalBeam();
    }

    public static void beginDeathRays() {
        EnderDragonBeams.beginDeathRays();
    }

    public static void endDeathRays() {
        EnderDragonBeams.endDeathRays();
    }

    /*
     * Angelica related compat.
     */

    public static RenderBossDragon createDragonRenderer() {
        return new DragonRenderer();
    }

    public static RenderMobAngryEnderman createAngryEndermanRenderer() {
        return new AngryEndermanRenderer();
    }

    public static RenderMobBabyEnderman createBabyEndermanRenderer() {
        return new BabyEndermanRenderer();
    }

    public static RenderMobHomelandEnderman createHomelandEndermanRenderer() {
        return new HomelandEndermanRenderer();
    }

    static final class DragonRenderer extends RenderBossDragon implements EyePassRenderer {
    }

    static final class AngryEndermanRenderer extends RenderMobAngryEnderman implements EyePassRenderer {
    }

    static final class BabyEndermanRenderer extends RenderMobBabyEnderman implements EyePassRenderer {
    }

    static final class HomelandEndermanRenderer extends RenderMobHomelandEnderman implements EyePassRenderer {
    }
}
