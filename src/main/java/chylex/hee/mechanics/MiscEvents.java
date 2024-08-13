package chylex.hee.mechanics;

import java.util.ArrayList;
import java.util.Arrays;

import javax.annotation.Nonnull;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;

import com.kuba6000.mobsinfo.api.IChanceModifier;
import com.kuba6000.mobsinfo.api.IMobExtraInfoProvider;
import com.kuba6000.mobsinfo.api.MobDrop;
import com.kuba6000.mobsinfo.api.MobRecipe;

import chylex.hee.entity.item.EntityItemDragonEgg;
import chylex.hee.init.ItemList;
import chylex.hee.item.ItemTransferenceGem;
import chylex.hee.mechanics.enhancements.EnhancementHandler;
import chylex.hee.mechanics.enhancements.types.TransferenceGemEnhancements;
import chylex.hee.system.util.ItemUtil;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@Optional.Interface(iface = "com.kuba6000.mobsinfo.api.IMobExtraInfoProvider", modid = "mobsinfo")
public class MiscEvents implements IMobExtraInfoProvider {

    /*
     * Dragon Egg entity join world
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEntityJoinWorld(EntityJoinWorldEvent e) {
        if (!e.world.isRemote && e.entity.getClass() == EntityItem.class
                && ((EntityItem) e.entity).getEntityItem().getItem() == Item.getItemFromBlock(Blocks.dragon_egg)) {
            e.setCanceled(true);

            EntityItem newEntity = new EntityItemDragonEgg(
                    e.world,
                    e.entity.posX,
                    e.entity.posY,
                    e.entity.posZ,
                    ((EntityItem) e.entity).getEntityItem());
            newEntity.delayBeforeCanPickup = 10;

            newEntity.copyLocationAndAnglesFrom(e.entity);
            newEntity.motionX = newEntity.motionY = newEntity.motionZ = 0D;
            newEntity.addVelocity(e.entity.motionX, e.entity.motionY, e.entity.motionZ);
            e.world.spawnEntityInWorld(newEntity);
        }
    }

    /*
     * Endermen dropping heads Silverfish dropping blood Mobs dropping Spectral Essence and dying next to Spectral
     * Essence Altar
     */
    @SubscribeEvent
    public void onLivingDrops(LivingDropsEvent e) {
        // ANY CHANGE MADE IN HERE MUST ALSO BE MADE IN provideDropsInformation!
        if (e.entity.worldObj.isRemote /* || SpectralEssenceHandler.handleMobDeath(e) */ || !e.recentlyHit) return;

        if (e.entity.getClass() == EntitySilverfish.class
                && e.entityLiving.getRNG().nextInt(14 - Math.min(e.lootingLevel, 4)) == 0) {
            boolean drop = e.entityLiving.getRNG().nextInt(4) == 0;
            boolean isPlayer = e.source.getEntity() instanceof EntityPlayer;

            if (!drop && isPlayer) {
                ItemStack held = ((EntityPlayer) e.source.getEntity()).inventory.getCurrentItem();
                if (held != null && held.getItem() == Items.golden_sword) drop = true;
            }

            if (drop) {
                EntityItem item = new EntityItem(
                        e.entity.worldObj,
                        e.entity.posX,
                        e.entity.posY,
                        e.entity.posZ,
                        new ItemStack(ItemList.silverfish_blood));
                item.delayBeforeCanPickup = 10;
                e.drops.add(item);
            }
        }
    }

    @Optional.Method(modid = "mobsinfo")
    @Override
    public void provideExtraDropsInformation(@Nonnull String entityString, @Nonnull ArrayList<MobDrop> drops,
            @Nonnull MobRecipe recipe) {
        if (recipe.entity.getClass() == EntitySilverfish.class) {
            MobDrop drop = new MobDrop(
                    new ItemStack(ItemList.silverfish_blood),
                    MobDrop.DropType.Normal,
                    179,
                    null,
                    null,
                    false,
                    false);
            drop.variableChance = true;
            drop.chanceModifiers.addAll(
                    Arrays.asList(
                            new IChanceModifier.NormalChance(1.79d),
                            new IChanceModifier.OrUsing(Items.golden_sword, 7.14d)));
            drops.add(drop);
        }
    }

    /*
     * Right-clicking on item frame, mob and item with Transference Gem
     */
    @SubscribeEvent
    public void onPlayerInteractEntity(EntityInteractEvent e) {
        if (e.entity.worldObj.isRemote) return;

        if (e.target instanceof EntityItemFrame) {
            EntityItemFrame itemFrame = (EntityItemFrame) e.target;

            ItemStack is = itemFrame.getDisplayedItem();

            if (is == null || is.getItem() != ItemList.transference_gem || e.entityPlayer.isSneaking()) return;
            else if (EnhancementHandler.hasEnhancement(is, TransferenceGemEnhancements.TOUCH)) {
                is = ((ItemTransferenceGem) ItemList.transference_gem)
                        .tryTeleportEntity(is, e.entityPlayer, e.entityPlayer);
                ItemUtil.getTagRoot(is, false).removeTag("cooldown");

                itemFrame.setDisplayedItem(is);
                e.setCanceled(true);
            }
        }
    }
}
