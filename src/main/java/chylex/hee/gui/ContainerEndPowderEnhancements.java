package chylex.hee.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import org.apache.commons.lang3.ArrayUtils;

import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.gui.slots.SlotBasicItem;
import chylex.hee.gui.slots.SlotEnhancementsSubject;
import chylex.hee.gui.slots.SlotReadOnly;
import chylex.hee.gui.slots.SlotShowCase;
import chylex.hee.init.ItemList;
import chylex.hee.item.ItemSpecialEffects;
import chylex.hee.mechanics.compendium.content.fragments.KnowledgeFragmentEnhancement;
import chylex.hee.mechanics.compendium.events.CompendiumEvents;
import chylex.hee.mechanics.compendium.player.PlayerCompendiumData;
import chylex.hee.mechanics.enhancements.EnhancementHandler;
import chylex.hee.mechanics.enhancements.IEnhanceableTile;
import chylex.hee.mechanics.enhancements.IEnhancementEnum;
import chylex.hee.mechanics.enhancements.SlotList;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C08PlaySound;
import chylex.hee.packets.client.C19CompendiumData;
import chylex.hee.proxy.ModCommonProxy.MessageType;
import chylex.hee.system.logging.Log;
import chylex.hee.system.util.DragonUtil;
import chylex.hee.system.util.MathUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ContainerEndPowderEnhancements extends Container {

    private EntityPlayerMP owner;

    public final IInventory containerInv;
    public final IEnhanceableTile enhanceableTile;
    public final Slot[] powderSlots = new Slot[8];
    public final Slot[] ingredientSlots = new Slot[8];

    public final IInventory enhancementListInv;
    public final ItemStack[] clientEnhancementItems = new ItemStack[7];
    public final String[] clientEnhancementTooltips = new String[7];
    public final boolean[] clientEnhancementBlocked = new boolean[7];
    public final int[] enhancementSlotX = new int[7];
    private IEnhancementEnum selectedEnhancement;
    private int selectedSlot = -1, prevSelectedSlot = -1;

    public ContainerEndPowderEnhancements(InventoryPlayer inv, IEnhanceableTile tileOptional) {
        containerInv = new InventoryBasic("", false, 2 + ingredientSlots.length + powderSlots.length);
        this.enhanceableTile = tileOptional;

        enhancementListInv = new InventoryBasic("", false, enhancementSlotX.length);
        for (int a = 0; a < enhancementSlotX.length; a++) enhancementSlotX[a] = -3200;

        if (isEnhancingTile()) {
            addSlotToContainer(new SlotShowCase(containerInv, 0, 80, 17));
            containerInv.setInventorySlotContents(0, tileOptional.createEnhancedItemStack());
            addSlotToContainer(new SlotReadOnly(containerInv, 1, 3200, 17));
        } else {
            addSlotToContainer(new SlotEnhancementsSubject(this, containerInv, 0, 53, 17));
            addSlotToContainer(new SlotBasicItem(containerInv, 1, 107, 17, null));
        }

        for (int a = 0; a < powderSlots.length; a++) {
            addSlotToContainer(powderSlots[a] = new SlotBasicItem(containerInv, 2 + a, -3200, 57, ItemList.end_powder));
        }

        for (int a = 0; a < ingredientSlots.length; a++) {
            addSlotToContainer(ingredientSlots[a] = new Slot(containerInv, 2 + powderSlots.length + a, -3200, 57));
        }

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) addSlotToContainer(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 88 + i * 18));
        }

        for (int i = 0; i < 9; ++i) addSlotToContainer(new Slot(inv, i, 8 + i * 18, 146));
    }

    @Override
    public void addCraftingToCrafters(ICrafting crafter) {
        super.addCraftingToCrafters(crafter);

        if (crafter instanceof EntityPlayerMP) {
            if (owner != null)
                throw new IllegalArgumentException("Cannot add second player to End Powder Enhancement container!");
            owner = (EntityPlayerMP) crafter;
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotId) {
        ItemStack is = null;
        ItemStack mainIS = containerInv.getStackInSlot(0);
        Slot slot = (Slot) inventorySlots.get(slotId);

        if (slot != null && slot.getHasStack()) {
            ItemStack is2 = slot.getStack();
            is = is2.copy();

            if (slotId < 2 + powderSlots.length + ingredientSlots.length) {
                if (!mergeItemStack(is2, 2 + powderSlots.length + ingredientSlots.length, inventorySlots.size(), true))
                    return null;
            } else {
                int shownPowderSlots = 0, shownIngredientSlots = 0;

                if (mainIS != null && selectedEnhancement != null) {
                    SlotList slots = EnhancementHandler.getEnhancementSlotsForItem(mainIS.getItem());
                    shownPowderSlots = slots.amountPowder;
                    shownIngredientSlots = slots.amountIngredient;
                }

                if (EnhancementHandler.canEnhanceItem(is2.getItem()) && !isEnhancingTile()) {
                    if (!mergeItemStack(is2, 0, 1, false)) return null;
                } else if (shownPowderSlots > 0 && is2.getItem() == ItemList.end_powder) {
                    if (!mergeItemStack(is2, 2, 2 + shownPowderSlots, false)) return null;
                } else if (shownIngredientSlots > 0 && !mergeItemStack(
                        is2,
                        2 + powderSlots.length,
                        2 + powderSlots.length + shownIngredientSlots,
                        false))
                    return null;
                else return null;

                if (mainIS != null && selectedEnhancement == null) return null;
            }

            if (is2.stackSize == 0) slot.putStack(null);
            else slot.onSlotChanged();
        }

        return is;
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);

        for (int a = isEnhancingTile() ? 1 : 0; a < containerInv.getSizeInventory(); a++) {
            if (containerInv.getStackInSlot(a) != null) {
                player.dropPlayerItemWithRandomChoice(containerInv.getStackInSlot(a), false);
            }
        }
    }

    @Override
    public void putStackInSlot(int slot, ItemStack is) {
        super.putStackInSlot(slot, is);

        if (slot == 0 && isEnhancingTile()) {
            onSubjectChanged();
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void putStacksInSlots(ItemStack[] items) {
        super.putStacksInSlots(items);
        if (isEnhancingTile()) onSubjectChanged();
    }

    public void updateClientItems() {
        ItemStack mainIS = getSlot(0).getStack();
        List<IEnhancementEnum> enhancements = mainIS == null ? null
                : EnhancementHandler.getEnhancementsForItem(mainIS.getItem());

        for (int a = enhancements == null ? 0 : enhancements.size(); a < enhancementSlotX.length; a++) {
            clientEnhancementItems[a] = null;
            clientEnhancementTooltips[a] = "";
        }

        if (enhancements == null) return;

        PlayerCompendiumData compendiumData = mainIS != null ? HardcoreEnderExpansion.proxy.getClientCompendiumData()
                : null;

        for (int a = 0; a < enhancements.size(); a++) {
            IEnhancementEnum enhancement = enhancements.get(a);
            ItemStack is = compendiumData != null && compendiumData
                    .hasUnlockedFragment(KnowledgeFragmentEnhancement.getEnhancementFragment(enhancement))
                            ? enhancement.getItemSelector().getRepresentativeItem()
                            : new ItemStack(ItemList.special_effects, 1, ItemSpecialEffects.questionMark);
            clientEnhancementItems[a] = is;
        }
    }

    public void onSubjectChanged() {
        ItemStack mainIS = getSlot(0).getStack();

        updateClientItems();
        HardcoreEnderExpansion.proxy.sendMessage(MessageType.ENHANCEMENT_SLOT_RESET, ArrayUtils.EMPTY_INT_ARRAY);

        List<ItemStack> toDrop = new ArrayList<>();
        SlotList slots = mainIS == null ? new SlotList()
                : EnhancementHandler.getEnhancementSlotsForItem(mainIS.getItem());

        for (int a = slots.amountPowder, index; a < powderSlots.length; a++) {
            index = powderSlots[0].slotNumber + a;

            if (containerInv.getStackInSlot(index) != null) {
                toDrop.add(containerInv.getStackInSlot(index));
                containerInv.setInventorySlotContents(index, null);
            }
        }

        for (int a = slots.amountIngredient, index; a < ingredientSlots.length; a++) {
            index = ingredientSlots[0].slotNumber + a;

            if (containerInv.getStackInSlot(index) != null) {
                toDrop.add(containerInv.getStackInSlot(index));
                containerInv.setInventorySlotContents(index, null);
            }
        }

        if (owner != null && !owner.worldObj.isRemote) {
            for (ItemStack is : toDrop) owner.dropPlayerItemWithRandomChoice(is, false);
        }
    }

    public void onEnhancementSlotClick(int slot) {
        if (selectedSlot != slot) { // SELECT SLOT
            List<IEnhancementEnum> enhancements = EnhancementHandler
                    .getEnhancementsForItem(getSlot(0).getStack().getItem());

            if (slot < 0 || slot >= enhancements.size())
                Log.reportedError("Received S01 enhancement gui packet with invalid slot - $0", slot);
            else {
                this.selectedEnhancement = enhancements.get(slot);
                this.selectedSlot = slot;
            }
        } else if (selectedEnhancement != null) { // TRY ENHANCE
            ItemStack mainIS = getSlot(0).getStack();
            List<IEnhancementEnum> enhancements = EnhancementHandler.getEnhancementsForItem(mainIS.getItem());

            if (slot < 0 || slot >= enhancements.size()) {
                Log.reportedError("Received S01 enhancement gui packet with invalid slot - $0", slot);
                return;
            }

            IEnhancementEnum enhancement = enhancements.get(slot);
            if (EnhancementHandler.getEnhancements(mainIS).contains(enhancement)) return;

            SlotList slots = EnhancementHandler.getEnhancementSlotsForItem(mainIS.getItem());
            int enhancedAmount = mainIS.stackSize;
            ItemStack is;

            for (int a = 0; a < slots.amountIngredient; a++) {
                if ((is = ingredientSlots[a].getStack()) == null || !enhancement.getItemSelector().isValid(is)) {
                    enhancedAmount = 0;
                    break;
                } else if (is.stackSize < enhancedAmount) enhancedAmount = is.stackSize;
            }

            for (int a = 0; a < slots.amountPowder; a++) {
                if ((is = powderSlots[a].getStack()) == null) {
                    enhancedAmount = 0;
                    break;
                } else if (is.stackSize < enhancedAmount) enhancedAmount = is.stackSize;
            }

            if (enhancedAmount > 0) { // PERFORM ENHANCEMENT
                ItemStack newIS = EnhancementHandler.addEnhancement(mainIS, enhancement);
                newIS.stackSize = enhancedAmount;

                ItemStack currentOutput = getSlot(1).getStack();
                if (!(currentOutput == null || (currentOutput.getItem() == newIS.getItem()
                        && currentOutput.getItemDamage() == newIS.getItemDamage()
                        && ItemStack.areItemStackTagsEqual(currentOutput, newIS))))
                    return;
                if (currentOutput != null && currentOutput.stackSize + newIS.stackSize > newIS.getMaxStackSize())
                    newIS.stackSize = enhancedAmount = newIS.getMaxStackSize() - currentOutput.stackSize;
                if (currentOutput != null) newIS.stackSize += currentOutput.stackSize;
                if (mainIS.stackSize - enhancedAmount == 0 && mainIS.stackSize > 1) newIS.stackSize = --enhancedAmount;

                if (owner != null) {
                    enhancement.onEnhanced(newIS, owner);

                    if (isEnhancingTile()) {
                        enhanceableTile.getEnhancements().clear();
                        enhanceableTile.getEnhancements().addAll(EnhancementHandler.getEnhancements(newIS));
                    }
                }

                containerInv.setInventorySlotContents(isEnhancingTile() ? 0 : 1, newIS);
                if (!isEnhancingTile() && (mainIS.stackSize -= enhancedAmount) <= 0)
                    containerInv.setInventorySlotContents(0, null);

                for (int a = 0; a < slots.amountPowder; a++) {
                    if ((powderSlots[a].getStack().stackSize -= enhancedAmount) <= 0)
                        containerInv.setInventorySlotContents(2 + a, null);
                }

                for (int a = 0; a < slots.amountIngredient; a++) {
                    if ((ingredientSlots[a].getStack().stackSize -= enhancedAmount) <= 0)
                        containerInv.setInventorySlotContents(2 + powderSlots.length + a, null);
                }

                if (isEnhancingTile() || getSlot(0).getStack() == null) onSubjectChanged();

                if (CompendiumEvents.getPlayerData(owner)
                        .tryUnlockFragment(KnowledgeFragmentEnhancement.getEnhancementFragment(selectedEnhancement))) {
                    PacketPipeline.sendToPlayer(owner, new C19CompendiumData(owner));
                }

                PacketPipeline.sendToPlayer(
                        owner,
                        new C08PlaySound(C08PlaySound.EXP_ORB, owner.posX, owner.posY, owner.posZ, 1F, 1F));
            } else if (owner != null) { // TRY BREAK SOME POWDER
                for (int a = 0; a < slots.amountPowder; a++) {
                    if (powderSlots[a].getStack() == null) return;
                }

                for (int a = 0; a < slots.amountIngredient; a++) {
                    if (ingredientSlots[a].getStack() == null) return;
                }

                Random rand = owner.worldObj.rand;
                int powderBroken = 1;

                if (slots.amountPowder > 2) {
                    float add = (rand.nextFloat() + rand.nextFloat()) * 0.49F * (slots.amountPowder - 1);
                    powderBroken += rand.nextBoolean() ? MathUtil.floor(add) : MathUtil.ceil(add);
                }

                int index;
                while (powderBroken > 0) {
                    if (powderSlots[index = rand.nextInt(slots.amountPowder)].getStack() != null) {
                        if (--powderSlots[index].getStack().stackSize <= 0)
                            containerInv.setInventorySlotContents(2 + index, null);
                        --powderBroken;
                    }
                }

                if (rand.nextInt(4 + ((slots.amountPowder + slots.amountIngredient) >> 1)) == 0) {
                    CompendiumEvents.getPlayerData(owner).tryUnlockFragment(
                            KnowledgeFragmentEnhancement.getEnhancementFragment(selectedEnhancement));
                    PacketPipeline.sendToPlayer(owner, new C19CompendiumData(owner));
                }

                PacketPipeline.sendToPlayer(
                        owner,
                        new C08PlaySound(C08PlaySound.RANDOM_BREAK, owner.posX, owner.posY, owner.posZ, 0.8F, 1.2F));
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void onEnhancementSlotChangeClient(int selectedSlot) {
        ItemStack mainIS = getSlot(0).getStack();

        if (mainIS == null) {
            prevSelectedSlot = -1;
            return;
        } else if (selectedSlot == -1) selectedSlot = prevSelectedSlot;
        else prevSelectedSlot = selectedSlot;

        List<IEnhancementEnum> enhancements = EnhancementHandler.getEnhancementsForItem(mainIS.getItem());
        List<Enum> currentEnhancements = EnhancementHandler.getEnhancements(mainIS);

        StringBuilder build = new StringBuilder();

        for (int a = 0; a < enhancements.size(); a++) {
            IEnhancementEnum enhancement = enhancements.get(a);

            clientEnhancementTooltips[a] = build.append(EnumChatFormatting.LIGHT_PURPLE)
                    .append(DragonUtil.stripChatFormatting(enhancement.getName())).append("\n")
                    .append(EnumChatFormatting.GRAY)
                    .append(
                            I18n.format(
                                    currentEnhancements.contains(enhancement) ? "enhancements.alreadyEnhanced"
                                            : (selectedSlot == a ? "enhancements.clickEnhance"
                                                    : "enhancements.clickSelect")))
                    .toString();

            clientEnhancementBlocked[a] = currentEnhancements.contains(enhancement);

            build.setLength(0);
        }
    }

    public boolean isEnhancingTile() {
        return enhanceableTile != null;
    }
}
