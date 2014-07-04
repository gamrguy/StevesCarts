package vswe.stevesvehicles.old.TileEntities;
import java.util.ArrayList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.nbt.NBTTagCompound;
import vswe.stevesvehicles.old.Helpers.Localization;
import vswe.stevesvehicles.vehicle.entity.EntityModularCart;
import vswe.stevesvehicles.old.Containers.ContainerActivator;
import vswe.stevesvehicles.container.ContainerBase;
import vswe.stevesvehicles.old.Helpers.ActivatorOption;
import vswe.stevesvehicles.old.Interfaces.GuiActivator;
import vswe.stevesvehicles.client.gui.GuiBase;
import vswe.stevesvehicles.module.common.addon.ModuleChunkLoader;
import vswe.stevesvehicles.module.common.addon.ModuleInvisible;
import vswe.stevesvehicles.module.common.addon.ModuleShield;
import vswe.stevesvehicles.module.common.attachment.ModuleCage;
import vswe.stevesvehicles.module.cart.tool.ModuleDrill;

/**
 * The tile entity used by the Module Toggler
 * @author Vswe
 *
 */
public class TileEntityActivator extends TileEntityBase
{

	@SideOnly(Side.CLIENT)
	@Override
	public GuiBase getGui(InventoryPlayer inv) {
		return new GuiActivator(inv, this);
	}
	
	@Override
	public ContainerBase getContainer(InventoryPlayer inv) {
		return new ContainerActivator(inv, this);		
	}
	
	/**
	 * The different settings the toggler can toggle
	 */
	private ArrayList<ActivatorOption> options;

    public TileEntityActivator()
    {
		loadOptions();
    }

    /**
     * Load the different settings the player can toggle and change. For example the drill.
     */
	private void loadOptions() {
		options = new ArrayList<ActivatorOption>();
		options.add(new ActivatorOption(Localization.GUI.TOGGLER.OPTION_DRILL, ModuleDrill.class));
		options.add(new ActivatorOption(Localization.GUI.TOGGLER.OPTION_SHIELD, ModuleShield.class));
		options.add(new ActivatorOption(Localization.GUI.TOGGLER.OPTION_INVISIBILITY, ModuleInvisible.class));
		options.add(new ActivatorOption(Localization.GUI.TOGGLER.OPTION_CHUNK, ModuleChunkLoader.class));
		options.add(new ActivatorOption(Localization.GUI.TOGGLER.OPTION_CAGE_AUTO, ModuleCage.class, 0));
		options.add(new ActivatorOption(Localization.GUI.TOGGLER.OPTION_CAGE, ModuleCage.class, 1));
	}
	
	/**
	 * Get the different settings the toggler can toggle
	 * @return A list of the settings
	 */
	public ArrayList<ActivatorOption> getOptions() {
		return options;
	}

	@Override
    public void readFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readFromNBT(nbttagcompound);
        
        //load all the options
 		for (ActivatorOption option : options) {
			option.setOption(nbttagcompound.getByte(option.getName()));
		}
    }

	@Override
    public void writeToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeToNBT(nbttagcompound);
        
        //save all the options
		for (ActivatorOption option : options) {
			nbttagcompound.setByte(option.getName(), (byte)option.getOption());
		}
    }




	@Override
	public void receivePacket(int id, byte[] data, EntityPlayer player) {
		if (id == 0) {
			boolean leftClick = (data[0] & 1) == 0;
			int optionId = (data[0] & ~1) >> 1;
			if (optionId >= 0 && optionId < options.size()) {
				options.get(optionId).changeOption(leftClick);
			}
		}
	}
	

	

	@Override
	public void initGuiData(Container con, ICrafting crafting) {
		for (int i = 0; i < options.size(); i++) {
			updateGuiData(con, crafting, i, (short)options.get(i).getOption());
		}
	}


	@Override
	public void checkGuiData(Container con, ICrafting crafting) {
		for (int i = 0; i < options.size(); i++) {
			int option = options.get(i).getOption();
			int lastoption = ((ContainerActivator)con).lastOptions.get(i);
			
			//if an update has been made, send the new data
			if (option != lastoption) {
				updateGuiData(con, crafting, i, (short)option);
				((ContainerActivator)con).lastOptions.set(i, option);
			}
		}
	}
	

	@Override
	public void receiveGuiData(int id, short data) {
		//if it's a valid id, update the option associated with that id
		if (id >= 0 && id < options.size()) {
			options.get(id).setOption(data);
		}	
	}
	
	/**
	 * Handles a cart that is passing an advanced detector rail "in front" of this toggler
	 * @param cart The cart that is passing
	 * @param isOrange Whether the cart is passing in the orange direction or not
	 */
	public void handleCart(EntityModularCart cart, boolean isOrange) {
		
		//tell the cart to update with any option that is not disabled
		for (ActivatorOption option : options) {
			if (!option.isDisabled()) {
				cart.getVehicle().handleActivator(option, isOrange);
			}
		}
	}



}
