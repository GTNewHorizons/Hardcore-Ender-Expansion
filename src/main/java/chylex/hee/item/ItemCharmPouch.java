package chylex.hee.item;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.api.expanded.BaubleItemHelper;
import baubles.api.expanded.IBaubleExpanded;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.init.ItemList;
import chylex.hee.mechanics.charms.CharmPouchInfo;
import chylex.hee.mechanics.charms.handler.CharmPouchHandler;
import chylex.hee.mechanics.charms.handler.CharmPouchHandlerClient;
import chylex.hee.system.achievements.AchievementManager;
import chylex.hee.system.integration.ModIntegrationManager;
import chylex.hee.system.integration.handlers.BaublesExpandedIntegration;
import chylex.hee.system.util.ItemUtil;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Optional.InterfaceList({
        @Optional.Interface(iface = "baubles.api.expanded.IBaubleExpanded", modid = "Baubles|Expanded"),
        @Optional.Interface(iface = "baubles.api.IBauble", modid = "Baubles") })
public class ItemCharmPouch extends Item implements IBauble, IBaubleExpanded {

    @Override
    public void onUpdate(ItemStack is, World world, Entity entity, int slot, boolean isHeld) {
        if (!ItemUtil.getTagRoot(is, false).getBoolean("isPouchActive") || !(entity instanceof EntityPlayer)) {
            return;
        }

        if (world.isRemote) {
            CharmPouchHandlerClient.onActivePouchUpdate((EntityPlayer) entity, is);
        } else {
            CharmPouchInfo pouchInfo = CharmPouchHandler.getActivePouch((EntityPlayer) entity);

            if (pouchInfo == null) {
                CharmPouchHandler.setActivePouch((EntityPlayer) entity, is);
                CharmPouchHandler.getActivePouch((EntityPlayer) entity).update(world);
            } else if (pouchInfo.pouchID != getPouchID(is)) {
                is.getTagCompound().setBoolean("isPouchActive", false);
            } else {
                pouchInfo.update(world);
            }
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player) {
        if (world.isRemote) return is;

        if (player.isSneaking()) {
            CharmPouchInfo activePouch = CharmPouchHandler.getActivePouch(player);
            boolean deactivate = activePouch != null && activePouch.pouchID == getPouchID(is);

            CharmPouchHandler.setActivePouch(player, deactivate ? null : is);
            ItemUtil.getTagRoot(is, true).setBoolean("isPouchActive", !deactivate);
            if (!deactivate) CharmPouchHandler.getActivePouch(player).update(world);
        } else {
            player.openGui(HardcoreEnderExpansion.instance, 5, world, 0, 0, 0);
        }

        return is;
    }

    @Override
    public boolean onEntityItemUpdate(EntityItem entity) {
        ItemStack is = entity.getEntityItem();
        if (is == null || !is.hasTagCompound()) return false;

        if (is.getTagCompound().getBoolean("isPouchActive")) is.getTagCompound().setBoolean("isPouchActive", false);
        return false;
    }

    @Override
    public void onCreated(ItemStack is, World world, EntityPlayer player) {
        player.addStat(AchievementManager.CHARM_POUCH, 1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack is, EntityPlayer player, List<String> textLines, boolean showAdvancedInfo) {
        textLines.add(
                I18n.format(
                        ItemUtil.getTagRoot(is, false).getBoolean("isPouchActive") ? "item.charmPouch.info.active"
                                : "item.charmPouch.info.inactive"));
        if (ModIntegrationManager.baublesLoaded) addBaubleInformation(textLines);
    }

    @SideOnly(Side.CLIENT)
    @Optional.Method(modid = "Baubles")
    private void addBaubleInformation(List<String> textLines) {
        if (ModIntegrationManager.baublesExpandedLoaded) {
            BaubleItemHelper.addSlotInformation(textLines, getBaubleTypes(null));
        } else {
            textLines.add(StatCollector.translateToLocal("baubletype.belt"));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack is, int pass) {
        return ItemUtil.getTagRoot(is, false).getBoolean("isPouchActive");
    }

    public static long getPouchID(ItemStack is) {
        if (is.getItem() != ItemList.charm_pouch) return 0;

        NBTTagCompound nbt = ItemUtil.getTagRoot(is, true);

        long id = nbt.getLong("pouchID");
        if (id == 0) nbt.setLong("pouchID", id = itemRand.nextLong());
        return id;
    }

    public static ItemStack[] getPouchCharms(ItemStack is) {
        if (is.getItem() != ItemList.charm_pouch) return new ItemStack[0];

        NBTTagList tagCharms = ItemUtil.getTagRoot(is, true).getTagList("pouchCharms", Constants.NBT.TAG_COMPOUND);

        ItemStack[] items = new ItemStack[tagCharms.tagCount()];

        for (int a = 0; a < tagCharms.tagCount(); a++) {
            NBTTagCompound tag = tagCharms.getCompoundTagAt(a);
            items[a] = tag.getBoolean("null") ? null : ItemStack.loadItemStackFromNBT(tag);
        }

        return items;
    }

    public static void setPouchCharms(ItemStack pouch, ItemStack[] charms) {
        if (pouch.getItem() != ItemList.charm_pouch) return;

        NBTTagList tagCharms = new NBTTagList();

        for (ItemStack charm : charms) {
            if (charm == null) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setBoolean("null", true);
                tagCharms.appendTag(tag);
            } else {
                tagCharms.appendTag(charm.writeToNBT(new NBTTagCompound()));
            }
        }

        ItemUtil.getTagRoot(pouch, true).setTag("pouchCharms", tagCharms);
    }

    @Override
    @Optional.Method(modid = "Baubles|Expanded")
    public String[] getBaubleTypes(ItemStack itemstack) {
        return new String[] { BaublesExpandedIntegration.BAUBLESLOT };
    }

    // Fallback for base Baubles
    @Override
    @Optional.Method(modid = "Baubles")
    public BaubleType getBaubleType(ItemStack itemStack) {
        return BaubleType.BELT;
    }

    @Override
    @Optional.Method(modid = "Baubles")
    public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
        onUpdate(itemstack, player.worldObj, player, 0, false);
    }

    @Override
    @Optional.Method(modid = "Baubles")
    public void onEquipped(ItemStack itemstack, EntityLivingBase player) {
        EntityPlayer entityPlayer = (EntityPlayer) player;
        CharmPouchHandler.setActivePouch(entityPlayer, itemstack);
        ItemUtil.getTagRoot(itemstack, true).setBoolean("isPouchActive", true);
        CharmPouchHandler.getActivePouch(entityPlayer).update(player.worldObj);
    }

    // Should not be toggled off because it still can be active in the inventory
    @Override
    @Optional.Method(modid = "Baubles")
    public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {}

    @Override
    @Optional.Method(modid = "Baubles")
    public boolean canEquip(ItemStack itemstack, EntityLivingBase player) {
        return true;
    }

    @Override
    @Optional.Method(modid = "Baubles")
    public boolean canUnequip(ItemStack itemstack, EntityLivingBase player) {
        return true;
    }
}
