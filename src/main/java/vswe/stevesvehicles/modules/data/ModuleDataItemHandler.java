package vswe.stevesvehicles.modules.data;


import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import vswe.stevesvehicles.modules.ModuleBase;
import vswe.stevesvehicles.old.Helpers.Localization;
import vswe.stevesvehicles.old.Items.ModItems;
import vswe.stevesvehicles.vehicles.VehicleBase;
import vswe.stevesvehicles.vehicles.VehicleRegistry;
import vswe.stevesvehicles.vehicles.VehicleType;
import vswe.stevesvehicles.vehicles.entities.EntityModularCart;
import vswe.stevesvehicles.vehicles.versions.VehicleVersion;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class ModuleDataItemHandler {

    //Modules list -> valid/error messages & total cost
    //ItemStack + ModuleType -> isValid
    //Module + ModuleType -> isValid
    //ItemStack + ModuleType.clazz -> isValid

    //Modules list -> vehicle item
    //VehicleBase -> vehicle item

    //vehicle item -> modules list

    public static String checkForErrors(ModuleDataHull hull, ArrayList<ModuleData> modules) {
        if (hull.getValidVehicles() == null || hull.getValidVehicles().isEmpty()) {
            return "The hull of this vehicle has no vehicle type"; //TODO localization
        }

        VehicleType vehicleType = hull.getValidVehicles().get(0);
        return checkForErrors(vehicleType, hull, modules);

    }

    public static String checkForErrors(VehicleType vehicle, ModuleDataHull hull, List<ModuleData> modules) {
        //Normal errors here
        if (getTotalCost(modules) > hull.getModularCapacity()) {
            return Localization.MODULE_INFO.CAPACITY_ERROR.translate();
        }


        for (int i = 0; i < modules.size(); i++) {
            ModuleData mod1 = modules.get(i);

            if (!mod1.getValidVehicles().contains(vehicle)) {
                return mod1.getName() + " can't be placed as a part of a " + vehicle.getName(); //TODO localization
            }

            if (mod1.getCost() > hull.getComplexityMax()) {
                return Localization.MODULE_INFO.COMPLEXITY_ERROR.translate(mod1.getName());
            }
            if (mod1.getParent() != null && !modules.contains(mod1.getParent())) {
                return Localization.MODULE_INFO.PARENT_ERROR.translate(mod1.getName(), mod1.getParent().getName());
            }
            if (mod1.getNemesis() != null) {
                for (ModuleData nemesis : mod1.getNemesis()) {
                    if (modules.contains(nemesis)) {
                        return Localization.MODULE_INFO.NEMESIS_ERROR.translate(mod1.getName(), nemesis.getName());
                    }
                }
            }
            if (mod1.getRequirement() != null){
                for (ModuleDataGroup group : mod1.getRequirement()) {
                    int count = 0;
                    for (ModuleData mod2 : group.getModules()) {
                        for (ModuleData mod3 : modules) {
                            if (mod2.equals(mod3)) {
                                count++;
                            }
                        }
                    }
                    if (count < group.getCount()) {
                        return Localization.MODULE_INFO.PARENT_ERROR.translate(mod1.getName(), group.getCountName() + " " + group.getName());
                    }
                }
            }

            for (int j = i+1; j < modules.size(); j++) {
                ModuleData mod2 = modules.get(j);

                if (mod1 == mod2) {
                    if (!mod1.getAllowDuplicate()) {
                        return Localization.MODULE_INFO.DUPLICATE_ERROR.translate(mod1.getName());
                    }
                }else if (mod1.getSides() != null && mod2.getSides() != null) {
                    ModuleSide clash = ModuleSide.NONE;
                    for (ModuleSide side1 : mod1.getSides()) {
                        for (ModuleSide side2 : mod2.getSides()) {
                            if (side1 == side2) {
                                clash = side1;
                                break;
                            }
                        }
                        if (clash != ModuleSide.NONE) {
                            break;
                        }
                    }
                    if (clash != ModuleSide.NONE) {
                        return Localization.MODULE_INFO.CLASH_ERROR.translate(mod1.getName(), mod2.getName(), clash.toString());
                    }
                }
            }
        }

        return null;
    }

    public static int getTotalCost(List<ModuleData> modules) {
        int currentCost = 0;
        for (ModuleData module : modules) {
            currentCost += module.getCost();
        }
        return currentCost;
    }


    public static boolean isValidModuleItem(ModuleType type, ItemStack itemstack) {
        ModuleData module = ModItems.modules.getModuleData(itemstack);
        return isValidModuleItem(type, module);
    }

    public static boolean isValidModuleItem(ModuleType type, ModuleData module) {
        return module != null && module.getModuleType() == type;
    }

    public static boolean isItemOfModularType(ItemStack itemstack, Class<? extends ModuleBase> validClass) {
        if (itemstack.getItem() == ModItems.modules) {
            ModuleData module = ModItems.modules.getModuleData(itemstack);
            if (module != null) {
                if (validClass.isAssignableFrom(module.getModuleClass())) {
                    return true;
                }
            }
        }

        return false;
    }

    public static ItemStack createModularVehicle(List<ItemStack> moduleItems) {
        VehicleType vehicleType = null;
        List<ModuleData> modules = new ArrayList<ModuleData>();
        for (ItemStack moduleItem : moduleItems) {
            ModuleData moduleData = ModItems.modules.getModuleData(moduleItem);
            modules.add(moduleData);
            if (moduleData != null) {
                if (moduleData.getModuleType() == ModuleType.HULL) {
                    if (moduleData.getValidVehicles() == null ||moduleData.getValidVehicles().isEmpty()) {
                        return null;
                    }
                    vehicleType = moduleData.getValidVehicles().get(0);
                }
            }
        }

        if (vehicleType != null) {
            return createModularVehicle(vehicleType, modules);
        }else{
            return null;
        }
    }



    private static final String NBT_VEHICLE_ID = "VehicleId";
    public static ItemStack createModularVehicle(VehicleType vehicle, List<ModuleData> modules) {
        if (vehicle == null) {
            return null;
        }

        int vehicleId = VehicleRegistry.getInstance().getIdFromType(vehicle);
        if (vehicleId < 0) {
            return null;
        }

        NBTTagCompound data = new NBTTagCompound();
        data.setByte(NBT_VEHICLE_ID, (byte)vehicleId);

        NBTTagList modulesCompoundList = new NBTTagList();
        for (ModuleData module : modules) {
            NBTTagCompound moduleCompound = new NBTTagCompound();
            int id = ModuleRegistry.getIdFromModule(module);
            if (id > 0) {
                moduleCompound.setShort(VehicleBase.NBT_ID, (short)id);
                //TODO let the ModuleData save extra data if it feels like it (for instance if easter egg bags have been opened or not)
                modulesCompoundList.appendTag(moduleCompound);
            }
        }

        data.setTag(VehicleBase.NBT_MODULES, modulesCompoundList);

        ItemStack vehicleItem = new ItemStack(ModItems.carts, 1);
        vehicleItem.setTagCompound(data);
        VehicleVersion.addVersion(vehicleItem);

        return vehicleItem;
    }

    public static ItemStack createModularVehicle(VehicleBase vehicle) {
        return createModularVehicle(vehicle.getVehicleType(), vehicle.getModuleDataList());
    }


    private ModuleDataItemHandler(){}
}