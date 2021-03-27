package gregtechmod.common.containers;

import gregtechmod.api.gui.GT_ContainerMetaTile_Machine;
import gregtechmod.api.gui.GT_Slot_Holo;
import gregtechmod.api.interfaces.IGregTechTileEntity;
import gregtechmod.api.util.GT_Utility;
import gregtechmod.common.network.SyncedField;
import gregtechmod.common.tileentities.automation.GT_MetaTileEntity_ElectricBufferSmall;
import gregtechmod.common.tileentities.automation.GT_MetaTileEntity_ElectricTypeSorter;

import com.google.gson.JsonObject;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class GT_Container_ElectricTypeSorter extends GT_ContainerMetaTile_Machine {

	public final SyncedField<Byte> mTargetDirection = new SyncedField<>("mTargetDirection"	, Byte.valueOf((byte)0));
	public final SyncedField<Byte> mMode			= new SyncedField<>("mMode"				, Byte.valueOf((byte)0));
	
	public GT_Container_ElectricTypeSorter(InventoryPlayer aInventoryPlayer, IGregTechTileEntity aTileEntity) {
		super(aInventoryPlayer, aTileEntity);
	}

    public void addSlots(InventoryPlayer aInventoryPlayer) {
        addSlotToContainer(new Slot(mTileEntity,  0,  25,  23));
        addSlotToContainer(new GT_Slot_Holo(mTileEntity, 1,  71, 23, false, true, 1));
        addSlotToContainer(new GT_Slot_Holo(mTileEntity, 1,   8, 63, false, true, 1));
        addSlotToContainer(new GT_Slot_Holo(mTileEntity, 1,  26, 63, false, true, 1));
        addSlotToContainer(new GT_Slot_Holo(mTileEntity, 1,  44, 63, false, true, 1));
        addSlotToContainer(new GT_Slot_Holo(mTileEntity, 1, 134, 63, false, true, 1));
    }

    public ItemStack slotClick(int aSlotIndex, int aMouseclick, int aShifthold, EntityPlayer aPlayer) {    	if (aSlotIndex < 1) return super.slotClick(aSlotIndex, aMouseclick, aShifthold, aPlayer);
	    
    	Slot tSlot = (Slot)inventorySlots.get(aSlotIndex);
	    if (tSlot != null) {
	    	if (mTileEntity.getMetaTileEntity() == null) return null;
	    	GT_MetaTileEntity_ElectricBufferSmall mte = (GT_MetaTileEntity_ElectricBufferSmall)mTileEntity.getMetaTileEntity();
		    if (aSlotIndex == 1) {
		    	if (aMouseclick == 0)
		    		((GT_MetaTileEntity_ElectricTypeSorter)mTileEntity.getMetaTileEntity()).switchModeForward();
		    	else
		    		((GT_MetaTileEntity_ElectricTypeSorter)mTileEntity.getMetaTileEntity()).switchModeBackward();
		    	return null;
		    } else if (aSlotIndex == 2) {
				mte.bOutput = !mte.bOutput;
				if (aPlayer.worldObj.isRemote)
					GT_Utility.sendChatToPlayer(aPlayer, new ChatComponentTranslation("metatileentity.status.energy_out." + mte.bOutput));
				return null;
			} else if (aSlotIndex == 3) {
				mte.bRedstoneIfFull = !mte.bRedstoneIfFull;
				if (aPlayer.worldObj.isRemote)
					GT_Utility.sendChatToPlayer(aPlayer, new ChatComponentTranslation("metatileentity.status.redstone_if_full." + mte.bRedstoneIfFull));
				return null;
			} else if (aSlotIndex == 4) {
				mte.bInvert = !mte.bInvert;
				if (aPlayer.worldObj.isRemote)
					GT_Utility.sendChatToPlayer(aPlayer, new ChatComponentTranslation("metatileentity.status.redstone_invert." + mte.bInvert));
				return null;
			} else if (aSlotIndex == 5) {
		    	((GT_MetaTileEntity_ElectricTypeSorter)mTileEntity.getMetaTileEntity()).mTargetDirection = (byte) ((((GT_MetaTileEntity_ElectricTypeSorter)mTileEntity.getMetaTileEntity()).mTargetDirection + 1) % 6);
		    }
    	}
	    
    	return super.slotClick(aSlotIndex, aMouseclick, aShifthold, aPlayer);
    }
    
	@Override
	public void prepareChanges(JsonObject data, boolean force) {
		super.prepareChanges(data, force);
		mMode.updateAndWriteChanges(data, force, ((GT_MetaTileEntity_ElectricTypeSorter)mTileEntity.getMetaTileEntity()).mMode);
		mTargetDirection.updateAndWriteChanges(data, force, ((GT_MetaTileEntity_ElectricTypeSorter)mTileEntity.getMetaTileEntity()).mTargetDirection);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void processChanges(JsonObject data) {
		super.processChanges(data);
		mMode.readChanges(data);
		mTargetDirection.readChanges(data);
	}
    
    public int getSlotCount() {
    	return 1;
    }
    
    public int getShiftClickSlotCount() {
    	return 1;
    }
}
