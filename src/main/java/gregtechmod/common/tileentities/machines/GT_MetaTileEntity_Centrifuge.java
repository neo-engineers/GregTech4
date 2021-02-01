package gregtechmod.common.tileentities.machines;

import java.util.List;
import java.util.Map;

import gregtechmod.api.GregTech_API;
import gregtechmod.api.interfaces.IGregTechTileEntity;
import gregtechmod.api.interfaces.IRecipeWorkable;
import gregtechmod.api.metatileentity.MetaTileEntity;
import gregtechmod.api.metatileentity.implementations.GT_MetaTileEntity_BasicTank;
import gregtechmod.api.recipe.Recipe;
import gregtechmod.api.recipe.RecipeLogic;
import gregtechmod.api.recipe.RecipeMaps;
import gregtechmod.api.util.GT_ModHandler;
import gregtechmod.api.util.GT_Utility;
import gregtechmod.api.util.InfoBuilder;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

public class GT_MetaTileEntity_Centrifuge extends GT_MetaTileEntity_BasicTank implements IRecipeWorkable {
	
	private RecipeLogic recipeLogic;
	private int mSpeed = 0;
	
	public GT_MetaTileEntity_Centrifuge(int aID, String mName) {
		super(aID, mName);
		init();
	}
	
	public GT_MetaTileEntity_Centrifuge() {
		init();
	}
	
	private void init() {
		recipeLogic = new RecipeLogic(RecipeMaps.sCentrifugeRecipes, this) {
			@Override
			protected void consumeInputs(Recipe recipe) {
				if (mFluid == null || GT_MetaTileEntity_Centrifuge.this.consumeFluids(true, recipe)) {
					super.consumeInputs(recipe);
				}
			}
			
			@Override
			protected boolean isInputNonEmpty() {
				return mFluid != null || super.isInputNonEmpty();
			}
			
			@Override
			protected boolean match(Recipe recipe) {
				return consumeFluids(false, recipe) || super.match(recipe);
			}
		};
		recipeLogic.moveItems = false;
		recipeLogic.setRecipeProvider(this::searchForRecipe);
	}
	
	@Override public boolean isTransformerUpgradable()				{return true;}
	@Override public boolean isOverclockerUpgradable()				{return true;}
	@Override public boolean isBatteryUpgradable()					{return true;}
    @Override public boolean isValidSlot(int aIndex) 				{return aIndex < 6;}
	@Override public boolean isSimpleMachine()						{return false;}
	@Override public boolean isFacingValid(byte aFacing)			{return false;}
	@Override public boolean isEnetInput() 							{return true;}
	@Override public boolean isInputFacing(byte aSide)				{return true;}
    @Override public int maxEUInput()								{return 32;}
    @Override public int maxEUStore()								{return 10000;}
    @Override public int maxRFStore()								{return maxEUStore();}
    @Override public int maxSteamStore()							{return maxEUStore();}
	@Override public int getInvSize()								{return 7;}
	@Override public void onRightclick(EntityPlayer aPlayer)		{getBaseMetaTileEntity().openGUI(aPlayer, 146);}
	@Override public boolean isAccessAllowed(EntityPlayer aPlayer)	{return true;}
	@Override public RecipeLogic getRecipeLogic() 					{return recipeLogic;}
	@Override public int increaseProgress(int aProgress)			{recipeLogic.increaseProgressTime(aProgress); return recipeLogic.getMaxProgressTime()-recipeLogic.getProgressTime();}
    
	@Override
	public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
		return new GT_MetaTileEntity_Centrifuge();
	}
	
	@Override
	public void saveNBTData(NBTTagCompound aNBT) {
		super.saveNBTData(aNBT);
    	recipeLogic.saveToNBT(aNBT);
	}
	
	@Override
	public void loadNBTData(NBTTagCompound aNBT) {
		super.loadNBTData(aNBT);
    	recipeLogic.loadFromNBT(aNBT);
	}
	
	@Override public boolean doesFillContainers()	{return false;}
	@Override public boolean doesEmptyContainers()	{return false;}
	@Override public boolean canTankBeFilled()		{return true;}
	@Override public boolean canTankBeEmptied()		{return true;}
	@Override public boolean displaysItemStack()	{return true;}
	@Override public boolean displaysStackSize()	{return true;}
	
	@Override public int getInputSlot() {return 1;}
	@Override public int getOutputSlot() {return 2;}
	@Override public int getStackDisplaySlot() {return 6;}
	
    @Override
    public void onPostTick() {
	    if (getBaseMetaTileEntity().isServerSide()) {
	    	mSpeed = getBaseMetaTileEntity().getOverclockerUpgradeCount();
	    	recipeLogic.update();
		}
    }
    
    public boolean spaceForOutput(Recipe aRecipe) {
    	if (mInventory[2] == null || aRecipe.getOutputs()[0] == null || (mInventory[2].stackSize + aRecipe.getOutputs()[0].stackSize <= mInventory[2].getMaxStackSize() && GT_Utility.areStacksEqual(mInventory[2], aRecipe.getOutputs()[0])))
    	if (mInventory[3] == null || aRecipe.getOutputs()[1] == null || (mInventory[3].stackSize + aRecipe.getOutputs()[1].stackSize <= mInventory[3].getMaxStackSize() && GT_Utility.areStacksEqual(mInventory[3], aRecipe.getOutputs()[1])))
    	if (mInventory[4] == null || aRecipe.getOutputs()[2] == null || (mInventory[4].stackSize + aRecipe.getOutputs()[2].stackSize <= mInventory[4].getMaxStackSize() && GT_Utility.areStacksEqual(mInventory[4], aRecipe.getOutputs()[2])))
    	if (mInventory[5] == null || aRecipe.getOutputs()[3] == null || (mInventory[5].stackSize + aRecipe.getOutputs()[3].stackSize <= mInventory[5].getMaxStackSize() && GT_Utility.areStacksEqual(mInventory[5], aRecipe.getOutputs()[3])))
    		return true;
    	return false;
    }
    
    private Recipe searchForRecipe() {
    	Recipe result = null;
    	
    	if (mFluid != null) {
    		ItemStack filledBucket = GT_Utility.fillFluidContainer(mFluid, new ItemStack(Items.bucket));
    		if (filledBucket != null) {
    			filledBucket.stackSize = mFluid.amount / 1000;
    			result = recipeLogic.recipeMap.stream()
    					.filter(rec -> rec.match(filledBucket))
    					.findFirst()
    					.orElse(null);
    		}
    	} else {
    		result = recipeLogic.recipeMap.stream()
    				.filter(rec -> rec.match(false, getBaseMetaTileEntity(), getInputSlots()))
    				.findFirst()
    				.orElse(null);
    	}
    	
    	return result;
    }
    
    private boolean consumeFluids(boolean consume, Recipe recipe) {
    	ItemStack fluidItem = findFluidItem(recipe);
    	FluidStack fluid = null;
    	
    	if ((fluid = GT_Utility.getFluidForFilledItem(fluidItem)) != null) {
    		fluid.amount = 1000 * fluidItem.stackSize;
    		if (fluid.getFluidID() == mFluid.getFluidID() && mFluid.amount >= fluid.amount) {
    			if (consume) mFluid.amount -= fluid.amount;
    			return true;
    		}
    	}
    	
    	return false;
    }
    
    private ItemStack findFluidItem(Recipe recipe) {
    	ItemStack fluidItem = null;
    	for (ItemStack[] stacks : recipe.getRepresentativeInputs()) {
    		for (ItemStack stack : stacks) {
    			if (GT_Utility.getFluidForFilledItem(stack) != null) {
    				fluidItem = stack;
    				break;
    			}
    		}
    	}
    	
    	return fluidItem;
    }
    
	@Override
	public int getTextureIndex(byte aSide, byte aFacing, boolean aActive, boolean aRedstone) {
    	if (aSide == 0) return 32;
    	if (aSide == 1) return aActive ? mSpeed >= 2 ? 44 : mSpeed == 1 ? 43 : 42 : 41;
    	return aActive ? mSpeed >= 2 ? 28 : mSpeed == 1 ? 27 : 26 : 25;
	}
	
	@Override
	public Map<String, List<Object>> getInfoData() {
		return InfoBuilder.create()
				.newKey("sensor.progress.percentage", recipeLogic.getProgressTime() * 100.0D / recipeLogic.getMaxProgressTime())
				.newKey("sensor.progress.secs", recipeLogic.getProgressTime() / 20)
				.newKey("sensor.progress.secs", recipeLogic.getMaxProgressTime() / 20)
				.build();
	}
	
	@Override
	public boolean isGivingInformation() {
		return true;
	}
	@Override
	public String getDescription() {
		return "metatileentity.GT_Centrifuge.tooltip";
	}
	
	@Override
	public boolean allowPullStack(int aIndex, byte aSide, ItemStack aStack) {
		return aIndex>1;
	}
	
	@Override
	public boolean allowPutStack(int aIndex, byte aSide, ItemStack aStack) {
		return GT_Utility.areStacksEqual(aStack, GT_ModHandler.getEmptyCell(1))||GT_Utility.areStacksEqual(aStack, GT_ModHandler.getEmptyFuelCan(1))?aIndex==1:aIndex==0;
	}
	
	@Override
	public void onValueUpdate(byte aValue) {
		mSpeed = aValue;
	}
	
	@Override
	public byte getUpdateData() {
		return (byte) mSpeed;
	}
	
	@Override public int getTankPressure() {return -100;}
	@Override public int getCapacity() {return 64000;}

	@Override
	public void startProcess() {}

	@Override
	public void endProcess() {
		getBaseMetaTileEntity().setErrorDisplayID(0);
	}

	@Override
	public void stutterProcess() {
		if (GregTech_API.sConstantEnergy) {
			int val = (int) (recipeLogic.getMaxProgressTime() * 0.1D);
			
			if (recipeLogic.getProgressTime() > val) 
				recipeLogic.increaseProgressTime(-val);
			getBaseMetaTileEntity().setErrorDisplayID(1);
		}
	}

	@Override
	public boolean allowToCheckRecipe() {
		return true;
	}

	@Override
	public int[] getInputSlots() {
		return new int[] {0, 1};
	}

	@Override
	public int[] getOutputSlots() {
		return new int[] {2, 3, 4, 5};
	}
}
