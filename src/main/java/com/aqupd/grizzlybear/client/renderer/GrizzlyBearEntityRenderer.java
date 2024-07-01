package com.aqupd.grizzlybear.client.renderer;

import com.aqupd.grizzlybear.MainClient;
import com.aqupd.grizzlybear.client.model.GrizzlyBearEntityModel;
import com.aqupd.grizzlybear.entities.GrizzlyBearEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class GrizzlyBearEntityRenderer extends MobRenderer<GrizzlyBearEntity, GrizzlyBearEntityModel<GrizzlyBearEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("aqupd", "textures/entity/grizzly_bear.png");

    public GrizzlyBearEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new GrizzlyBearEntityModel<>(context.bakeLayer(MainClient.GRIZZLY_BEAR_LAYER)), 0.9F);
    }

    public @NotNull ResourceLocation getTextureLocation(GrizzlyBearEntity polarBearEntity) {
        return TEXTURE;
    }

    protected void scale(GrizzlyBearEntity grizzlyBearEntity, PoseStack matrixStack, float f) {
        matrixStack.scale(1.2F, 1.2F, 1.2F);
        super.scale(grizzlyBearEntity, matrixStack, f);
    }
}
