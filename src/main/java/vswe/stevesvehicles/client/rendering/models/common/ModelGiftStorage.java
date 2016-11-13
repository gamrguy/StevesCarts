package vswe.stevesvehicles.client.rendering.models.common;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;

import vswe.stevesvehicles.client.ResourceHelper;
import vswe.stevesvehicles.client.rendering.models.ModelVehicle;
import vswe.stevesvehicles.module.ModuleBase;
import vswe.stevesvehicles.module.common.storage.chest.ModuleChest;

@SideOnly(Side.CLIENT)
public class ModelGiftStorage extends ModelVehicle {
	private static final ResourceLocation TEXTURE = ResourceHelper.getResource("/models/giftStorageModel.png");

	@Override
	public ResourceLocation getResource(ModuleBase module) {
		return TEXTURE;
	}

	@Override
	protected int getTextureHeight() {
		return 64;
	}

	private ModelRenderer lid1;
	private ModelRenderer lid2;

	public ModelGiftStorage() {
		lid1 = addChest(false);
		lid2 = addChest(true);
	}

	private ModelRenderer addChest(boolean opposite) {
		ModelRenderer chestAnchor = new ModelRenderer(this);
		addRenderer(chestAnchor);
		int offsetY = 0;
		if (opposite) {
			chestAnchor.rotateAngleY = (float) Math.PI;
			offsetY = 21;
		}
		ModelRenderer base = new ModelRenderer(this, 0, 7 + offsetY);
		fixSize(base);
		chestAnchor.addChild(base);
		base.addBox(8, // X
				3, // Y
				2F, // Z
				16, // Size X
				6, // Size Y
				4, // Size Z
				0.0F);
		base.setRotationPoint(-16.0F, // X
				-5.5F, // Y
				-14.0F // Z
				);
		ModelRenderer lid = new ModelRenderer(this, 0, offsetY);
		fixSize(lid);
		chestAnchor.addChild(lid);
		lid.addBox(8, // X
				-3, // Y
				-4F, // Z
				16, // Size X
				3, // Size Y
				4, // Size Z
				0.0F);
		lid.setRotationPoint(-16.0F, // X
				-1.5F, // Y
				-8F // Z
				);
		ModelRenderer lock = new ModelRenderer(this, 0, 17 + offsetY);
		fixSize(lock);
		lid.addChild(lock);
		lock.addBox(1F, // X
				1.5F, // Y
				0.5F, // Z
				2, // Size X
				3, // Size Y
				1, // Size Z
				0.0F);
		lock.setRotationPoint(14.0F, // X
				-3F, // Y
				-5.5F // Z
				);
		return lid;
	}

	@Override
	public void applyEffects(ModuleBase module, float yaw, float pitch, float roll) {
		lid1.rotateAngleX = module == null ? 0 : -((ModuleChest) module).getChestAngle();
		lid2.rotateAngleX = module == null ? 0 : -((ModuleChest) module).getChestAngle();
	}
}