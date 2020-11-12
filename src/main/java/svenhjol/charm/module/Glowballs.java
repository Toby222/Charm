package svenhjol.charm.module;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.handler.RegistryHandler;
import svenhjol.charm.base.iface.Module;
import svenhjol.charm.block.GlowballBlobBlock;
import svenhjol.charm.client.GlowballsClient;
import svenhjol.charm.entity.GlowballEntity;
import svenhjol.charm.item.GlowballItem;

@Module(mod = Charm.MOD_ID, description = "Glowballs can be thrown to produce a light source where they impact.")
public class Glowballs extends CharmModule {
    public static Identifier ID = new Identifier(Charm.MOD_ID, "glowball");
    public static GlowballItem GLOWBALL_ITEM;
    public static GlowballBlobBlock GLOWBALL_BLOCK;
    public static EntityType<GlowballEntity> GLOWBALL;
    public static GlowballsClient client;

    @Override
    public void register() {
        GLOWBALL_BLOCK = new GlowballBlobBlock(this);
        GLOWBALL_ITEM = new GlowballItem(this);

        GLOWBALL = RegistryHandler.entity(ID, EntityType.Builder.<GlowballEntity>create(GlowballEntity::new, SpawnGroup.MISC)
            .maxTrackingRange(4)
            .trackingTickInterval(10)
            .setDimensions(0.25F, 0.25F)
            .build(ID.getPath()));
    }

    @Override
    public void clientRegister() {
        client = new GlowballsClient(this);
    }

    @Override
    public void clientInit() {
        BlockRenderLayerMap.INSTANCE.putBlock(GLOWBALL_BLOCK, RenderLayer.getTranslucent());
    }
}
