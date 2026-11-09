package chylex.hee.system.integration;

import java.util.HashSet;
import java.util.Set;

import chylex.hee.system.integration.handlers.BaublesExpandedIntegration;
import chylex.hee.system.integration.handlers.MineFactoryReloadedIntegration;
import chylex.hee.system.integration.handlers.NotEnoughItemsIntegration;
import chylex.hee.system.integration.handlers.ThaumcraftIntegration;
import chylex.hee.system.logging.Log;
import chylex.hee.system.logging.Stopwatch;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.versioning.VersionParser;

public final class ModIntegrationManager {

    public static boolean baublesLoaded;
    public static boolean baublesExpandedLoaded;
    public static boolean angelicaLoaded;

    public static final Set<String> blacklistedMods = new HashSet<>();

    public static void preInit() {
        Stopwatch.time("ModIntegrationManager - preInit");
        baublesLoaded = Loader.isModLoaded("Baubles");
        baublesExpandedLoaded = Loader.isModLoaded("Baubles|Expanded");
        ModContainer angelica = Loader.instance().getIndexedModList().get("angelica");
        angelicaLoaded = angelica != null
                && VersionParser.parseRange("[2.2.30,)").containsVersion(angelica.getProcessedVersion());

        Class[] handlerClasses = new Class[] { BaublesExpandedIntegration.class };

        for (Class<? extends IIntegrationHandler> cls : handlerClasses) {
            try {
                IIntegrationHandler handler = cls.newInstance();
                String modId = handler.getModId();
                if (Loader.isModLoaded(modId) && !blacklistedMods.contains(modId)) handler.integrate();
            } catch (Throwable e) {
                Log.throwable(e, "Unable to integrate with mod $0.", cls.getSimpleName());
            }
        }

        Stopwatch.finish("ModIntegrationManager - preInit");
    }

    public static void postInit() {
        Stopwatch.time("ModIntegrationManager - postInit");

        Class[] handlerClasses = new Class[] { NotEnoughItemsIntegration.class, ThaumcraftIntegration.class,
                MineFactoryReloadedIntegration.class };

        for (Class<? extends IIntegrationHandler> cls : handlerClasses) {
            try {
                IIntegrationHandler handler = cls.newInstance();
                String modId = handler.getModId();
                if (Loader.isModLoaded(modId) && !blacklistedMods.contains(modId)) handler.integrate();
            } catch (Throwable e) {
                Log.throwable(e, "Unable to integrate with mod $0.", cls.getSimpleName());
            }
        }

        Stopwatch.finish("ModIntegrationManager - postInit");
    }
}
