package com.aqupd.grizzlybear;

import com.aqupd.grizzlybear.client.model.GrizzlyBearEntityModel;
import com.aqupd.grizzlybear.client.renderer.GrizzlyBearEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public class MainClient implements ClientModInitializer {

    public static final ModelLayerLocation GRIZZLY_BEAR_LAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("aqupd", "grizzly_bear"), "main");

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(Main.GRIZZLYBEAR, GrizzlyBearEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(GRIZZLY_BEAR_LAYER, GrizzlyBearEntityModel::createBodyLayer);
    }
}