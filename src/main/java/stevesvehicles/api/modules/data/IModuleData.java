package stevesvehicles.api.modules.data;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevesvehicles.api.modules.Module;
import stevesvehicles.api.modules.container.IModuleContainer;
import stevesvehicles.api.modules.handlers.IContentHandlerFactory;
import stevesvehicles.api.modules.handlers.IModuleHandler;
import stevesvehicles.api.modules.handlers.ModuleHandlerType;

public interface IModuleData extends IForgeRegistryEntry<IModuleData> {
	public static void addNemesis(IModuleData m1, IModuleData m2) {
		m2.addNemesis(m1);
		m1.addNemesis(m2);
	}

	Class<? extends Module> getModuleClass();

	String getRawUnlocalizedName();

	String getFullRawUnlocalizedName();

	void setFullRawUnlocalizedName(String val);

	int getCost();

	IModuleType getModuleType();

	boolean getIsValid();

	boolean getIsLocked();

	IModuleData lock();

	boolean getEnabledByDefault();

	IModuleData lockByDefault();

	IModuleData setAllowDuplicate(boolean b);

	boolean getAllowDuplicate();

	IModuleData setHasExtraData(boolean val);

	boolean hasExtraData();

	void addDefaultExtraData(NBTTagCompound compound);

	void addExtraData(NBTTagCompound compound, Module module);

	void readExtraData(NBTTagCompound compound, Module moduleBase);

	String getModuleInfoText(NBTTagCompound compound);

	String getCartInfoText(String name, NBTTagCompound compound);

	ArrayList<IModuleSide> getSides();

	IModuleData addSides(IModuleSide... sides);

	IModuleData addParent(IModuleData parent);

	IModuleData addMessage(ILocalizedText s);

	void addNemesis(IModuleData nemesis);

	IModuleData addRequirement(IModuleDataGroup requirement);

	String getName();

	String getUnlocalizedName();

	String getUnlocalizedNameForItem();

	IModuleData getParent();

	ArrayList<IModuleData> getNemesis();

	ArrayList<IModuleDataGroup> getRequirement();

	boolean getHasRecipe();

	void addSpecificInformation(List<String> list);

	void addInformation(List<String> list, NBTTagCompound compound);

	void addExtraMessage(List<String> list);

	/* HANDLER TYPES */
	IModuleData addHandlers(ModuleHandlerType... types);

	ArrayList<ModuleHandlerType> getValidHandlers();

	Module createModule(IModuleContainer container, IModuleHandler handler, ItemStack stack);

	/* OPTIONAL HANDLERS*/
	void addOptionalHandlers(IContentHandlerFactory... factorys);

	/* MODLES */
	@SideOnly(Side.CLIENT)
	IModuleData addModelData(ModuleHandlerType type, IModelData modelData);

	@SideOnly(Side.CLIENT)
	IModelData getModelData(ModuleHandlerType type);
}