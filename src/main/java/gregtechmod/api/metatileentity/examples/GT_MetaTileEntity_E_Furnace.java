package gregtechmod.api.metatileentity.examples;

import java.util.Collections;
import java.util.List;

import gregtechmod.api.GregTech_API;
import gregtechmod.api.interfaces.IGregTechTileEntity;
import gregtechmod.api.metatileentity.MetaTileEntity;
import gregtechmod.api.metatileentity.implementations.GT_MetaTileEntity_BasicMachine;
import gregtechmod.api.recipe.Ingredient;
import gregtechmod.api.recipe.Recipe;
import gregtechmod.api.recipe.RecipeMap;
import gregtechmod.api.util.GT_ModHandler;
import gregtechmod.api.util.GT_OreDictUnificator;
import gregtechmod.api.util.ListAdapter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * This is a copy of my Electric Furnace, with a few Comments from me.
 * 
 * I chose this one as it contains also the Heating Coils as a Machine-specific Upgrade
 * 
 * It uses the Basic Machine GUIs and Containers
 */
public class GT_MetaTileEntity_E_Furnace extends GT_MetaTileEntity_BasicMachine {
	
	public int mHeatingCoilTier = 0;
	
	// see @MetaTileEntity to register MetaTileEntities
	public GT_MetaTileEntity_E_Furnace(int aID, String aName, RecipeMap<?> recipeMap) {
		super(aID, aName, recipeMap);
	}
	
	// An empty constructor, which is needed for several Java reasons
	public GT_MetaTileEntity_E_Furnace(RecipeMap<?> recipeMap) {
		super(recipeMap);
		recipeLogic.setProgressTimeManipulator(time -> time * (1 + mHeatingCoilTier));
		recipeLogic.setRecipeProvider(() -> {
			ItemStack output;
			
			List<ItemStack> inputs = getInputItems();
			for (int i = 0; i < inputs.size(); i++) {
				ItemStack in = inputs.get(i);
				if (in != null && null != (output = GT_ModHandler.getSmeltingOutput(inputs))) {
					// It shall cook at 3 EU/t, if this Machine is overclocked then it will consume more
					// The time it usually needs, the Heating Coils re decreasing this Time, and if the Machine is overclocked, then it gets processed faster
					Ingredient input = new Ingredient() {
						ItemStack input;
						{	
							input = in.copy();
							input.stackSize = 1;
						}
						@Override
						public boolean match(ItemStack input) {
							return this.input.isItemEqual(input);
						}
						
						@Override
						public boolean isWildcard() {
							return false;
						}
						
						@Override
						public List<ItemStack> getVariants() {
							return Collections.singletonList(input);
						}
						
						@Override
						public int getCount() {
							return 1;
						}
					};
					
					return new Recipe(0, 3, 130, false, Collections.singletonList(input), Collections.singletonList(output), Collections.emptyList());
				}
			}
			
			return null;
		});
	}
	
	// Apply your empty constructor here
	@Override
	public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
		return new GT_MetaTileEntity_E_Furnace(recipeLogic.recipeMap);
	}
	
	@Override
	public void saveNBTData(NBTTagCompound aNBT) {
		super.saveNBTData(aNBT);
		// saving the Heating Coil Upgrades
    	aNBT.setByte("mHeatingCoilTier", (byte)mHeatingCoilTier);
	}
	
	@Override
	public void loadNBTData(NBTTagCompound aNBT) {
		super.loadNBTData(aNBT);
		// loading the Heating Coil Upgrades
    	mHeatingCoilTier = aNBT.getByte("mHeatingCoilTier");
	}
	
	@Override
	public void setItemNBT(NBTTagCompound aNBT) {
		super.setItemNBT(aNBT);
		// putting the Heating Coil Upgrades on the dropped ItemStack after wrenching the Machine away.
		if (mHeatingCoilTier > 0) aNBT.setByte("mHeatingCoilTier", (byte)mHeatingCoilTier);
	}
	
	@Override
	public void onRightclick(EntityPlayer aPlayer) {
	    ItemStack tPlayerItem = aPlayer.inventory.getCurrentItem();
	    // Adds the Heating Coil Upgrades when rightclicking the Machine with them
	    if (mHeatingCoilTier <= 0 && GT_OreDictUnificator.isItemStackInstanceOf(tPlayerItem, "craftingHeatingCoilTier01")) {
	    	// Creative Players don't have to pay
	    	if (!aPlayer.capabilities.isCreativeMode) tPlayerItem.stackSize--;
		    mHeatingCoilTier = 1;
	    	return;
	    }
	    if (mHeatingCoilTier == 1 && GT_OreDictUnificator.isItemStackInstanceOf(tPlayerItem, "craftingHeatingCoilTier02")) {
	    	if (!aPlayer.capabilities.isCreativeMode) tPlayerItem.stackSize--;
		    mHeatingCoilTier = 2;
	    	return;
	    }
	    if (mHeatingCoilTier == 2 && GT_OreDictUnificator.isItemStackInstanceOf(tPlayerItem, "craftingHeatingCoilTier03")) {
	    	if (!aPlayer.capabilities.isCreativeMode) tPlayerItem.stackSize--;
		    mHeatingCoilTier = 3;
	    	return;
	    }
	    // Opens the GUI of your Machine. Replace GregTech_API.gregtechmod with your Mod to call your GUI.
		getBaseMetaTileEntity().openGUI(aPlayer, 135, GregTech_API.gregtechmod);
	}
	
	@Override
    public List<ItemStack> getInputItems() {
    	return new ListAdapter<>(mInventory, 1, 2);
	}
	
	@Override
    public List<ItemStack> getOutputItems() {
		return new ListAdapter<>(mInventory, 3, 4);
	}
	
	@Override
	public int getFrontFacingInactive() {
		// The Furnace Front Texture when it does nothing
		// Since this relies on my Texture Indices I would recommend the use of @getTextureIcon in @MetaTileEntity
		return 250;
	}
	
	@Override
	public int getFrontFacingActive() {
		// The Furnace Front Texture when it is Active
		// Since this relies on my Texture Indices I would recommend the use of @getTextureIcon in @MetaTileEntity
		return 251;
	}
	
	@Override
	public String getDescription() {
		// The Description of the Machine, as seen in the Tooltip
		// Currently using localization keys insteadof plain text
		return "metatileentity.GT_E_Furnace.tooltip";
	}
}
