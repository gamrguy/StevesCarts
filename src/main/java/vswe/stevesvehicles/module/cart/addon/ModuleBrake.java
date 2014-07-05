package vswe.stevesvehicles.module.cart.addon;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import vswe.stevesvehicles.client.gui.GuiVehicle;
import vswe.stevesvehicles.module.common.addon.ModuleAddon;
import vswe.stevesvehicles.vehicle.VehicleBase;
import vswe.stevesvehicles.old.Helpers.Localization;
import vswe.stevesvehicles.old.Helpers.ResourceHelper;
import vswe.stevesvehicles.module.cart.ILeverModule;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ModuleBrake extends ModuleAddon implements ILeverModule {
	public ModuleBrake(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	@Override
	public boolean hasSlots() {
		return false;
	}

	@Override
	public boolean hasGui(){
		return true;
	}

	@Override
	public int guiWidth() {
		return 80;
	}

	@Override
	public int guiHeight() {
		return 35;
	}

	@Override
	public void drawForeground(GuiVehicle gui) {
	    drawString(gui, Localization.MODULES.ADDONS.CONTROL_LEVER.translate(), 8, 6, 0x404040);
	}


	
	@Override
	@SideOnly(Side.CLIENT)
	public void drawBackground(GuiVehicle gui, int x, int y) {
		ResourceHelper.bindResource("/gui/lever.png");

		drawButton(gui, x,y, START_STOP_RECT, isForceStopping() ? 2 : 1);
		drawButton(gui, x,y, TURN_BACK_RECT, 0);
	}

	private void drawButton(GuiVehicle gui, int x, int y, int[] coordinates, int imageID) {
		if (inRect(x,y, coordinates)) {
			drawImage(gui,coordinates, 0, coordinates[3]);
		}else{
			drawImage(gui,coordinates, 0, 0);
		}

		int srcY = coordinates[3] * 2 + imageID * (coordinates[3] - 2);
		drawImage(gui, coordinates[0] + 1, coordinates[1] + 1, 0, srcY, coordinates[2] - 2, coordinates[3] - 2);
	}

	private static final int[] START_STOP_RECT = new int[] {15,20, 24, 12};
	private static final int[] TURN_BACK_RECT = new int[] {START_STOP_RECT[0] + START_STOP_RECT[2] + 5, START_STOP_RECT[1], START_STOP_RECT[2], START_STOP_RECT[3]};

	@Override
	public boolean stopEngines() {
		return isForceStopping();
	}

	private boolean isForceStopping() {
		if (isPlaceholder())  {
			return getSimInfo().getBrakeActive();
		}else{
			return getDw(0) != 0;
		}
	}

	private void setForceStopping(boolean val) {
		updateDw(0, (byte)(val ? 1 : 0));
	}

	@Override
	public void drawMouseOver(GuiVehicle gui, int x, int y) {
		drawStringOnMouseOver(gui, isForceStopping() ? Localization.MODULES.ADDONS.LEVER_START.translate() : Localization.MODULES.ADDONS.LEVER_STOP.translate(), x,y, START_STOP_RECT);
		drawStringOnMouseOver(gui, Localization.MODULES.ADDONS.LEVER_TURN.translate(), x,y, TURN_BACK_RECT);
	}

	@Override
	public void mouseClicked(GuiVehicle gui, int x, int y, int button) {
		if (button == 0) {
			if (inRect(x,y, START_STOP_RECT)) {
				sendPacket(0);
			}else if (inRect(x,y, TURN_BACK_RECT)) {
				sendPacket(1);
			}
		}
	}

	@Override
	protected void receivePacket(int id, byte[] data, EntityPlayer player) {
		if (id == 0) {
			setForceStopping(!isForceStopping());
		}else if(id == 1) {
			turnback();
		}
	}

	@Override
	public int numberOfPackets() {
		return 2;
	}
	
	@Override
	public float getLeverState() {
		if (isForceStopping()) {
			return 0;
		}else{
			return 1;
		}
	}
	

	@Override
	public int numberOfDataWatchers() {
		return 1;
	}

	@Override
	public void initDw() {
		addDw(0,0);
	}

	
	
	@Override
	protected void save(NBTTagCompound tagCompound) {
		tagCompound.setBoolean("ForceStop", isForceStopping());
	}
	
	@Override
	protected void load(NBTTagCompound tagCompound) {
		setForceStopping(tagCompound.getBoolean("ForceStop"));
	}		
}