package ladysnake.ratsmischief.client;

import ladysnake.ratsmischief.client.render.entity.RatEntityRenderer;
import ladysnake.ratsmischief.common.Mischief;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

public class MischiefClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(Mischief.RAT, RatEntityRenderer::new);

        // model predicates
        FabricModelPredicateProviderRegistry.register(new Identifier(Mischief.MODID + ":filled"), (itemStack, world, livingEntity, seed) -> itemStack.getOrCreateSubTag(Mischief.MODID).getFloat("filled"));

        BlockRenderLayerMap.INSTANCE.putBlock(Mischief.RAT_NEST, RenderLayer.getCutout());
    }
}
