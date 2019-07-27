package svenhjol.charm;

import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import svenhjol.charm.automation.CharmAutomation;
import svenhjol.charm.base.CharmModLoader;
import svenhjol.charm.base.ClientProxy;
import svenhjol.charm.base.ServerProxy;
import svenhjol.charm.brewing.CharmBrewing;
import svenhjol.charm.crafting.CharmCrafting;
import svenhjol.charm.tweaks.CharmTweaks;
import svenhjol.charm.world.CharmWorld;
import svenhjol.meson.Feature;
import svenhjol.meson.Meson;
import svenhjol.meson.iface.IMesonSidedProxy;

@Mod(Charm.MOD_ID)
public class Charm
{
    public static final String MOD_NAME = "Charm";
    public static final String MOD_ID = "charm";
    public static final String VERSION = "1.2.3";

    public static IMesonSidedProxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);

    public Charm()
    {
        Meson.init();

        CharmModLoader.INSTANCE.registerModLoader(MOD_ID).setup(
            new CharmAutomation(),
            new CharmBrewing(),
            new CharmCrafting(),
            new CharmTweaks(),
            new CharmWorld()
        );

        // add the Charm modloader to event bus
//        FMLJavaModLoadingContext.get().getModEventBus().addListener();
    }

    public static boolean hasFeature(Class<? extends Feature> feature)
    {
        return CharmModLoader.INSTANCE.enabledFeatures.contains(feature);
    }
}