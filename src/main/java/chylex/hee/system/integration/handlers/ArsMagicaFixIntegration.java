package chylex.hee.system.integration.handlers;
import java.util.HashMap;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import chylex.hee.system.integration.IIntegrationHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ICrashCallable;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ArsMagicaFixIntegration implements IIntegrationHandler{
	@Override
	public String getModId(){
		return "arsmagica2";
	}

	@Override
	public void integrate(){
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().registerCrashCallable(new ICrashCallable(){
			@Override
			public String getLabel(){
				return "Hardcore Ender Expansion";
			}
			
			@Override
			public String call() throws Exception{
				return "CAUTION! Ars Magica 2 is not supported by HEE, if the crash is caused by a conflict of the two mods, it will very likely not be possible to fix.";
			}
		});
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void am2NullWorldConstructionWorkaround(EntityConstructing e){
		if (e.entity.worldObj == null && e.entity instanceof IScrewWithAM2){
			((IScrewWithAM2)e.entity).getExtendedPropertiesMap().remove("ArsMagicaExProps");
		}
	}
	
	public static interface IScrewWithAM2{
		HashMap<String,IExtendedEntityProperties> getExtendedPropertiesMap();
	}
}
