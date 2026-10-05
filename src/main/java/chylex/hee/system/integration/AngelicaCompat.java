package chylex.hee.system.integration;

import com.gtnewhorizons.angelica.api.EnderDragonBeams;

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
}
